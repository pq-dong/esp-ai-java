package ai.esp.com.service;

import ai.esp.com.config.ClientConfig;
import ai.esp.com.config.GlobalConfig;
import ai.esp.com.data.DeviceSession;
import ai.esp.com.service.iat.XunFeiIATService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

@Slf4j
@Service
public class IATService {

    @Resource
    private XunFeiIATService xunFeiIATService;

    @Resource
    private GlobalConfig globalConfig;

    @Resource
    private ClientConfig clientConfig;

    public void start(String deviceId){
        log.info("iat 开始请求语音识别");
        DeviceSession deviceSession = globalConfig.getDevices().get(deviceId);
        if (clientConfig.getIatServer().equals("xun_fei")){
            xunFeiIATService.IAT_FN(
                    deviceSession.getSessionId(),
                    deviceId
            );
        }
    }


    //是个定时器，要清除定时器中的内容
    public void cancelIatEndFrameTimer(String deviceId){
    }

    public void iatEnd(Map<String, Object> commArgs){
        return;
    }
}
