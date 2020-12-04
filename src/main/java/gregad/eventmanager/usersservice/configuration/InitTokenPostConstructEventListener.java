package gregad.eventmanager.usersservice.configuration;

import gregad.eventmanager.usersservice.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @author Greg Adler
 */
@Component
public class InitTokenPostConstructEventListener implements ApplicationListener<ContextRefreshedEvent> {
    public static boolean isExecuted;
    @Autowired
    private ApplicationContext context;
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if (!isExecuted) {
            isExecuted=true;
            UserServiceImpl bean = context.getBean(UserServiceImpl.class);
            bean.initToken();
        }
    }
}
