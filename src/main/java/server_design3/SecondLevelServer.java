package server_design3;

import java.util.ArrayList;
import java.util.List;

public class SecondLevelServer {
    private List<Database> databases = new ArrayList<>(4);
    private PassiveDatabase passiveDatabase;

    public SecondLevelServer(String dbNamePrefix) {
        for (int i = 1; i <= 4; i++) {
            databases.add(new Database(dbNamePrefix + "_DB" + i));
        }
        this.passiveDatabase = new PassiveDatabase(dbNamePrefix + "_PassiveDB");
    }

    public boolean storeUser(User user) {
        int successCount = 0;
        for (Database db : databases) {
            if (db.storeUser(user)) {
                successCount++;
                // Replicate data to other databases
                for (Database replicaDb : databases) {
                    if (!replicaDb.equals(db)) {
                        db.replicateUser(replicaDb, user);
                    }
                }
            }
            if (successCount >= 3) {
                return true;
            }
        }
        return false;
    }

    public User getUser(String email) {
        for (Database db : databases) {
            User user = db.getUser(email);
            if (user != null) {
                return user;
            }
        }
        return null;
    }

    public void archiveUser(String email) {
        for (Database db : databases) {
            User user = db.getUser(email);
            if (user != null) {
                db.moveToArchive(email, user, passiveDatabase);
                break;
            }
        }
    }

    public User retrieveArchivedUser(String email) {
        return passiveDatabase.retrieveArchivedUser(email);
    }

    public List<Database> getDatabases() {
        return databases;
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
