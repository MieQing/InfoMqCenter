package info.mq.admincenter.model;

public class Center_Topic {
    private String topicName; //topic
    private String description; //对应topic的描述
    private int partition;//分区数
    private int replicationFactor;//副本数
    private int status;

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPartition() {
        return partition;
    }

    public void setPartition(int partition) {
        this.partition = partition;
    }

    public int getReplicationFactor() {
        return replicationFactor;
    }

    public void setReplicationFactor(int replicationFactor) {
        this.replicationFactor = replicationFactor;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "{" +
                "topicName='" + topicName + '\'' +
                ", description='" + description + '\'' +
                ", partition=" + partition +
                ", replicationFactor=" + replicationFactor +
                ", status=" + status +
                '}';
    }
}
