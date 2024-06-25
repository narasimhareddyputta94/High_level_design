package server_design2;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class PassiveDatabase {
    private String name;
    private Map<String, String> dataStore = new ConcurrentHashMap<>();

    public PassiveDatabase(String name) {
        this.name = name;
    }

    public void archiveData(String key, String value) {
        dataStore.put(key, value);
    }

    public String retrieveArchivedData(String key) {
        return dataStore.get(key);
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PassiveDatabase database = (PassiveDatabase) obj;
        return name.equals(database.name);
    }

    @Override
    public String toString() {
        return "PassiveDatabase{" +
                "name='" + name + '\'' +
                '}';
    }
}
