package ai.esp.com.config;


import ai.esp.com.data.DeviceInfo;
import ai.esp.com.data.Intention;
import lombok.Data;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Data
@Component
public class GlobalConfig {

    @Resource
    private ClientConfig clientConfig;

    private Map<String, DeviceInfo> devices;

    //todo: 这个实例是什么？
    private Object instance;

    private Map<String, String> sessionIds;

    private Integer maxAudioChunkSize;

    //todo: 这个实例怎么用？
    private Object wsServer;

    //从clientConfig中迁移
    private List<Intention> intention;

    private Map<String, Object> cacheTTS;
    public GlobalConfig() {
        this.devices = new HashMap<>();
        this.sessionIds = new HashMap<>();
        this.sessionIds.put("cache_du", "1000");
        this.sessionIds.put("cache_hello", "1001");
        this.sessionIds.put("cache_sleep_reply", "1002");
        this.sessionIds.put("tts_all_end_align", "2000");
        this.sessionIds.put("tts_all_end", "2001");
        this.sessionIds.put("tts_chunk_end", "2002");
        this.maxAudioChunkSize = 1024 * 8;
        Intention intentionInfo = new Intention(Arrays.asList("退下吧","退下"), "__sleep__", "我先退下了，有需要再叫我。");
        this.intention = new ArrayList<>();
        this.intention.add(intentionInfo);
    }
}
