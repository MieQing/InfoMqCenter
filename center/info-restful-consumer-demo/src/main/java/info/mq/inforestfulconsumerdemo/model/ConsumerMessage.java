package info.mq.inforestfulconsumerdemo.model;

import java.util.List;

public class ConsumerMessage {
    private String topicName;
    private String message;

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "{" +
                "topicName='" + topicName + '\'' +
                ", message=" + message +
                '}';
    }
}
