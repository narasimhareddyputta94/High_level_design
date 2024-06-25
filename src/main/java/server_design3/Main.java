package server_design3;

public class Main {
    public static void main(String[] args) {
        // Initialize master nodes
        MasterNode master1 = new MasterNode();
        MasterNode master2 = new MasterNode();
        MasterNode master3 = new MasterNode();

        // Add peer masters to each master node for replication
        master1.addPeerMaster(master2);
        master1.addPeerMaster(master3);

        master2.addPeerMaster(master1);
        master2.addPeerMaster(master3);

        master3.addPeerMaster(master1);
        master3.addPeerMaster(master2);

        // Initialize second-level servers and add to masters
        for (int i = 1; i <= 4; i++) {
            SecondLevelServer server1 = new SecondLevelServer("Master1_DB" + i);
            SecondLevelServer server2 = new SecondLevelServer("Master2_DB" + i);
            SecondLevelServer server3 = new SecondLevelServer("Master3_DB" + i);

            master1.addSecondLevelServer(server1);
            master2.addSecondLevelServer(server2);
            master3.addSecondLevelServer(server3);
        }

        // Register users in master-master setup
        master1.registerUser("user1@example.com", "password1");
        master2.registerUser("user2@example.com", "password2");
        master3.registerUser("user3@example.com", "password3");

        // Login users from different masters
        System.out.println("Login user1 from Master1: " + master1.loginUser("user1@example.com", "password1"));
        System.out.println("Login user2 from Master2: " + master2.loginUser("user2@example.com", "password2"));
        System.out.println("Login user3 from Master3: " + master3.loginUser("user3@example.com", "password3"));

        // Archive users
        master1.archiveUser("user1@example.com");
        master2.archiveUser("user2@example.com");

        // Retrieve archived users
        System.out.println("Retrieve archived user1: " + master1.retrieveArchivedUser("user1@example.com"));
        System.out.println("Retrieve archived user2: " + master2.retrieveArchivedUser("user2@example.com"));
    }
}
