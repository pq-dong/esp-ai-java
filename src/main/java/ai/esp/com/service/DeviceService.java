package ai.esp.com.service;

import ai.esp.com.data.CurrentRequest;
import ai.esp.com.data.DeviceInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DeviceService {

    public DeviceInfo info(){
        return CurrentRequest.getDevice();
    }
}
