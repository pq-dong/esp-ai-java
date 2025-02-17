package ai.esp.com.service.iat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class XunFeiIATService {

    @Resource
    private IATCommonService iatCommonService;

    public void IAT_FN(String sessionId, String deviceId){

        return;
    }
}
