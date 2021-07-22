package info.mq.admincenter.model;

import java.sql.Timestamp;

public class Center_Consumer_Task {
    private int id;
    private int groupId;//消费者组id
    private String taskCode;//任务编码
    private String getType;//获取方式 1 单条 2批量
    private int status;//状态 0 禁用 1 启用
    private String operateType;//调用类型 restful dubbo
    private String operateUrl;//调用地址
    private int batchNumber;//每次poll拉取数
    private int operateOutTime;//调用超时时间（ms）
    private int hea_int_ms;
    private int ses_timeout_ms;
    private int max_poll_ms;
    private Timestamp modifyTime;
    private String execCode; //执行器节点

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getTaskCode() {
        return taskCode;
    }

    public void setTaskCode(String taskCode) {
        this.taskCode = taskCode;
    }

    public String getGetType() {
        return getType;
    }

    public void setGetType(String getType) {
        this.getType = getType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public String getOperateUrl() {
        return operateUrl;
    }

    public void setOperateUrl(String operateUrl) {
        this.operateUrl = operateUrl;
    }

    public int getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(int batchNumber) {
        this.batchNumber = batchNumber;
    }

    public int getOperateOutTime() {
        return operateOutTime;
    }

    public void setOperateOutTime(int operateOutTime) {
        this.operateOutTime = operateOutTime;
    }

    public int getHea_int_ms() {
        return hea_int_ms;
    }

    public void setHea_int_ms(int hea_int_ms) {
        this.hea_int_ms = hea_int_ms;
    }

    public int getSes_timeout_ms() {
        return ses_timeout_ms;
    }

    public void setSes_timeout_ms(int ses_timeout_ms) {
        this.ses_timeout_ms = ses_timeout_ms;
    }

    public int getMax_poll_ms() {
        return max_poll_ms;
    }

    public void setMax_poll_ms(int max_poll_ms) {
        this.max_poll_ms = max_poll_ms;
    }

    public Timestamp getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Timestamp modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getExecCode() {
        return execCode;
    }

    public void setExecCode(String execCode) {
        this.execCode = execCode;
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", groupId=" + groupId +
                ", taskCode='" + taskCode + '\'' +
                ", getType='" + getType + '\'' +
                ", status=" + status +
                ", operateType='" + operateType + '\'' +
                ", operateUrl='" + operateUrl + '\'' +
                ", batchNumber=" + batchNumber +
                ", operateOutTime=" + operateOutTime +
                ", hea_int_ms=" + hea_int_ms +
                ", ses_timeout_ms=" + ses_timeout_ms +
                ", max_poll_ms=" + max_poll_ms +
                ", modifyTime=" + modifyTime +
                ", execCode='" + execCode + '\'' +
                '}';
    }
}

