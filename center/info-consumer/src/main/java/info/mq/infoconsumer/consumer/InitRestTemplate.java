package info.mq.infoconsumer.consumer;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/*
* 初始化restTemplate
* */
public class InitRestTemplate {
    public int outOfTime;
    public InitRestTemplate(int outOfTime){
        this.outOfTime=outOfTime;
    }

    public RestTemplate getRestTemplate(){
        // 超时时间设置
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(outOfTime);// 从主机读取数据超时
        factory.setConnectTimeout(10 * 1000); // 连接到主机超时 10S超时

        RestTemplate restTemplate=new RestTemplate(factory);

        //RestTemplate设置编码UTF-8
        FormHttpMessageConverter fc = new FormHttpMessageConverter();
        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        List<HttpMessageConverter<?>> partConverters = new ArrayList<HttpMessageConverter<?>>();
        partConverters.add(stringConverter);
        partConverters.add(new ResourceHttpMessageConverter());
        partConverters.add(new MappingJackson2HttpMessageConverter());
        fc.setPartConverters(partConverters);
        restTemplate.getMessageConverters().addAll(Arrays.asList(fc));
        return restTemplate;
    }

}
