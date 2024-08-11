import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileServer {
    private static final Logger LOGGER = Logger.getLogger(FileServer.class.getName());

    private static final int PORT = 12345;
    private static final String STORAGE_DIR = "server_storage";

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        LOGGER.info("Starting File Server...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            LOGGER.info("Server started. Listening on port " + PORT);

            File storageDir = new File(STORAGE_DIR);
            if (!storageDir.exists()) {
                storageDir.mkdirs();
                LOGGER.info("Storage directory created at " + storageDir.getAbsolutePath());
            }

            while (true) {
                Socket clientSocket = serverSocket.accept();
                LOGGER.info("New client connected: " + clientSocket.getInetAddress());
                executor.execute(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Server encountered an error", e);
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
                 DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream())) {

                String command = dis.readUTF();
                LOGGER.info("Received command: " + command + " from client: " + clientSocket.getInetAddress());

                if ("UPLOAD".equalsIgnoreCase(command)) {
                    receiveFile(dis);
                } else if ("DOWNLOAD".equalsIgnoreCase(command)) {
                    sendFile(dis, dos);
                } else {
                    LOGGER.warning("Unknown command received: " + command);
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error handling client: " + clientSocket.getInetAddress(), e);
            } finally {
                try {
                    clientSocket.close();
                    LOGGER.info("Client connection closed: " + clientSocket.getInetAddress());
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Error closing client socket", e);
                }
            }
        }

        private void receiveFile(DataInputStream dis) throws IOException {
            String fileName = dis.readUTF();
            long fileSize = dis.readLong();
            LOGGER.info("Receiving file: " + fileName + " of size: " + fileSize + " bytes");

            File file = new File(STORAGE_DIR, fileName);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                byte[] buffer = new byte[4096];
                int read;
                long totalRead = 0;

                while (totalRead < fileSize && (read = dis.read(buffer)) != -1) {
                    totalRead += read;
                    fos.write(buffer, 0, read);
                }

                LOGGER.info("File " + fileName + " received successfully. Total bytes read: " + totalRead);
            }
        }

        private void sendFile(DataInputStream dis, DataOutputStream dos) throws IOException {
            String fileName = dis.readUTF();
            File file = new File(STORAGE_DIR, fileName);
            LOGGER.info("Client requested file: " + fileName);

            if (file.exists()) {
                dos.writeUTF("OK");
                dos.writeLong(file.length());
                LOGGER.info("Sending file: " + fileName + " of size: " + file.length() + " bytes");

                try (FileInputStream fis = new FileInputStream(file)) {
                    byte[] buffer = new byte[4096];
                    int read;

                    while ((read = fis.read(buffer)) != -1) {
                        dos.write(buffer, 0, read);
                    }
                }

                LOGGER.info("File " + fileName + " sent successfully.");
            } else {
                dos.writeUTF("ERROR");
                LOGGER.warning("File not found: " + fileName);
            }
        }
    }
}
