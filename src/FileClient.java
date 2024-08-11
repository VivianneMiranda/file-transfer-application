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
        if (args.length < 2) {
            System.out.println("Usage: java FileClient <UPLOAD/DOWNLOAD> <file-path> [destination-path]");
            return;
        }

        String command = args[0].toUpperCase();
        String filePath = args[1];
        String destinationPath = (args.length > 2) ? args[2] : "";

        try (Socket socket = new Socket(SERVER_ADDRESS, PORT);
             DataInputStream dis = new DataInputStream(socket.getInputStream());
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {

            LOGGER.info("Connected to server: " + SERVER_ADDRESS + " on port: " + PORT);
            dos.writeUTF(command);
            LOGGER.info("Sent command: " + command + " to server");

            if ("UPLOAD".equals(command)) {
                uploadFile(dos, filePath);
            } else if ("DOWNLOAD".equals(command)) {
                downloadFile(dis, dos, filePath, destinationPath);
            } else {
                LOGGER.warning("Unknown command");
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Client encountered an error", e);
        }
    }

    private static void uploadFile(DataOutputStream dos, String filePath) throws IOException {
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

    private static void downloadFile(DataInputStream dis, DataOutputStream dos, String fileName, String destinationPath) throws IOException {
        dos.writeUTF(fileName);
        LOGGER.info("Requested download for file: " + fileName);

        String response = dis.readUTF();
        if ("OK".equalsIgnoreCase(response)) {
            long fileSize = dis.readLong();
            LOGGER.info("Downloading file: " + fileName + " of size: " + fileSize + " bytes");

            if (destinationPath.isEmpty()) {
                destinationPath = fileName;  // Default to current directory
            }

            try (FileOutputStream fos = new FileOutputStream(destinationPath)) {
                byte[] buffer = new byte[4096];
                int read;
                long totalRead = 0;

                while (totalRead < fileSize && (read = dis.read(buffer)) != -1) {
                    totalRead += read;
                    fos.write(buffer, 0, read);
                }
            }

            LOGGER.info("File " + fileName + " downloaded successfully to " + destinationPath + ". Total bytes read: " + fileSize);
        } else {
            LOGGER.warning("File not found on the server.");
        }
    }
}
