package Client;

import Server.StateCode;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class DictionaryClient {
    private static String address = ""; // Change to server IP if needed
    private static int serverPort = 0; // Change to server port if needed
    private BufferedReader reader;
    private BufferedWriter writer;
    private DictionaryClientGUI gui;

    public DictionaryClient() {
        try {
            Socket socket = new Socket(address, serverPort);
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            gui = new DictionaryClientGUI(this); // Pass the client instance to the GUI
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: " + address);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: " + address);
            System.exit(1);
        }

    }

    public static void main(String[] args) {
        validation(args);
        DictionaryClient dictionaryClient = new DictionaryClient();
    }


    private static void processResponse(JSONObject response) {
        int status = Integer.parseInt(response.get("status").toString());
        switch (status) {
            case StateCode.SUCCESS:
                System.out.println("Operation successful");
                // Handle success response based on the command
                handleSuccess(response);
                break;
            case StateCode.FAIL:
                System.out.println("Operation failed");
                String message = response.get("message").toString();
                System.out.println("Error message: " + message);
                break;
            case StateCode.NOT_FOUND:
                System.out.println("Word not found");
                break;
            case StateCode.DUPLICATE:
                System.out.println("Word already exists");
                break;
            case StateCode.EMPTY_MEANING:
                System.out.println("Meanings cannot be empty");
                break;
            // Handle other response statuses as needed
            default:
                System.out.println("Unexpected response status");
        }
    }

    private static void handleSuccess(JSONObject response) {
        int command = Integer.parseInt(response.get("command").toString());
        switch (command) {
            case StateCode.QUERY:
                String word = response.get("word").toString();
                String meanings = response.get("meanings").toString();
                System.out.println("Word: " + word);
                System.out.println("Meanings: " + meanings);
                break;
            case StateCode.ADD:
                System.out.println("Word added successfully");
                break;
            case StateCode.REMOVE:
                System.out.println("Word removed successfully");
                break;
            case StateCode.UPDATE:
                System.out.println("Word meanings updated successfully");
                break;
            // Handle other success responses as needed
            default:
                System.out.println("Operation successful");
        }
    }


    private static void validation(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java -jar <server address> <server port>");
            System.exit(1);
        }
        address = args[0];

        try {
            serverPort = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Error: Invalid port number");
            System.exit(1);
        }

        if (serverPort < 1 || serverPort > 65535) {
            System.err.println("Error: Invalid port number");
            System.exit(1);
        }

    }
    public void addWord(String word, String meanings) throws IOException, ParseException {
        JSONObject request = new JSONObject();
        request.put("command", StateCode.ADD);
        request.put("word", word);
        request.put("meanings", meanings);
        sendRequest(request);
    }

    private void sendRequest(JSONObject request) {
        try {
            writer.write(request.toJSONString() + "\n");
            writer.flush();
            String response = reader.readLine();
            JSONObject jsonResponse = (JSONObject) new JSONParser().parse(response);
            processResponse(jsonResponse);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred while sending the request.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    public BufferedReader getReader() {
        return reader;
    }

    public BufferedWriter getWriter() {
        return writer;
    }
}
