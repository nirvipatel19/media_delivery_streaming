
package nirvi.project.media_delivery_streaming;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;

@EnableCaching
@Slf4j
@SpringBootApplication
public class S3streaming {

    public static void main(String[] args) {
        SpringApplication myapp = new SpringApplication(S3streaming.class);
        ApplicationContext context = myapp.run(args);
        log.info("Application started - {}", context.getEnvironment().getActiveProfiles());
    }
}