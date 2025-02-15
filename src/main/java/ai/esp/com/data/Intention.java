package ai.esp.com.data;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Intention {

    private List<String> key;

    private String instruct;

    private String message;
}
