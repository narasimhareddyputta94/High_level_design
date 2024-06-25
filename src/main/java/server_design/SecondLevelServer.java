package server_design;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class SecondLevelServer {
    private String database;
    private Map<String, String> localDataStore = new ConcurrentHashMap<>();

    public SecondLevelServer(String database) {
        this.database = database;
    }

    public void storeData(String key, String value) {
        localDataStore.put(key, value);
    }

    public String getData(String key) {
        return localDataStore.get(key);
    }

    @Override
    public int hashCode() {
        return database.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SecondLevelServer that = (SecondLevelServer) obj;
        return database.equals(that.database);
    }

    @Override
    public String toString() {
        return "SecondLevelServer{" +
                "database='" + database + '\'' +
                '}';
    }
}
