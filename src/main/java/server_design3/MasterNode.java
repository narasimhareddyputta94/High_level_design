package server_design3;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MasterNode {
    private Map<String, User> userStore = new ConcurrentHashMap<>();
    private List<SecondLevelServer> secondLevelServers = new ArrayList<>();
    private List<MasterNode> peerMasters = new ArrayList<>();

    public MasterNode() {}

    public void addSecondLevelServer(SecondLevelServer server) {
        secondLevelServers.add(server);
    }

    public void addPeerMaster(MasterNode peer) {
        peerMasters.add(peer);
    }

    public void registerUser(String email, String password) {
        User user = new User(email, password);
        userStore.put(email, user);
        replicateToPeers(user);
        replicateToSecondLevelServers(user);
    }

    public User loginUser(String email, String password) {
        User user = userStore.get(email);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    private void replicateToPeers(User user) {
        for (MasterNode peer : peerMasters) {
            peer.receiveReplication(user);
        }
    }

    public void receiveReplication(User user) {
        userStore.put(user.getEmail(), user);
        replicateToSecondLevelServers(user);
    }

    private void replicateToSecondLevelServers(User user) {
        for (SecondLevelServer server : secondLevelServers) {
            server.storeUser(user);
        }
    }

    public void archiveUser(String email) {
        for (SecondLevelServer server : secondLevelServers) {
            server.archiveUser(email);
        }
        userStore.remove(email);
    }

    public User retrieveArchivedUser(String email) {
        for (SecondLevelServer server : secondLevelServers) {
            User user = server.retrieveArchivedUser(email);
            if (user != null) {
                return user;
            }
        }
        return null;
    }
}
