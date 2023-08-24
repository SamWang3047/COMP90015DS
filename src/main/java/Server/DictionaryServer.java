package Server;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class DictionaryServer {
    private static Map<String, String> dictionary = new HashMap<>();

    public static void main(String[] args) {
        int portNumber = 8888;
        try {
            ServerSocket serverSocket = new ServerSocket(portNumber);
            System.out.println("Server is listening on port " + portNumber);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted connection from " + clientSocket.getInetAddress());

                Thread clientThread = new Thread(() -> handleClient(clientSocket));
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))
        ) {
            String request = reader.readLine();
            JSONObject response = processRequest(request);

            writer.write(response.toJSONString() + "\n");
            writer.flush();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private static JSONObject processRequest(String request) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject requestData = (JSONObject) parser.parse(request);
        JSONObject responseData = new JSONObject();

        String command = (String) requestData.get("command");
        String word = (String) requestData.get("word");
        String meanings = (String) requestData.get("meanings");

        switch (command) {
            case "SEARCH":
                String meaning = dictionary.get(word);
                if (meaning != null) {
                    responseData.put("status", "success");
                    responseData.put("meanings", meaning);
                } else {
                    responseData.put("status", "not found");
                }
                break;

            case "ADD":
                if (!dictionary.containsKey(word)) {
                    if (meanings != null && !meanings.isEmpty()) {
                        dictionary.put(word, meanings);
                        responseData.put("status", "success");
                    } else {
                        responseData.put("status", "error");
                        responseData.put("message", "Meanings cannot be empty");
                    }
                } else {
                    responseData.put("status", "duplicate");
                }
                break;

            case "REMOVE":
                if (dictionary.containsKey(word)) {
                    dictionary.remove(word);
                    responseData.put("status", "success");
                } else {
                    responseData.put("status", "not found");
                }
                break;

            case "UPDATE":
                if (dictionary.containsKey(word) && meanings != null && !meanings.isEmpty()) {
                    dictionary.put(word, meanings);
                    responseData.put("status", "success");
                } else {
                    responseData.put("status", "not found");
                }
                break;

            default:
                responseData.put("status", "error");
                responseData.put("message", "Invalid command");
        }

        return responseData;
    }
}

