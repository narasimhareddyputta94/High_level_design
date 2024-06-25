package server_design3;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class Database {
    private String name;
    private Map<String, User> userStore = new ConcurrentHashMap<>();

    public Database(String name) {
        this.name = name;
    }

    public boolean storeUser(User user) {
        userStore.put(user.getEmail(), user);
        return true;
    }

    public User getUser(String email) {
        return userStore.get(email);
    }

    public void replicateUser(Database targetDatabase, User user) {
        targetDatabase.storeUser(user);
    }

    public void moveToArchive(String email, User user, PassiveDatabase passiveDatabase) {
        passiveDatabase.archiveUser(email, user);
        userStore.remove(email);
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
