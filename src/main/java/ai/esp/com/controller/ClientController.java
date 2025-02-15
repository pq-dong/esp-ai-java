package ai.esp.com.controller;

import ai.esp.com.data.DeviceSession;
import ai.esp.com.data.ResponseMessage;
import ai.esp.com.service.ClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/api/device")
public class ClientController {

    @Resource
    private ClientService clientService;

    /*
    * 测试接口连通性
    * */
    @GetMapping("/info")
    public ResponseMessage<DeviceSession> info(){
        return ResponseMessage.successData(ResponseMessage.Type.SUCCESS, clientService.info());
    }

}
