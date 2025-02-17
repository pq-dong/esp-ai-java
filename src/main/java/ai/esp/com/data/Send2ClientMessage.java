package ai.esp.com.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Send2ClientMessage {

    private String type;

    private String sessionId;

    private String at;

    private String status;

    private String code;

    private String message;
}
