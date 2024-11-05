package gutek.utils;

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Utility class for accessing Spring's application context.
 * This class allows other components to fetch Spring-managed beans from the context.
 * It implements {@link ApplicationContextAware} to capture the {@link ApplicationContext}.
 */
@Component
@AllArgsConstructor
public class SpringContext implements ApplicationContextAware {

    /**
     * The Spring application context, which provides access to all Spring-managed beans.
     */
    private ApplicationContext context;

    /**
     * This method is called by the Spring container to inject the {@link ApplicationContext}
     * into this bean.
     *
     * @param ctx the {@link ApplicationContext} that this object runs in
     */
    @Override
    public void setApplicationContext(ApplicationContext ctx) {
        context = ctx;
    }

    /**
     * Retrieves a Spring-managed bean of the specified class type from the application context.
     *
     * @param <T> the type of the bean to retrieve
     * @param beanClass the class type of the bean to fetch
     * @return an instance of the specified bean class
     */
    public <T> T getBean(Class<T> beanClass) {
        return context.getBean(beanClass);
    }
}
