package ai.esp.com.service.iat;

import ai.esp.com.config.GlobalConfig;
import ai.esp.com.data.DeviceSession;
import ai.esp.com.data.Send2ClientMessage;
import ai.esp.com.service.IATService;
import ai.esp.com.service.tts.TTSVolcengineSerivce;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.websocket.Session;
import java.io.IOException;

@Slf4j
@Service
public class IATCommonService {

    @Resource
    private GlobalConfig globalConfig;

    @Resource
    private TTSVolcengineSerivce ttsVolcengineSerivce;

    @Resource
    private IATService iatService;

    // 开始连接 iat 服务的回调
    public void connectServerBeforeCb(String deviceId) {
        DeviceSession deviceSession = globalConfig.getDevices().get(deviceId);
        if (deviceSession == null) {
            return;
        }
        // 设置设备会话的 iat_server_connect_ing 为 true
        deviceSession.getPlatformStatus().setIatServerConnectIng(true);
    }

    // 连接 iat 服务后的回调
    public void connectServerCb(String deviceId, boolean connected) throws IOException {
        DeviceSession deviceSession = globalConfig.getDevices().get(deviceId);
        if (deviceSession == null) {
            return;
        }

        if (connected) {
            // 更新设备会话的状态
            deviceSession.getPlatformStatus().setIatServerConnected(true);
            deviceSession.getPlatformStatus().setIatServerConnectIng(false);
            // 清空缓存
            deviceSession.setAsrBufferCache(new byte[0]);
            // 执行连接成功后的回调逻辑
            deviceSession.setStarted(true);
            if (deviceSession.getWs() != null) {
                Send2ClientMessage send2ClientMessage = Send2ClientMessage.builder().type("session_status")
                        .status("iat_start")
                        .build();
                deviceSession.getWs().getBasicRemote().sendText(JSONUtil.toJsonStr(send2ClientMessage));
            }
        } else {
            deviceSession.getPlatformStatus().setIatServerConnected(false);
            deviceSession.getPlatformStatus().setIatServerConnectIng(false);
            deviceSession.setIatWs(null);

            if (deviceSession.getWs() != null) {
                Send2ClientMessage send2ClientMessage = Send2ClientMessage.builder().type("session_status")
                        .status("iat_end")
                        .build();
                deviceSession.getWs().getBasicRemote().sendText(JSONUtil.toJsonStr(send2ClientMessage));
            }
        }
    }

    // 记录 tts 服务对象
    public void logWSServer(String deviceId, Session wsServer) {
        DeviceSession deviceSession = globalConfig.getDevices().get(deviceId);
        if (deviceSession == null) {
            return;
        }
        deviceSession.setIatWs(wsServer);
        deviceSession.getPlatformStatus().setIatServerConnected(false);
    }

    // 记录发送音频数据给服务的函数
    public void logSendAudio(String deviceId, Runnable sendFn) {
        DeviceSession deviceSession = globalConfig.getDevices().get(deviceId);
        if (deviceSession == null) {
            return;
        }
        deviceSession.setSendPcm(sendFn);
    }

    // 服务发生错误时调用
    public void iatServerErrorCb(String deviceId, String err, String code) throws IOException {
        DeviceSession deviceSession = globalConfig.getDevices().get(deviceId);
        if (deviceSession == null) {
            return;
        }
        log.error("IAT error: " + err);
        // 调用错误捕获方法
        Send2ClientMessage send2ClientMessage = Send2ClientMessage.builder().type("error")
                .at("IAT")
                .code(code != null ? code : "102")
                .message(err)
                .build();
        deviceSession.getWs().getBasicRemote().sendText(JSONUtil.toJsonStr(send2ClientMessage));

        deviceSession.setIatWs(null);
        deviceSession.getPlatformStatus().setIatServerConnected(false);

        // 调用 TTS 服务
        ttsVolcengineSerivce.TTS_FN(deviceId, "语音识别发生了错误:" + err, true, true);
    }

    // 当 IAT 服务连接成功了，但长时间不响应时
    public void serverTimeOutCb(String deviceId) throws IOException {
        DeviceSession deviceSession = globalConfig.getDevices().get(deviceId);
        if (deviceSession == null) {
            return;
        }

        // 获取相关信息
        Session iatWs = deviceSession.getIatWs();
        boolean iatServerConnected = deviceSession.getPlatformStatus().isIatServerConnected();
        //todo: 可能存在循环依赖
        iatService.cancelIatEndFrameTimer(deviceId);

        if (!iatServerConnected) return;

        if (iatWs != null) {
            iatWs.close();
        }
        connectServerCb(deviceId, false);
        log.info("-> IAT 服务响应超时，会话结束");
        deviceSession.setStarted(true);
    }

    // IAT 静默时间达到后触发
    public void iatEndQueueCb(String deviceId, Runnable fn) {
        DeviceSession deviceSession = globalConfig.getDevices().get(deviceId);
        if (deviceSession == null) {
            return;
        }
        deviceSession.setStopped(true);
        deviceSession.setIatEndQueue(fn);
    }
}