package ai.esp.com.service;

import ai.esp.com.config.GlobalConfig;
import ai.esp.com.data.DeviceSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.websocket.CloseReason;
import java.io.IOException;

@Service
@Slf4j
public class ScheduledService {

    @Resource
    private GlobalConfig globalConfig;

    // 定时任务：每分钟检查一次设备是否存活
    @Scheduled(fixedRate = 60000)  // 每 60 秒执行一次
    public void checkDeviceActivity() {
        for (DeviceSession deviceSession : globalConfig.getDevices().values()) {
            if (deviceSession.getWs().isOpen()) {
                Boolean isAlive = Boolean.parseBoolean(deviceSession.getWs().getUserProperties().get("isAlive").toString());
                if (Boolean.FALSE.equals(isAlive)) {
                    log.warn("[{}] 设备掉线，主动关闭连接", deviceSession.getDeviceId());
                    try {
                        deviceSession.getWs().close(new CloseReason(CloseReason.CloseCodes.GOING_AWAY, "Device disconnected"));
                    } catch (IOException e) {
                        log.error("关闭连接时出错：{}", e.getMessage());
                    }
                    globalConfig.getDevices().remove(deviceSession.getDeviceId());
                } else {
                    // 重置 isAlive 状态
                    deviceSession.getWs().getUserProperties().put("isAlive", false);
                    deviceSession.getWs().getAsyncRemote().sendText("ping");  // 发送 ping 消息
                }
            }
        }
    }
}
