package info.mq.admincenter.model;

import java.sql.Timestamp;

public class Error_Message {
    private int id;
    private String topicName;
    private String mesBody;
    private Timestamp createTime;
    private int status;
    private String taskCode;
    private String errorMsg;

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

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
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
                ", createTime=" + createTime +
                ", status=" + status +
                ", taskCode='" + taskCode + '\'' +
                ", errorMsg='" + errorMsg + '\'' +
                '}';
    }
}
