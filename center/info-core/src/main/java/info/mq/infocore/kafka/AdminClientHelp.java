package info.mq.infocore.kafka;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class AdminClientHelp {
    private static Logger logger = LoggerFactory.getLogger(AdminClientHelp.class);

    private static AdminClientHelp instance=new AdminClientHelp();

    private volatile AdminClient adminClient;

    private AdminClientHelp(){ }

    public static AdminClientHelp getInstance(){ return instance;}

    public AdminClient getAdminClient(String address){
        if(adminClient==null){
            synchronized (AdminClientHelp.class){
                if(adminClient==null){
                    Map<String, Object> props = new HashMap<>();
                    props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, address);
                    adminClient = AdminClient.create(props);
                }
            }
        }
        return adminClient;
    }

}
