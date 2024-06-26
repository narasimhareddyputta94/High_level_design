package Master_Slave_Replication;

import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MasterNode {
    private List<DataRecord> database = new ArrayList<>();
    private List<SlaveNodeConnection> slaves = new ArrayList<>();
    private int port;

    public MasterNode(int port) {
        this.port = port;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Master node started on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                SlaveNodeConnection slave = new SlaveNodeConnection(clientSocket);
                slaves.add(slave);
                new Thread(slave).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addDataRecord(DataRecord record) {
        database.add(record);
        replicateData(record);
    }

    private void replicateData(DataRecord record) {
        for (SlaveNodeConnection slave : slaves) {
            slave.sendData(record);
        }
    }

    private class SlaveNodeConnection implements Runnable {
        private Socket socket;
        private ObjectOutputStream outputStream;

        public SlaveNodeConnection(Socket socket) {
            this.socket = socket;
            try {
                this.outputStream = new ObjectOutputStream(socket.getOutputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            // This can be used to listen for messages from the slave if needed
        }

        public void sendData(DataRecord record) {
            try {
                outputStream.writeObject(record);
                outputStream.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        MasterNode master = new MasterNode(5000);
        master.start();

        // Adding some data records for testing
        master.addDataRecord(new DataRecord(1, "First Record"));
        master.addDataRecord(new DataRecord(2, "Second Record"));
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
