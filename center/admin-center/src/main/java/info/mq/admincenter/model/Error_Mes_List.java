package info.mq.admincenter.model;

import java.util.List;

public class Error_Mes_List {
    List<Error_Message> mes;
    int count;

    public List<Error_Message> getMes() {
        return mes;
    }

    public void setMes(List<Error_Message> mes) {
        this.mes = mes;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "{" +
                "mes=" + mes +
                ", count=" + count +
                '}';
    }
}
