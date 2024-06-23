package consisting_hashing;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class ConsistentHashing {
    private final int replicas;
    private final SortedMap<Integer, String> circle = new TreeMap<>();

    public ConsistentHashing(List<String> nodes, int replicas) {
        this.replicas = replicas;
        for (String node : nodes) {
            addNode(node);
        }
    }

    private int hash(String key) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        md.update(key.getBytes(StandardCharsets.UTF_8));
        byte[] digest = md.digest();
        return ((digest[3] & 0xFF) << 24) | ((digest[2] & 0xFF) << 16) | ((digest[1] & 0xFF) << 8) | (digest[0] & 0xFF);
    }

    public void addNode(String node) {
        for (int i = 0; i < replicas; i++) {
            circle.put(hash(node + ":" + i), node);
        }
    }

    public void removeNode(String node) {
        for (int i = 0; i < replicas; i++) {
            circle.remove(hash(node + ":" + i));
        }
    }

    public String getNode(String key) {
        if (circle.isEmpty()) {
            return null;
        }
        int hash = hash(key);
        if (!circle.containsKey(hash)) {
            SortedMap<Integer, String> tailMap = circle.tailMap(hash);
            hash = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
        }
        return circle.get(hash);
    }

    public static void main(String[] args) {
        List<String> nodes = new ArrayList<>();
        Collections.addAll(nodes, "A", "B", "C");
        ConsistentHashing ch = new ConsistentHashing(nodes, 3);

        // Adding a key and finding its node
        String key = "my_data_key";
        String node = ch.getNode(key);
        System.out.println("Key '" + key + "' is assigned to node '" + node + "'");

        // Adding a new node
        ch.addNode("D");
        node = ch.getNode(key);
        System.out.println("After adding node 'D', key '" + key + "' is assigned to node '" + node + "'");
    }
}
