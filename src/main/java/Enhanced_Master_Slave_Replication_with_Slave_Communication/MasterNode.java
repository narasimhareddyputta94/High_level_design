package Enhanced_Master_Slave_Replication_with_Slave_Communication;

import Enhanced_Master_Slave_Replication_with_Slave_Communication.DataRecord;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MasterNode {
    private List<DataRecord> database = new ArrayList<>();
    private List<SlaveNodeConnection> slaves = new ArrayList<>();
    private int port;
    private int replicationFactor; // Number of acknowledgments required

    public MasterNode(int port, int replicationFactor) {
        this.port = port;
        this.replicationFactor = replicationFactor;
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

    public boolean addDataRecord(DataRecord record) {
        database.add(record);
        return replicateData(record);
    }

    private boolean replicateData(DataRecord record) {
        CountDownLatch latch = new CountDownLatch(replicationFactor);

        for (SlaveNodeConnection slave : slaves) {
            new Thread(() -> {
                if (slave.sendData(record)) {
                    latch.countDown();
                }
            }).start();
        }

        try {
            return latch.await(5, TimeUnit.SECONDS); // Wait for acknowledgments with a timeout
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    private class SlaveNodeConnection implements Runnable {
        private Socket socket;
        private ObjectOutputStream outputStream;
        private ObjectInputStream inputStream;

        public SlaveNodeConnection(Socket socket) {
            this.socket = socket;
            try {
                this.outputStream = new ObjectOutputStream(socket.getOutputStream());
                this.inputStream = new ObjectInputStream(socket.getInputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            // This can be used to listen for messages from the slave if needed
        }

        public boolean sendData(DataRecord record) {
            try {
                outputStream.writeObject(record);
                outputStream.flush();

                // Wait for acknowledgment
                return inputStream.readBoolean();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public static void main(String[] args) {
        MasterNode master = new MasterNode(5000, 1); // Example: replication factor of 1
        master.start();

        // Adding some data records for testing
        System.out.println("Replication successful: " + master.addDataRecord(new DataRecord(1, "First Record")));
        System.out.println("Replication successful: " + master.addDataRecord(new DataRecord(2, "Second Record")));
    }
}
