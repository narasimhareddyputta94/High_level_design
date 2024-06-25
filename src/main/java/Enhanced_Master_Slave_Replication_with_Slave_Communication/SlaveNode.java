package Enhanced_Master_Slave_Replication_with_Slave_Communication;

import Enhanced_Master_Slave_Replication_with_Slave_Communication.DataRecord;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SlaveNode {
    private List<DataRecord> database = new ArrayList<>();
    private String masterHost;
    private int masterPort;
    private int slavePort;
    private List<String> otherSlaves = new ArrayList<>();

    public SlaveNode(String masterHost, int masterPort, int slavePort, List<String> otherSlaves) {
        this.masterHost = masterHost;
        this.masterPort = masterPort;
        this.slavePort = slavePort;
        this.otherSlaves = otherSlaves;
    }

    public void start() {
        new Thread(this::connectToMaster).start();
        new Thread(this::startSlaveServer).start();
        scheduleDataSharing();
    }

    private void connectToMaster() {
        try (Socket socket = new Socket(masterHost, masterPort)) {
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("Connected to master node at " + masterHost + ":" + masterPort);

            while (true) {
                DataRecord record = (DataRecord) inputStream.readObject();
                database.add(record);
                System.out.println("Received replicated data: " + record);

                // Send acknowledgment
                outputStream.writeBoolean(true);
                outputStream.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startSlaveServer() {
        try (ServerSocket serverSocket = new ServerSocket(slavePort)) {
            System.out.println("Slave node started on port " + slavePort);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new SlaveHandler(clientSocket)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void scheduleDataSharing() {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                shareDataWithSlaves();
            }
        }, 0, 10000); // Share data every 10 seconds
    }

    private void shareDataWithSlaves() {
        for (String slaveAddress : otherSlaves) {
            try (Socket socket = new Socket(slaveAddress, slavePort)) {
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                for (DataRecord record : database) {
                    outputStream.writeObject(record);
                    outputStream.flush();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class SlaveHandler implements Runnable {
        private Socket socket;

        public SlaveHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())) {
                while (true) {
                    DataRecord record = (DataRecord) inputStream.readObject();
                    if (!database.contains(record)) {
                        database.add(record);
                        System.out.println("Received data from another slave: " + record);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        List<String> otherSlaves = new ArrayList<>();
        otherSlaves.add("localhost"); // Example slave addresses
        SlaveNode slave = new SlaveNode("localhost", 5000, 5001, otherSlaves);
        slave.start();
    }
}
