package Master_Slave_Replication;

import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SlaveNode {
    private List<DataRecord> database = new ArrayList<>();
    private String masterHost;
    private int masterPort;

    public SlaveNode(String masterHost, int masterPort) {
        this.masterHost = masterHost;
        this.masterPort = masterPort;
    }

    public void start() {
        try (Socket socket = new Socket(masterHost, masterPort)) {
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            System.out.println("Connected to master node at " + masterHost + ":" + masterPort);

            while (true) {
                DataRecord record = (DataRecord) inputStream.readObject();
                database.add(record);
                System.out.println("Received replicated data: " + record);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SlaveNode slave = new SlaveNode("localhost", 5000);
        slave.start();
    }
}
//Start the Master Node:
//
//Run the MasterNode class. It will start a server on port 5000 and wait for slave nodes to connect.
//Start the Slave Node:
//
//Run the SlaveNode class. It will connect to the master node on localhost:5000 and start receiving replicated data.
//Add Data to the Master Node:
//
//The MasterNode class has a few sample data records added for testing. When these records are added, they will be sent to all connected slave nodes.
//        Explanation
//MasterNode: Listens for connections from slave nodes and maintains a list of connected slaves. When a new data record is added, it sends the record to all connected slaves.
//SlaveNode: Connects to the master node and waits for data records to be sent. When a record is received, it adds it to its local database.
//This example demonstrates a basic replication model. In a real-world scenario, additional features such as handling disconnections, ensuring data consistency, and more sophisticated replication mechanisms would be needed.