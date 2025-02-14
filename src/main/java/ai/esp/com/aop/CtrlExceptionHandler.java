package ai.esp.com.aop;

import ai.esp.com.data.ResponseMessage;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.ServletException;

@RestControllerAdvice(annotations = RestController.class)
public class CtrlExceptionHandler {

    @ExceptionHandler({Exception.class})
    public ResponseMessage<String> exceptionHandler(Exception e) {
        return ResponseMessage.failedData(ResponseMessage.Type.SERVER_ERROR, e.toString());
    }

    /**
     * 处理ServletException类的异常
     */
    @ExceptionHandler({ServletException.class})
    public ResponseMessage<String> exceptionHandler(ServletException e) {
        if (e instanceof MissingServletRequestParameterException) {
            return ResponseMessage.failedData(ResponseMessage.Type.MISSING_PARAM, e.toString());
        }
        return ResponseMessage.failedData(ResponseMessage.Type.SERVER_ERROR, e.toString());
    }

}
