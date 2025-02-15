package ai.esp.com.controller;

import ai.esp.com.config.GlobalConfig;
import ai.esp.com.data.DeviceSession;
import ai.esp.com.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ServerEndpoint(value = "/")
public class EspChannel implements ApplicationContextAware {

    // 全局静态变量，保存 ApplicationContext
    private static ApplicationContext applicationContext;

    private GlobalConfig globalConfig;

    private MessageService messageService;

    private Session session;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        EspChannel.applicationContext = applicationContext;
    }


    // 连接打开时的处理
    @OnOpen
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        // 保存 session 到对象
        this.session = session;
        this.globalConfig = EspChannel.applicationContext.getBean(GlobalConfig.class);
        this.messageService = EspChannel.applicationContext.getBean(MessageService.class);

        Map<String, Object> params = messageService.parseUrlParams(session.getRequestURI().getQuery());
        String deviceId = params.get("device_id").toString(); // 获取 device_id
        String version = params.get("v").toString(); // 获取 device_id
        if (deviceId == null || deviceId.isEmpty()) {
            log.error("设备异常，未读取到 device_id");
            try {
                session.getBasicRemote().sendText("{\"type\": \"error\", \"message\": \"设备异常，未读取到 device_id\", \"code\": \"004\"}");
                session.close();
            } catch (IOException e) {
                log.error("Error closing connection: " + e.getMessage());
            }
            return;
        }

        log.info("[{}] 硬件连接", deviceId);

        // 如果设备已经存在，断电重连
        if (globalConfig.getDevices().containsKey(deviceId)) {
            DeviceSession oldSession = globalConfig.getDevices().get(deviceId);
            try {
                oldSession.getWs().close();
            } catch (IOException e) {
                log.error("Error closing previous session: " + e.getMessage());
            }
            globalConfig.getDevices().remove(deviceId);
        }


        // 保存新的连接信息
        DeviceSession deviceSession = new DeviceSession();
        deviceSession.setStarted(false);
        deviceSession.setStopped(true);
        deviceSession.setWs(session);
        deviceSession.setUserConfig(new HashMap<>());
        deviceSession.setFirstSession(true);
        deviceSession.setLlmHistories(new ArrayList<>());
        deviceSession.setTtsList(new HashMap<>());
        deviceSession.setAwaitOutTTS(new ArrayList<>());
        deviceSession.setClientParams(params);
        deviceSession.setVersion(version);
        //todo: 这里可能要一直在运行中，少一个error_catch没有设置
        deviceSession.setTtsBufferChunkQueue(new ArrayList<>());
        deviceSession.setDeviceId(deviceId);
        deviceSession.setUsedFlow(0);
        deviceSession.setReadPinCbs(new HashMap<>());
        // 填充其他需要的参数，例如 clientParams
        globalConfig.getDevices().put(deviceId, deviceSession);

        session.setMaxIdleTimeout(60000); // 设置最大空闲时间，防止连接过早关闭
        session.getUserProperties().put("isAlive", true);  // 设置 isAlive
        session.getUserProperties().put("device_id", deviceId);  // 设置 device_id
        session.getUserProperties().put("client_params", params);  // 设置 client_params
        log.info("[{}] 设备连接成功", deviceId);

        //处理消息
        session.addMessageHandler(String.class, message -> {
            messageService.handleStringMessage(deviceId, message);
        });
        session.addMessageHandler(byte[].class, message -> {
            messageService.handleByteMessage(deviceId, message);
        });
        session.addMessageHandler(null, message -> {
            messageService.handlePingMessage(deviceId);
        });
    }


    // 连接关闭时的处理
    @OnClose
    public void onClose(CloseReason closeReason) {
        log.info("[{}] 连接断开 device_id={}，reason={}", this.session.getId(), session.getUserProperties().get("device_id"),
                closeReason.getReasonPhrase());
        // 清理设备记录
        String deviceId = (String) session.getUserProperties().get("device_id");
        if (deviceId != null) {
            messageService.handleStopMessage(deviceId);
            globalConfig.getDevices().remove(deviceId);
        }
    }

    // 连接异常时的处理
    @OnError
    public void onError(Throwable throwable) {
        log.error("[{}] 连接异常：{}", this.session.getId(), throwable.getMessage());
        // 异常时关闭连接
        try {
            session.close(new CloseReason(CloseReason.CloseCodes.UNEXPECTED_CONDITION, throwable.getMessage()));
        } catch (IOException e) {
            log.error("Error closing session: " + e.getMessage());
        }
    }
}