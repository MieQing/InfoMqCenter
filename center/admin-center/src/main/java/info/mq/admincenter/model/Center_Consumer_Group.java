package info.mq.admincenter.model;

public class Center_Consumer_Group {
    private int id;
    private String topicName;//任务归属topic
    private String groupName;//消费者组
    private String offsetReset;//OffsetReset
    private String par_assign_strategy;//分区策略
    private String description;//描述
    private String notifier;//通知人

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getOffsetReset() {
        return offsetReset;
    }

    public void setOffsetReset(String offsetReset) {
        this.offsetReset = offsetReset;
    }

    public String getPar_assign_strategy() {
        return par_assign_strategy;
    }

    public void setPar_assign_strategy(String par_assign_strategy) {
        this.par_assign_strategy = par_assign_strategy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNotifier() {
        return notifier;
    }

    public void setNotifier(String notifier) {
        this.notifier = notifier;
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", topicName='" + topicName + '\'' +
                ", groupName='" + groupName + '\'' +
                ", offsetReset='" + offsetReset + '\'' +
                ", par_assign_strategy='" + par_assign_strategy + '\'' +
                ", description='" + description + '\'' +
                ", notifier='" + notifier + '\'' +
                '}';
    }
}
