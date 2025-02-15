package ai.esp.com.service;

import ai.esp.com.data.CurrentRequest;
import ai.esp.com.data.DeviceSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class ClientService {

    public DeviceSession info(){
        return CurrentRequest.getDevice();
    }

    public void start(Map<String, Object> commArgs){

    }
}
