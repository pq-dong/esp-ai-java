package ai.esp.com.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TTSService {

    //清除设备队列中的播放队列
    public void clearTTSBufferChunkQueue(String deviceId){

    }

    public void onEnd(Long startAudioTime, Long endTime, int playTime, int playAudioSeek, String event, Long seek){

    }
}
