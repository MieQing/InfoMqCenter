package info.mq.infoconsumer.erroralarm;


import com.google.common.collect.Lists;
import info.mq.infoconsumer.erroralarm.thread.ThreadErrorPool;
import info.mq.infoconsumer.model.ConsumerResult;
import info.mq.infoconsumer.model.Consumer_Task;
import info.mq.infoconsumer.service.DoErrorAlarmService;
import info.mq.infocore.model.MessageInfo;
import info.mq.infocore.thread.ListenerThreadPool;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;

public class ErrorOperate {

    private static List<DoErorAlarm> alarmList;

    private static ErrorOperate instance=new ErrorOperate();

    private static volatile ThreadErrorPool threadErrorPool = null;

    private ErrorOperate(){

    }

    public static ErrorOperate getInstance(){
        return  instance;
    }

    public static void setAlarmList(List<DoErorAlarm> list){
        alarmList=list;
    }

    private static ThreadErrorPool getPool(){
        if(threadErrorPool==null){
            synchronized (ErrorOperate.class){
                if(threadErrorPool==null){
                    threadErrorPool=new ThreadErrorPool();
                }
            }
        }
        return threadErrorPool;
    }

    /*
     * 执行发送消息命令
     * */
    public static void doAlarm(ConsumerResult consumerResult, Consumer_Task task){
        if (alarmList!=null && alarmList.size()>0) {
            for (DoErorAlarm alarm: alarmList) {
                try {
                    //在调用邮件等信息通知时会出现阻塞，所以此处用后台线程池去处理，尽早释放去处理下个任务
                    getPool().invoker(consumerResult,task,alarm);
                } catch (Exception e) {
                }
            }
        }
    }

}
