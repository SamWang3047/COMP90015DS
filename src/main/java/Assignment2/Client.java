package Assignment2;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.Socket;

public class Client {
    private String serverAddress = ""; // Change to server IP if needed
    private int serverPort = 0; // Change to server port if needed
    private String username;
    private ClientGUI clientGUI;

    private BufferedWriter writer;
    private BufferedReader reader;
    private Socket socket;

    public Client(String username, String serverAddress, int serverPort) {
        this.username = username;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public void run() {
        try {
            socket = new Socket(serverAddress, serverPort);
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            clientGUI = new ClientGUI(writer, reader);
            clientGUI.init();
            // Send the username to the server
            // Send the username to the server as a JSON message
            JSONObject request = new JSONObject();
            request.put("username", username);
            writer.write(request.toJSONString());
            writer.newLine();
            writer.flush();

            // Read messages from the server and update the GUI
            String serverMessage;
            JSONParser parser = new JSONParser();
            while ((serverMessage = reader.readLine()) != null) {
                JSONObject receivedJson = (JSONObject) parser.parse(serverMessage);
                String receivedUsername = (String) receivedJson.get("username");
                String receivedMessage = (String) receivedJson.get("message");
                // Update the GUI based on the received JSON message
                clientGUI.updateGUI(receivedJson);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("Usage: java PlayerClient <username> <server address> <server port>");
            System.exit(1);
        }

        String username = args[0];
        String address = args[1];
        int port;
        try {
            port = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            System.err.println("Error: Invalid port number");
            System.exit(1);
            return;
        }

        Client client = new Client(username, address, port);
        client.run();
    }
}
