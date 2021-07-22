package info.mq.admincenter.conf;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/*
* 跨域处理
* */
@Configuration
public class CrossOriginConfig {


    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        //允许所有域的请求
        corsConfiguration.addAllowedOrigin("*");
        //允许请求携带认证信息(cookies)
        corsConfiguration.setAllowCredentials(false);
        //允许所有的请求方法
        corsConfiguration.addAllowedMethod("*");
        //允许所有的请求头
        corsConfiguration.addAllowedHeader("*");
        //允许暴露所有头部信息
        corsConfiguration.addExposedHeader("*");

        //添加映射路径
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);

        //返回新的CorsFilter对象
        return new CorsFilter(urlBasedCorsConfigurationSource);
    }
}