package info.mq.infoconsumer.model;

public class ResultMsg {
    /**
     * code:查询结果  200 成功  其它失败
     */
    private int code;

    /**
     * message:结果信息
     */
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
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
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
