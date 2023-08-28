package Client;

import Server.StateCode;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class DictionaryClient {
    private static String address = ""; // Change to server IP if needed
    private static int serverPort = 0; // Change to server port if needed
    private BufferedReader reader;
    private BufferedWriter writer;
    private DictionaryClientGUI gui;
    private Socket socket;

    public DictionaryClient() {
        try {
            socket = new Socket(address, serverPort);
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
    private List<String> sendRequest(JSONObject request) {
        List<String> responseList = new ArrayList<>();
        try {
            String requestString = request.toJSONString();
            String encodedString = Base64.getEncoder().encodeToString(requestString.getBytes());
            System.out.println(encodedString);
            writer.write(encodedString + "\n");
            writer.flush();
            String response = reader.readLine();
            JSONObject jsonResponse = (JSONObject) new JSONParser().parse(response);
            responseList = processResponse(jsonResponse);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred while sending the request.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return responseList;
    }
    public boolean addWord(String word, String meanings) throws IOException, ParseException {
        JSONObject request = new JSONObject();
        request.put("command", StateCode.ADD);
        request.put("word", word);
        request.put("meanings", meanings);
        List<String> responseList =  sendRequest(request);
        return Integer.parseInt(responseList.get(0)) == StateCode.SUCCESS;
    }
    public String queryWord(String word) throws IOException, ParseException{
        JSONObject request = new JSONObject();
        request.put("command", StateCode.QUERY);
        request.put("word", word);
        List<String> responseList = sendRequest(request);
        if (Integer.parseInt(responseList.get(0)) == StateCode.SUCCESS) {
            return responseList.get(1);
        } else {
            return null;
        }
    }
    public boolean removeWord(String word) throws IOException, ParseException{
        JSONObject request = new JSONObject();
        request.put("command", StateCode.REMOVE);
        request.put("word", word);
        List<String> responseList = sendRequest(request);
        return Integer.parseInt(responseList.get(0)) == StateCode.SUCCESS;
    }

    public void updateWord(String word, String newMeanings) throws IOException, ParseException{
        JSONObject request = new JSONObject();
        request.put("command", StateCode.UPDATE);
        request.put("word", word);
        request.put("meanings", newMeanings);
        sendRequest(request);
    }
    public static void main(String[] args) {
        validation(args);
        DictionaryClient dictionaryClient = new DictionaryClient();
    }


    private List<String> processResponse(JSONObject response) {
        List<String> responseList = new ArrayList<>();
        int status = Integer.parseInt(response.get("status").toString());
        switch (status) {
            case StateCode.SUCCESS:
                System.out.println("Operation successful");
                // Handle success response based on the command
                responseList = handleSuccess(response);
                break;
            case StateCode.FAIL:
                System.out.println("Operation failed");
                String message = response.get("message").toString();
                responseList.add(String.valueOf(StateCode.FAIL));
                System.out.println("Error message: " + message);
                break;
            case StateCode.NOT_FOUND:
                responseList.add(String.valueOf(StateCode.FAIL));
                System.out.println("Word not found");
                break;
            case StateCode.DUPLICATE:
                System.out.println("Word already exists");
                responseList.add(String.valueOf(StateCode.FAIL));
                break;
            case StateCode.EMPTY_MEANING:
                System.out.println("Meanings cannot be empty");
                responseList.add(String.valueOf(StateCode.FAIL));
                break;
            // Handle other response statuses as needed
            default:
                responseList.add(String.valueOf(StateCode.FAIL));
                System.out.println("Unexpected response status");
        }
        return responseList;
    }

    private List<String> handleSuccess(JSONObject response) {
        List<String> responseList = new ArrayList<>();
        int command = Integer.parseInt(response.get("command").toString());
        switch (command) {
            case StateCode.QUERY:
                String word = response.get("word").toString();
                String meanings = response.get("meanings").toString();
                System.out.println("Word: " + word);
                System.out.println("Meanings: " + meanings);
                responseList.add(word);
                responseList.add(meanings);
                break;
            case StateCode.ADD:
                System.out.println("Word added successfully");
                responseList.add(String.valueOf(StateCode.SUCCESS));
                break;
            case StateCode.REMOVE:
                System.out.println("Word removed successfully");
                responseList.add(String.valueOf(StateCode.SUCCESS));
                break;
            case StateCode.UPDATE:
                System.out.println("Word meanings updated successfully");
                responseList.add(String.valueOf(StateCode.SUCCESS));
                break;
            // Handle other success responses as needed
            default:
                System.out.println("Operation successful");
        }
        return responseList;
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

    public BufferedReader getReader() {
        return reader;
    }
    public BufferedWriter getWriter() {
        return writer;
    }
    public Socket getSocket() {
        return socket;
    }
}
