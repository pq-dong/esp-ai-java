package ai.esp.com.aop;

import ai.esp.com.data.CurrentRequest;
import ai.esp.com.data.DeviceInfo;
import ai.esp.com.data.ResponseMessage;
import cn.hutool.http.ContentType;
import cn.hutool.json.JSONUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@Component
@Slf4j
public class UserInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handleObject) {
        if (!(handleObject instanceof HandlerMethod)) {
            return true;
        }
        if (request.getMethod().equals(HttpMethod.OPTIONS.name())) {
            return true;
        }
        CurrentRequest.init();
        String requestURI = request.getRequestURI();
        // 直接跳过不需要处理的uri
        if (requestURI.startsWith("/api/debug") || requestURI.endsWith(".js") || requestURI.endsWith(".html") ||
                requestURI.endsWith(".ico") || requestURI.endsWith(".png") || requestURI.endsWith(".css") || requestURI.equals("/error")) {
            return true;
        } else {
            String token = request.getHeader("token");
            //方便测试添加，正式发布要去掉
            if (StringUtils.isEmpty(token)){
                token = "text";
            }
            if (StringUtils.isEmpty(token)) {
                return forbid(response, requestURI, ResponseMessage.Type.NEED_LOGIN);
            } else {
                CurrentRequest.setDevice(DeviceInfo.builder().token(token).build());
                return true;
            }
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        CurrentRequest.remove(CurrentRequest.KEY_DEVICE);
    }

    @SneakyThrows
    private boolean forbid(HttpServletResponse response, String uri, ResponseMessage.Type type) {
        StringBuilder content = new StringBuilder();
        content.append("无权访问").append(uri);
        responseWriteForbiddenContent(response, ResponseMessage.failedData(type, content.toString()));
        return false;
    }

    private void responseWriteForbiddenContent(HttpServletResponse response,
                                               ResponseMessage responseMessage) throws IOException {
        response.setContentType(ContentType.JSON.toString());
        byte[] data = JSONUtil.toJsonStr(responseMessage).getBytes();
        response.setContentLength(data.length);
        OutputStream out = response.getOutputStream();
        out.write(data);
        out.close();
    }
}
