package info.mq.admincenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan({"info.mq"})
public class AdminCenterApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(AdminCenterApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(
            SpringApplicationBuilder builder)
    {
        return  builder.sources(this.getClass());
    }
}
