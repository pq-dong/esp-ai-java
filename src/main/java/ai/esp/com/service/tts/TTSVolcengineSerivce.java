package ai.esp.com.service.tts;

import ai.esp.com.config.ClientConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class TTSVolcengineSerivce {

    @Resource
    private ClientConfig clientConfig;

    public void TTS_FN(String deviceId, String message, boolean reRecord, boolean pauseInputAudio){
        String iatApiKey = clientConfig.getIatApiKey();
        String iatApiSecret = clientConfig.getIatAppSecret();
        String iatAppId = clientConfig.getIatAppid();
        if (StringUtils.isEmpty(iatAppId) || StringUtils.isEmpty(iatApiSecret) || StringUtils.isEmpty(iatApiKey)){
            log.error("请给 IAT 配置需要的参数");
            return;
        }



    }
}
