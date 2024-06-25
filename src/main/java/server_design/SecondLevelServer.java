package server_design;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class SecondLevelServer {
    private Database database;

    public SecondLevelServer(String dbName) {
        this.database = new Database(dbName);
    }

    public void storeData(String key, String value) {
        database.storeData(key, value);
    }

    public String getData(String key) {
        return database.getData(key);
    }

    public Database getDatabase() {
        return database;
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
                "database=" + database +
                '}';
    }
}
