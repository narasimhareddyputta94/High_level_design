package server_design2;

import java.util.ArrayList;
import java.util.List;

public class SecondLevelServer {
    private List<Database> databases = new ArrayList<>(4);
    private PassiveDatabase passiveDatabase;
    private SecondLevelServer standbyServer;

    public SecondLevelServer(String dbNamePrefix) {
        for (int i = 1; i <= 4; i++) {
            databases.add(new Database(dbNamePrefix + "_DB" + i));
        }
        this.passiveDatabase = new PassiveDatabase(dbNamePrefix + "_PassiveDB");
    }

    public void setStandbyServer(SecondLevelServer standbyServer) {
        this.standbyServer = standbyServer;
    }

    public boolean storeData(String key, String value) {
        int successCount = 0;
        for (Database db : databases) {
            if (db.storeData(key, value)) {
                successCount++;
                // Replicate data to other databases
                for (Database replicaDb : databases) {
                    if (!replicaDb.equals(db)) {
                        db.replicateData(replicaDb, key, value);
                    }
                }
            }
            if (successCount >= 3) {
                return true;
            }
        }
        return false;
    }

    public String getData(String key) {
        for (Database db : databases) {
            String value = db.getData(key);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    public void archiveData(String key) {
        for (Database db : databases) {
            String value = db.getData(key);
            if (value != null) {
                db.moveToArchive(key, value, passiveDatabase);
                break;
            }
        }
    }

    public String retrieveArchivedData(String key) {
        return passiveDatabase.retrieveArchivedData(key);
    }

    public List<Database> getDatabases() {
        return databases;
    }

    public SecondLevelServer getStandbyServer() {
        return standbyServer;
    }

    @Override
    public int hashCode() {
        return databases.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SecondLevelServer that = (SecondLevelServer) obj;
        return databases.equals(that.databases);
    }

    @Override
    public String toString() {
        return "SecondLevelServer{" +
                "databases=" + databases +
                '}';
    }
}
