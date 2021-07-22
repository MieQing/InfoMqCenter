package info.mq.inforestfulconsumerdemo.model;

import java.util.List;

public class ConsumerResult {
    private int code; //200 成功
    private String messgae;
    private String errorMessage;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessgae() {
        return messgae;
    }

    public void setMessgae(String messgae) {
        this.messgae = messgae;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "{" +
                "code=" + code +
                ", messgae=" + messgae +
                ", errorMessage=" + errorMessage +
                '}';
    }
}
