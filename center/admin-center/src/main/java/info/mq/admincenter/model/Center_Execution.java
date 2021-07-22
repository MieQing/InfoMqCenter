package info.mq.admincenter.model;



public class Center_Execution {
    private int id;

    //当前执行器节点运行的IP地址 无实际用处，仅仅用于参考
    private String address;

    //执行器节点的名称
    private String name;

    //状态 0-禁用 1-启用
    private int status;

    //执行器的code，唯一标识符，次code会在ZK中建立节点
    private String code;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", address='" + address + '\'' +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", code='" + code + '\'' +
                '}';
    }
}
