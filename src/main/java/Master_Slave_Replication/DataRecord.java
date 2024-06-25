package Master_Slave_Replication;

import java.io.Serializable;

public class DataRecord implements Serializable {
    private int id;
    private String data;

    public DataRecord(int id, String data) {
        this.id = id;
        this.data = data;
    }

    public int getId() {
        return id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "DataRecord{id=" + id + ", data='" + data + "'}";
    }
}
