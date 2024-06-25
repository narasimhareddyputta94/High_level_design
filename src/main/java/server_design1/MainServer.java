package server_design1;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MainServer {
    private TreeMap<Integer, SecondLevelServer> hashRing = new TreeMap<>();
    private List<SecondLevelServer> servers = new ArrayList<>();
    private List<SecondLevelServer> standbyServers = new ArrayList<>();
    private Map<String, String> dataStore = new ConcurrentHashMap<>();
    private MainServer standbyMainServer;

    public synchronized void addServer(SecondLevelServer server, SecondLevelServer standbyServer) {
        int hash = server.hashCode();
        hashRing.put(hash, server);
        servers.add(server);
        standbyServers.add(standbyServer);
        server.setStandbyServer(standbyServer);
        rebalanceData();
    }

    public synchronized void removeServer(SecondLevelServer server) {
        int hash = server.hashCode();
        hashRing.remove(hash);
        servers.remove(server);
        standbyServers.remove(server.getStandbyServer());
        rebalanceData();
    }

    public SecondLevelServer getServer(Object key) {
        if (hashRing.isEmpty()) {
            return null;
        }
        int hash = key.hashCode();
        if (!hashRing.containsKey(hash)) {
            SortedMap<Integer, SecondLevelServer> tailMap = hashRing.tailMap(hash);
            hash = tailMap.isEmpty() ? hashRing.firstKey() : tailMap.firstKey();
        }
        return hashRing.get(hash);
    }

    private void rebalanceData() {
        for (Map.Entry<String, String> entry : dataStore.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            SecondLevelServer server = getServer(key);
            server.storeData(key, value);
        }
    }

    public void storeData(String key, String value) {
        SecondLevelServer server = getServer(key);
        if (server.storeData(key, value)) {
            dataStore.put(key, value);
        }
    }

    public String getData(String key) {
        SecondLevelServer server = getServer(key);
        return server.getData(key);
    }

    public void setStandbyMainServer(MainServer standbyMainServer) {
        this.standbyMainServer = standbyMainServer;
    }

    public MainServer getStandbyMainServer() {
        return standbyMainServer;
    }

    public static void main(String[] args) {
        MainServer mainServer = new MainServer();
        MainServer standbyMainServer = new MainServer();
        mainServer.setStandbyMainServer(standbyMainServer);

        // Add initial servers with standby servers
        for (int i = 1; i <= 10; i++) {
            SecondLevelServer server = new SecondLevelServer("DB" + i);
            SecondLevelServer standbyServer = new SecondLevelServer("Standby_DB" + i);
            mainServer.addServer(server, standbyServer);
        }

        // Simulate storing data for 10k users
        for (int i = 1; i <= 10000; i++) {
            mainServer.storeData("user" + i, "data" + i);
        }

        // Retrieve data for some users to test
        System.out.println(mainServer.getData("user1"));
        System.out.println(mainServer.getData("user5000"));
        System.out.println(mainServer.getData("user9999"));

        // Add a new server and rebalance data
        SecondLevelServer newServer = new SecondLevelServer("DB11");
        SecondLevelServer newStandbyServer = new SecondLevelServer("Standby_DB11");
        mainServer.addServer(newServer, newStandbyServer);

        // Retrieve data again to see the rebalancing effect
        System.out.println(mainServer.getData("user1"));
        System.out.println(mainServer.getData("user5000"));
        System.out.println(mainServer.getData("user9999"));
    }
}
