package server_design3;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class PassiveDatabase {
    private String name;
    private Map<String, User> userStore = new ConcurrentHashMap<>();

    public PassiveDatabase(String name) {
        this.name = name;
    }

    public void archiveUser(String email, User user) {
        userStore.put(email, user);
    }

    public User retrieveArchivedUser(String email) {
        return userStore.get(email);
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
