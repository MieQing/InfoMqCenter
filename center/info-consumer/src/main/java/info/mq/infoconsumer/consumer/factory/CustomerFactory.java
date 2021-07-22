package info.mq.infoconsumer.consumer.factory;

import info.mq.infoconsumer.consumer.InvokeProxy;
import info.mq.infoconsumer.consumer.Invoked;
import info.mq.infoconsumer.consumer.impl.RestfulBatchInvoke;
import info.mq.infoconsumer.consumer.impl.RestfulOldInvoke;
import info.mq.infoconsumer.model.Consumer_Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;


/*
* 工厂类，封装需要调用的具体执行方法
* */
@Component
public  class CustomerFactory {

    private static Logger logger = LoggerFactory.getLogger(CustomerFactory.class);

    @Autowired
    LoadBalancerClient loadBalancerClient;

    public Invoked Create(Consumer_Task task) {

        String operateType=task.getOperateType();

        //生成需要执行的url
        String url=transferUrl(task);

        Invoked invoked=null;
        if("1".equals(task.getGetType()))//兼容老版本，新版本不会用这个了
            invoked=new RestfulOldInvoke(url);
        else
            switch (operateType)
            {
                case "spring.cloud.eureka":
                case "restful":
                    invoked=new RestfulBatchInvoke(url);

            }

        //此处修改为动态代理，用于调用接口之后如果出错等的后续任务
        Invoked inv = (Invoked) Proxy.newProxyInstance(invoked.getClass().getClassLoader(),
                new Class[]{Invoked.class},
                new InvokeProxy(invoked)
        );
        return inv;
    }


    public String transferUrl(Consumer_Task task){
        String url=task.getOperateUrl();
        String operateType=task.getOperateType();
        switch (operateType){
            case "spring.cloud.eureka"://基于eurkea注册中心的微服务
                url=getEurekaUrl(url);
                break;
            default:
                break;
        }

        return url;

    }

    public String getEurekaUrl(String url){
        try {
            String[] urlParams = url.split("\\|");

            ServiceInstance instance = loadBalancerClient.choose(urlParams[0]);

            url = "http://" + instance.getHost() + ":" + instance.getPort() + "" + urlParams[1];

            logger.info("getEurekaUrl transfer url:"+url);
        }
        catch (Exception e){
            logger.error("getEurekaUrl error {}",e);
        }

        return url;
    }


}
