package ai.esp.com.controller;

import ai.esp.com.data.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/debug")
public class DebugController {

    /*
    * 测试接口连通性
    * */
    @GetMapping("/test")
    public ResponseMessage<String> test(@RequestParam String text){
        log.info("收到测试请求，测试参数:{}", text);
        return ResponseMessage.successData(ResponseMessage.Type.SUCCESS, text);
    }

}
