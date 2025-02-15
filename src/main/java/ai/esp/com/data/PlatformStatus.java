package ai.esp.com.data;

import lombok.Data;

@Data
public class PlatformStatus {

    private boolean playAudioIng;

    private boolean iatServerConnectIng;

    private boolean iatServerConnected;

    private boolean ttsServerConnectIng;

    private boolean ttsServerConnected;

    private boolean llmServerConnectIng;

    private boolean llmServerConnected;

    private boolean clientOutAudioIng;

    private boolean prevPlayAudioIng;

    private Long startAudioTime;

    private boolean playAudioOnEnd;

    private Long playAudioSeek;

    private boolean awaitOutTtsIng;
}
