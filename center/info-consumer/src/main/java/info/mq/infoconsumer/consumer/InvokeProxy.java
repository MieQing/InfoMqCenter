package info.mq.infoconsumer.consumer;

import info.mq.infoconsumer.erroralarm.ErrorOperate;
import info.mq.infoconsumer.model.ConsumerResult;
import info.mq.infoconsumer.model.Consumer_Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;


/*
* 调用函数的动态代理类 用于在调用结束之后用于对执行结果做处理
* */
public class InvokeProxy implements InvocationHandler {
    private static Logger logger = LoggerFactory.getLogger(InvokeProxy.class);
    Invoked ik;

    public InvokeProxy(Invoked ik) {
        this.ik = ik;
    }

    public void after(Object o,Object[] args) {
        ConsumerResult result=(ConsumerResult)o;
        Consumer_Task task=(Consumer_Task)args[2];
        if(result.getCode()==200)
            logger.info("Consumer success topic={} node={} task={}"
                    ,task.getTopicName(),task.getExecCode(),task.getTaskCode());
        else {
            logger.error("Consumer error!!!!!!!!!! topic={} node={} task={} message={} errorMsg={}"
                    , task.getTopicName(), task.getExecCode(), task.getTaskCode(), result.getMessgae(),result.getErrorMessage());
            //错误信息进行通知
            ErrorOperate.getInstance().doAlarm(result,task);
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object o = method.invoke(ik, args);
        after(o,args);
        return o;
    }
}
