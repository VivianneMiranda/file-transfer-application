import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileClient {
    private static final Logger LOGGER = Logger.getLogger(FileClient.class.getName());

    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, PORT);
             DataInputStream dis = new DataInputStream(socket.getInputStream());
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {

            LOGGER.info("Connected to server: " + SERVER_ADDRESS + " on port: " + PORT);

            String command = "UPLOAD"; // or "DOWNLOAD"
            dos.writeUTF(command);
            LOGGER.info("Sent command: " + command + " to server");

            if ("UPLOAD".equalsIgnoreCase(command)) {
                uploadFile(dos);
            } else if ("DOWNLOAD".equalsIgnoreCase(command)) {
                downloadFile(dis, dos);
            } else {
                LOGGER.warning("Unknown command");
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Client encountered an error", e);
        }
    }

    private static void uploadFile(DataOutputStream dos) throws IOException {
        String filePath = "example.txt"; // Replace with actual file name
        File file = new File(filePath);
        if (!file.exists() || file.isDirectory()) {
            LOGGER.warning("File not found or is a directory: " + filePath);
            return;
        }

        LOGGER.info("Preparing to upload file: " + filePath + " of size: " + file.length() + " bytes");

        dos.writeUTF(file.getName());
        dos.writeLong(file.length());

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[4096];
            int read;

            while ((read = fis.read(buffer)) != -1) {
                dos.write(buffer, 0, read);
            }
        }

        LOGGER.info("File " + filePath + " uploaded successfully.");
    }

    private static void downloadFile(DataInputStream dis, DataOutputStream dos) throws IOException {
        String fileName = "example.txt"; // Replace with actual file name
        dos.writeUTF(fileName);
        LOGGER.info("Requested download for file: " + fileName);

        String response = dis.readUTF();
        if ("OK".equalsIgnoreCase(response)) {
            long fileSize = dis.readLong();
            LOGGER.info("Downloading file: " + fileName + " of size: " + fileSize + " bytes");

            try (FileOutputStream fos = new FileOutputStream(fileName)) {
                byte[] buffer = new byte[4096];
                int read;
                long totalRead = 0;

                while (totalRead < fileSize && (read = dis.read(buffer)) != -1) {
                    totalRead += read;
                    fos.write(buffer, 0, read);
                }
            }

            LOGGER.info("File " + fileName + " downloaded successfully. Total bytes read: " + fileSize);
        } else {
            LOGGER.warning("File not found on the server.");
        }
    }
}
