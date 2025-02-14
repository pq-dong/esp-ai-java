package ai.esp.com.config;

import ai.esp.com.aop.UserInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Slf4j
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Resource
    private UserInterceptor userInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userInterceptor).addPathPatterns("/**")
                .excludePathPatterns("/swagger-resources/**", "/v2/**", "/webjars/**", "/changelog/**", "/swagger-ui.html/**")
        ;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //将templates目录下的CSS、JS文件映射为静态资源，防止Spring把这些资源识别成thymeleaf模版
        registry.addResourceHandler("/templates/**.js").addResourceLocations("classpath:/templates/");
        registry.addResourceHandler("/templates/**.css").addResourceLocations("classpath:/templates/");
        //swagger增加url映射
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

}