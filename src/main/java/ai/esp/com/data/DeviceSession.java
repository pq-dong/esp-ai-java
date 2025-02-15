package ai.esp.com.data;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.websocket.Session;
import java.util.List;
import java.util.Map;
import java.util.Queue;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeviceSession {

    private String token;

    private boolean started;

    private boolean stopped;

    private Session ws;

    private Map<String, Object> userConfig;

    private boolean firstSession;

    private List<LLMMessage> llmHistories;

    private Map<String, Object> ttsList;

    private List<Object> awaitOutTTS;

    private Map<String, Object> clientParams;

    private String version;

    private String deviceId;

    private List<Object> ttsBufferChunkQueue;

    private Integer usedFlow;

    private Map<String, Object> readPinCbs;

}
