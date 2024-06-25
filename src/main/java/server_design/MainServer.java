package server_design;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MainServer {
    private TreeMap<Integer, SecondLevelServer> hashRing = new TreeMap<>();
    private List<SecondLevelServer> servers = new ArrayList<>();
    private Map<String, String> dataStore = new ConcurrentHashMap<>();

    public synchronized void addServer(SecondLevelServer server) {
        int hash = server.hashCode();
        hashRing.put(hash, server);
        servers.add(server);
        rebalanceData();
    }

    public synchronized void removeServer(SecondLevelServer server) {
        int hash = server.hashCode();
        hashRing.remove(hash);
        servers.remove(server);
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
        server.storeData(key, value);
        dataStore.put(key, value);
    }

    public String getData(String key) {
        SecondLevelServer server = getServer(key);
        return server.getData(key);
    }

    public static void main(String[] args) {
        MainServer mainServer = new MainServer();
        // Add initial servers
        SecondLevelServer server1 = new SecondLevelServer("DB1");
        SecondLevelServer server2 = new SecondLevelServer("DB2");
        mainServer.addServer(server1);
        mainServer.addServer(server2);

        // Store data
        mainServer.storeData("key1", "value1");
        mainServer.storeData("key2", "value2");

        // Retrieve data
        System.out.println(mainServer.getData("key1"));
        System.out.println(mainServer.getData("key2"));

        // Add a new server and rebalance data
        SecondLevelServer server3 = new SecondLevelServer("DB3");
        mainServer.addServer(server3);

        // Retrieve data again to see the rebalancing effect
        System.out.println(mainServer.getData("key1"));
        System.out.println(mainServer.getData("key2"));
    }
}
