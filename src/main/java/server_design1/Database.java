package server_design1;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class Database {
    private String name;
    private Map<String, String> dataStore = new ConcurrentHashMap<>();

    public Database(String name) {
        this.name = name;
    }

    public boolean storeData(String key, String value) {
        dataStore.put(key, value);
        return true;
    }

    public String getData(String key) {
        return dataStore.get(key);
    }

    public void replicateData(Database targetDatabase, String key, String value) {
        targetDatabase.storeData(key, value);
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
        Database database = (Database) obj;
        return name.equals(database.name);
    }

    @Override
    public String toString() {
        return "Database{" +
                "name='" + name + '\'' +
                '}';
    }
}
