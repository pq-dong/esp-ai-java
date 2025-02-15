package ai.esp.com.service;

import ai.esp.com.config.GlobalConfig;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class MessageService {

    @Resource
    private GlobalConfig globalConfig;

    @Resource
    private ClientService clientService;

    public Map<String, Object> parseUrlParams(String query) {
        Map<String, Object> params = new HashMap<>();
        if (query != null && !query.isEmpty()) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    params.put(keyValue[0], keyValue[1]);
                }
            }
        }
        return params;
    }

    public void handleStringMessage(String deviceId, String message){
        Map<String, Object> commArgs = new HashMap<>();
        commArgs.put("device_id", deviceId);
        log.info("[{}] 收到消息：{}", deviceId, message);

        try {
            // 解析消息（假设消息是 JSON 格式）
            Map<String, Object> messageData = JSONUtil.toBean(message, Map.class);
            String type = (String) messageData.get("type");
            commArgs.put("type", type);

            // 解析消息中的参数
            if (messageData.containsKey("session_id")) commArgs.put("session_id", messageData.get("session_id"));
            if (messageData.containsKey("tts_task_id")) commArgs.put("tts_task_id", messageData.get("tts_task_id"));
            if (messageData.containsKey("stc_time")) commArgs.put("stc_time", messageData.get("stc_time"));
            if (messageData.containsKey("sid")) commArgs.put("sid", messageData.get("sid"));
            if (messageData.containsKey("text")) commArgs.put("text", messageData.get("text"));
            if (messageData.containsKey("success")) commArgs.put("success", messageData.get("success"));
            if (messageData.containsKey("value")) commArgs.put("value", messageData.get("value"));
            if (messageData.containsKey("pin")) commArgs.put("pin", messageData.get("pin"));

            // 根据类型执行不同操作
            switch (type) {
                case "start":
                    clientService.start(commArgs);
                    break;
                case "iat_end":
                    iat_end(commArgs);
                    break;
                case "client_out_audio_ing":
                    client_out_audio_ing_fn(commArgs);
                    break;
                case "client_out_audio_over":
                    client_out_audio_over(commArgs);
                    break;
                case "play_audio_ws_conntceed":
                    play_audio_ws_conntceed(commArgs);
                    break;
                case "tts":
                    String text = (String) commArgs.get("text");
                    G_Instance.tts(deviceId, text);
                    break;
                case "cts_time":
                    cts_time(commArgs);
                    break;
                case "set_wifi_config_res":
                    set_wifi_config_res(commArgs);
                    break;
                case "digitalRead":
                    digitalRead(commArgs);
                    break;
                case "analogRead":
                    analogRead(commArgs);
                    break;
                default:
                    log.warn("未知消息类型：{}", type);
            }
        } catch (Exception e) {
            log.info("消息格式不是string：{}", e.getMessage());
        }
    }

    public void handleByteMessage(String deviceId, byte[] message){
        globalConfig.getDevices().get(deviceId).getWs().getUserProperties().put("isAlive", true);
        audio(commArgs, message);
    }

    public void handlePingMessage(String deviceId){
        globalConfig.getDevices().get(deviceId).getWs().getUserProperties().put("isAlive", true);
    }

    public void handleStopMessage(String deviceId){
        return;
    }
}
