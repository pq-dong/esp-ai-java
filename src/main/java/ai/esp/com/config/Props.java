package ai.esp.com.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


@Configuration
@Data
public class Props {

    @Value("${spring.profiles.active}")
    private String active;

}