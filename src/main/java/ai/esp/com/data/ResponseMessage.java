package ai.esp.com.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ResponseMessage
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseMessage<T> {

    private int code;

    private String msg;

    private T data;

    public ResponseMessage(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static <T> ResponseMessage<T> successMessage(Type type) {
        return new ResponseMessage<>(type.code, type.msg, null);
    }

    public static <T> ResponseMessage<T> successData(Type type, T data) {
        return new ResponseMessage<>(type.code, type.msg, data);
    }

    public static <T> ResponseMessage<T> failedMessage(Type type) {
        return new ResponseMessage<>(type.code, type.msg, null);
    }

    public static <T> ResponseMessage<T> failedData(Type type, T data) {
        return new ResponseMessage<>(type.code, type.msg, data);
    }


    public enum Type {
        SUCCESS(0, "success"),
        INVALID_PARAM(1100,  "无效参数"),
        MISSING_PARAM(1101, "缺少参数"),
        REQUEST_FORMAT_ERROR(1102, "请求格式错误"),
        NO_ACCESS(3101, "禁止访问"),
        NEED_LOGIN(3102, "需要登录"),
        SERVER_ERROR(4101, "服务错误"),
        REQUEST_TIMEOUT(4102, "请求超时"),
        REQUEST_FREQUENCY_LIMIT(5101, "请求超出频率限制"),
        THIRD_PARTY_ERROR(5102, "第三方服务错误");

        public final int code;
        public final String msg;

        Type(int code, String message) {
            this.code = code;
            this.msg = message;
        }
    }
}
