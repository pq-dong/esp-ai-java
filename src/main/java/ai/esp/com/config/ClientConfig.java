package ai.esp.com.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


@Configuration
@Data
public class ClientConfig {

    @Value("${spring.profiles.active}")
    private String active;

    @Value("${server.port}")
    private Integer port;

    @Value("${client.llmQaNumber}")
    private Integer llmQaNumber;

    @Value("${client.iat.server}")
    private String iatServer;

    @Value("${client.iat.appid}")
    private String iatAppid;

    @Value("${client.iat.appSecret}")
    private String iatAppSecret;

    @Value("${client.iat.apiKey}")
    private String iatApiKey;

    @Value("${client.iat.vadEos}")
    private Integer iatVadEos;

    @Value("${client.tts.server}")
    private String ttsServer;

    @Value("${client.tts.appid}")
    private String ttsAppid;

    @Value("${client.tts.accessToken}")
    private String ttsAccessToken;

    @Value("${client.tts.rate}")
    private Integer ttsRate;

    @Value("${client.tts.voiceType}")
    private String ttsVoiceType;

    @Value("${client.tts.speedRatio}")
    private Double ttsSpeedRatio;

    @Value("${client.llm.server}")
    private String llmServer;

    @Value("${client.llm.appKey}")
    private String llmAppKey;

    @Value("${client.llm.llm}")
    private String llmModel;

    @Value("${client.llm_init_messages}")
    private String llmInitMessages;

    @Value("${client.cacheTTSNumber}")
    private Integer cacheTTSNumber;

}