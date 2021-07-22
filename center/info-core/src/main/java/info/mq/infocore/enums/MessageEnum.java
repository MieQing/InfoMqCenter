package info.mq.infocore.enums;

public enum MessageEnum {
    MAIL("Mail");

    private String type;
    private MessageEnum (String title) {
        this.type = title;
    }

    public void setType(String title) {
        this.type = title;
    }
    public String getType() {
        return type;
    }
}
