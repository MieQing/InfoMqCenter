package info.mq.infoconsumer.model;


public class Error_Message {
    private int id;
    private String topicName;  //主题
    private String mesBody;  //消息体
    private int status;   //状态0 未处理  1已处理  2 忽略
    private String taskCode; //具体消费任务的编码
    private String errorMsg; //返回的错误信息

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

    public String getMesBody() {
        return mesBody;
    }

    public void setMesBody(String mesBody) {
        this.mesBody = mesBody;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTaskCode() {
        return taskCode;
    }

    public void setTaskCode(String taskCode) {
        this.taskCode = taskCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", topicName='" + topicName + '\'' +
                ", mesBody='" + mesBody + '\'' +
                ", status=" + status +
                ", taskCode='" + taskCode + '\'' +
                ", errorMsg='" + errorMsg + '\'' +
                '}';
    }
}
