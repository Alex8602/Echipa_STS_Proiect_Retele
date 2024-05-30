import java.io.*;
import java.net.*;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 13456;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Connected to server. Type 'help' for available commands.");

            String userInputLine;
            while ((userInputLine = userInput.readLine()) != null) {
                if (userInputLine.equals("exit")) {
                    out.println("exit");
                    break;
                } else if (userInputLine.equals("help")) {
                    printHelp();
                } else {
                    out.println(userInputLine);
                    String serverResponse = in.readLine();
                    System.out.println("Server response: " + serverResponse);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printHelp() {
        System.out.println("Available commands:");
        System.out.println("list - List all files on the server");
        System.out.println("create filename - Create a new file on the server");
        System.out.println("delete filename - Delete a file from the server");
        System.out.println("modify filename content - Modify the content of a file on the server");
        System.out.println("clear filename - Clear content");
        System.out.println("rename currentFileName newFileName - Rename the file");
        System.out.println("exit - Exit the application");
        System.out.println("help - Print this help message");
    }
}
