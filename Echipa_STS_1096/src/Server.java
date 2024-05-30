import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

public class Server {
    private static final int PORT = 13456;
    private static final String BASE_DIR = "server_files/";

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server started on port " + PORT);

            Path baseDirPath = Paths.get(BASE_DIR);
            if (!Files.exists(baseDirPath)) {
                Files.createDirectories(baseDirPath);
            }

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket);

                Thread clientThread = new Thread(new ClientHandler(clientSocket));
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    System.out.println("Received from client: " + inputLine);
                    processInput(inputLine);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (in != null) in.close();
                    if (out != null) out.close();
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void processInput(String input) {
            String[] tokens = input.split("\\s+", 3);
            String command = tokens[0];

            switch (command) {
                case "list":
                    listFiles();
                    break;
                case "create":
                    if (tokens.length == 2) {
                        createFile(tokens[1]);
                    } else {
                        out.println("Invalid CREATE command syntax.");
                    }
                    break;
                case "delete":
                    if (tokens.length == 2) {
                        deleteFile(tokens[1]);
                    } else {
                        out.println("Invalid DELETE command syntax.");
                    }
                    break;
                case "modify":
                    if (tokens.length == 3) {
                        modifyFile(tokens[1], tokens[2]);
                    } else {
                        out.println("Invalid MODIFY command syntax.");
                    }
                    break;
                case "rename":
                    if (tokens.length == 3) {
                        renameFile(tokens[1], tokens[2]);
                    } else {
                        out.println("Invalid RENAME command syntax.");
                    }
                    break;
                case "clear":
                    if (tokens.length == 2) {
                        clearFile(tokens[1]);
                    } else {
                        out.println("Invalid CLEAR command syntax.");
                    }
                    break;
            }
        }

        private void listFiles() {
            try {
                File folder = new File(BASE_DIR);
                File[] listOfFiles = folder.listFiles();

                out.println("FILES:");
                if (listOfFiles != null) {
                    for (File file : listOfFiles) {
                        out.println(file.getName());
                    }
                }
                out.println("END");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void createFile(String fileName) {
            try {
                File newFile = new File(BASE_DIR + fileName);
                if (newFile.createNewFile()) {
                    out.println("File '" + fileName + "' created.");
                } else {
                    out.println("File '" + fileName + "' already exists.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void deleteFile(String fileName) {
            try {
                File fileToDelete = new File(BASE_DIR + fileName);
                if (fileToDelete.delete()) {
                    out.println("File '" + fileName + "' deleted.");
                } else {
                    out.println("Failed to delete file '" + fileName + "'. File does not exist or permission denied.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void modifyFile(String fileName, String content) {
            try {
                Path filePath = Paths.get(BASE_DIR + fileName);
                Files.write(filePath, content.getBytes());
                out.println("File '" + fileName + "' modified.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void renameFile(String oldFileName, String newFileName) {
            try {
                File oldFile = new File(BASE_DIR + oldFileName);
                File newFile = new File(BASE_DIR + newFileName);

                if (oldFile.exists()) {
                    if (oldFile.renameTo(newFile)) {
                        out.println("File '" + oldFileName + "' renamed to '" + newFileName + "'.");
                    } else {
                        out.println("Failed to rename file '" + oldFileName + "'.");
                    }
                } else {
                    out.println("File '" + oldFileName + "' does not exist.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void clearFile(String fileName) {
            try {
                Path filePath = Paths.get(BASE_DIR + fileName);
                Files.write(filePath, new byte[0]); // Scriem un șir gol pentru a șterge conținutul

                out.println("Cleared content of file '" + fileName + "'.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
