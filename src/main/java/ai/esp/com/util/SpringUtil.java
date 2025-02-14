package ai.esp.com.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContextParam) throws BeansException {
        applicationContext = applicationContextParam;
    }

    //通过名称获取bean
    public static Object getBean(String beanName) {
        return applicationContext.getBean(beanName);
    }

    //通过类型获取bean
    public <T> T getBean(Class<T> tClass) {
        return applicationContext.getBean(tClass);
    }

    //通过类型和名称获取bean
    public static <T> T getBean(String beanName, Class<T> tClass) {
        return applicationContext.getBean(beanName, tClass);
    }
}