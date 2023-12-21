package zone.richardli.datahub.component;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Component
public class ThreadPoolService {

    @Bean
    public ThreadPoolExecutor executor() {
        return (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
    }

}
