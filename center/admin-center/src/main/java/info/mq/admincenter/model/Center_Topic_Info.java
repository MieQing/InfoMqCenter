package info.mq.admincenter.model;

public class Center_Topic_Info {
    private String name;
    private boolean internal;
    private int partition;
    private String leader;
    private String replicas;
    private String isr;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isInternal() {
        return internal;
    }

    public void setInternal(boolean internal) {
        this.internal = internal;
    }

    public int getPartition() {
        return partition;
    }

    public void setPartition(int partition) {
        this.partition = partition;
    }

    public String getLeader() {
        return leader;
    }

    public void setLeader(String leader) {
        this.leader = leader;
    }

    public String getReplicas() {
        return replicas;
    }

    public void setReplicas(String replicas) {
        this.replicas = replicas;
    }

    public String getIsr() {
        return isr;
    }

    public void setIsr(String isr) {
        this.isr = isr;
    }
}
