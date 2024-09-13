package gutek.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * Configuration class for message resources.
 *
 * This class provides a bean definition for {@link MessageSource}, which is used for internationalization
 * (i18n) in Spring applications. It configures a {@link ResourceBundleMessageSource} that loads messages
 * from property files (e.g., `messages.properties`), and it sets the default character encoding to UTF-8.
 */
@Configuration
public class MessageConfig {

    /**
     * Defines the {@link MessageSource} bean.
     *
     * This bean loads message resources from files with the base name "messages" and sets the default
     * character encoding to "UTF-8". The base name refers to property files such as `messages.properties`,
     * `messages_en_US.properties`, etc., which contain key-value pairs for internationalized messages.
     *
     * @return a {@link ResourceBundleMessageSource} that is configured to load messages from the "messages"
     *         resource bundle with UTF-8 encoding.
     */
    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
//    @Bean(name="messageSource")
//    public ReloadableResourceBundleMessageSource messageSource() {
//        ReloadableResourceBundleMessageSource resource = new ReloadableResourceBundleMessageSource();
//        resource.setBasename("classpath:/messages");
//        resource.setDefaultEncoding("UTF-8");
//        resource.setUseCodeAsDefaultMessage(true);
//        resource.setFallbackToSystemLocale(false);
//        return resource;
//    }
}
