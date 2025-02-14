package ai.esp.com.controller;

import ai.esp.com.data.DeviceInfo;
import ai.esp.com.data.ResponseMessage;
import ai.esp.com.service.DeviceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/api/device")
public class DeviceController {

    @Resource
    private DeviceService deviceService;

    /*
    * 测试接口连通性
    * */
    @GetMapping("/info")
    public ResponseMessage<DeviceInfo> info(){
        return ResponseMessage.successData(ResponseMessage.Type.SUCCESS, deviceService.info());
    }

}
