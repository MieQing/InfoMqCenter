package info.mq.infocore.model;



public class MessageInfo {
    public String type;//消息类型
    public String msessage;//消息内容
    public String theme;//消息主题
    public String notifier;//通知人 默认此处使用邮箱，可以根据实际情况去修改要传入的参数数据

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMsessage() {
        return msessage;
    }

    public void setMsessage(String msessage) {
        this.msessage = msessage;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getNotifier() {
        return notifier;
    }

    public void setNotifier(String notifier) {
        this.notifier = notifier;
    }

    @Override
    public String toString() {
        return "MessageInfo{" +
                "type='" + type + '\'' +
                ", msessage='" + msessage + '\'' +
                ", theme='" + theme + '\'' +
                ", notifier='" + notifier + '\'' +
                '}';
    }
}
