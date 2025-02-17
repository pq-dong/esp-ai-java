package ai.esp.com.service;

import ai.esp.com.config.GlobalConfig;
import ai.esp.com.data.CurrentRequest;
import ai.esp.com.data.DeviceSession;
import ai.esp.com.data.PlatformStatus;
import ai.esp.com.data.Send2ClientMessage;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.websocket.Session;
import java.util.Map;
import java.util.Stack;

@Slf4j
@Service
public class ClientService {

    @Resource
    private GlobalConfig globalConfig;

    @Resource
    private IATService iatService;

    @Resource
    private TTSService ttsService;

    public DeviceSession info() {
        return CurrentRequest.getDevice();
    }

    public void start(String deviceId) {
        try {
            DeviceSession deviceSession = globalConfig.getDevices().get(deviceId);
            if (deviceSession == null) {
                log.error("[" + deviceId + "] start 消息错误：设备会话不存在");
                return;
            }

            Boolean isAlive = Boolean.parseBoolean(globalConfig.getDevices().get(deviceId).getWs().getUserProperties().get("isAlive").toString());

            if (Boolean.FALSE.equals(isAlive)) {
                log.error("[" + deviceId + "] start 消息错误： 设备离线, 将忽略本次唤醒。");
                return;
            }

            // 终止会话
            boolean result = stopSession(deviceId, "打断会话时");
            while (!result) {
                result = stopSession(deviceId, "打断会话时");
            }
            newSession(deviceId);
            // 启动 IAT 服务
            startIAT(deviceId);

        } catch (Exception e) {
            log.error("[" + deviceId + "] start 消息错误： " + e.getMessage());
        }
    }


    private void startIAT(String deviceId) {
        // Check if device is still connected
        DeviceSession deviceSession = globalConfig.getDevices().get(deviceId);
        if (deviceSession == null) return;
        deviceSession.setStarted(true);
        iatService.start(deviceId);
    }

    public boolean stopSession(String deviceId, String at) {
        if (StringUtils.isEmpty(deviceId)) {
            log.error("调用 stop 方法时，请传入 device_id");
            return true;
        }

        // 清空该设备的所有任务
        DeviceSession deviceSession = globalConfig.getDevices().get(deviceId);
        if (deviceSession == null) {
            return true;
        }

        try {
            PlatformStatus status = deviceSession.getPlatformStatus();
            Session ws = deviceSession.getWs();
            // 判断是否有任务进行中
            if (status.isPlayAudioIng() || status.isIatServerConnectIng()
                    || status.isIatServerConnected()
                    || status.isTtsServerConnectIng()
                    || status.isTtsServerConnected()
                    || status.isLlmServerConnectIng()
                    || status.isLlmServerConnected()
                    || status.isClientOutAudioIng()) {
                log.info("收到stop消息，需要打断会话");
                // 停止会话
                if (ws != null) {
                    ws.getBasicRemote().sendText(JSONUtil.toJsonStr(Send2ClientMessage.builder().type("session_stop").build()));
                }

                // 清理设备会话
                deviceSession.setStarted(false);
                deviceSession.setStopped(true);
                deviceSession.setFirstSession(true);

                // 清空音频播放信息
                status.setPlayAudioIng(false);
                status.setPrevPlayAudioIng(false);
                status.setStartAudioTime(null);
                status.setPlayAudioOnEnd(false);
                status.setPlayAudioSeek(0L);


                // 停止定时器
                iatService.cancelIatEndFrameTimer(deviceId);

                // 清空 TTS 缓冲区
                ttsService.clearTTSBufferChunkQueue(deviceId);

                // 计算播放时间
                long endTime = System.currentTimeMillis();
                long playTime = endTime - status.getStartAudioTime();
                if (status.isPlayAudioIng() && status.isPlayAudioOnEnd()) {
                    ttsService.onEnd(status.getStartAudioTime(), endTime, (int) (playTime / 1000),
                            (int) (status.getPlayAudioSeek() + playTime / 1000),
                            "ws_disconnect", status.getPlayAudioSeek());
                }

                // 关闭 WebSocket 连接
                if (deviceSession.getIatWs() != null) {
                    deviceSession.getIatWs().close();
                }
                if (deviceSession.getLlmWs() != null) {
                    deviceSession.getLlmWs().close();
                }

                // 清空 TTS 队列
                for (Map.Entry<String, Object> entry : deviceSession.getTtsList().entrySet()) {
                    try {
                        Session ttsWS = (Session) entry.getValue();
                        if (ttsWS != null) {
                            // 假设 ttsWS 是 WebSocket
                            ttsWS.close();
                        }
                    } catch (Exception e) {
                        log.error("[" + deviceId + "] " + at + " TTS 队列关闭失败");
                    }
                }
                deviceSession.getTtsList().clear();
                return true;
            }
        } catch (Exception e) {
            log.error("[" + deviceId + "] " + at + " 会话打断失败");
        }

        return false;
    }

    public void newSession(String deviceId) {
        if (StringUtils.isEmpty(deviceId)) {
            log.error("调用 newSession 方法时，请传入 device_id");
            return;
        }

        // 获取设备会话
        DeviceSession deviceSession = globalConfig.getDevices().get(deviceId);
        if (deviceSession == null) {
            log.error("用户 newSession 方法时，设备信息为空");
            return;
        }
        try {
            String sessionId = deviceSession.getWs().getId();
            log.info("创建新的session session ID: " + sessionId);

            // 发送 session_start 消息
            // 假设 ws.sendText() 为 WebSocket 发送消息的方法
            if (deviceSession.getWs() != null) {
                Send2ClientMessage send2ClientMessage = Send2ClientMessage.builder().type("session_start")
                        .sessionId(sessionId)
                        .build();
                deviceSession.getWs().getBasicRemote().sendText(JSONUtil.toJsonStr(send2ClientMessage));
            }

            // 初始化用户会话数据
            deviceSession.setSessionId(sessionId);
            deviceSession.setFirstSession(false);
            deviceSession.getPlatformStatus().setIatServerConnected(false);
            deviceSession.setTtsList(new java.util.HashMap<>());
            deviceSession.setAwaitOutTTS(new java.util.ArrayList<>());
            deviceSession.getPlatformStatus().setAwaitOutTtsIng(false);
            deviceSession.getPlatformStatus().setPlayAudioIng(false);
            deviceSession.getPlatformStatus().setStartAudioTime(null);
            deviceSession.getPlatformStatus().setPlayAudioOnEnd(false);
            deviceSession.getPlatformStatus().setPlayAudioSeek(0L);
            deviceSession.setStopped(false);
        } catch (Exception e) {
            log.error("会话创建失败: " + e.getMessage());
            throw new RuntimeException("Failed to create new session", e);
        }
    }

    // 处理 await_out_tts 队列任务
    private void awaitOutTts(String deviceId) {
        DeviceSession deviceSession = globalConfig.getDevices().get(deviceId);
        if (deviceSession == null || !deviceSession.getPlatformStatus().isAwaitOutTtsIng()) return;

        try {
            for (Runnable task : deviceSession.getAwaitOutTTS()) {
                task.run();
                deviceSession.getAwaitOutTTS().remove(0); // 移除队列中的任务
                awaitOutTts(deviceId); // 递归调用以处理下一个任务
            }
            // 如果没有更多任务，设置 awaitOutTtsIng 为 false
            deviceSession.getPlatformStatus().setAwaitOutTtsIng(false);
        } catch (Exception e) {
            log.error("处理 await_out_tts 队列时出现错误: " + e.getMessage());
        }
    }
}
