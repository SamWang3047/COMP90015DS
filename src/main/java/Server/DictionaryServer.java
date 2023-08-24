package Server;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DictionaryServer {
    private static Map<String, String> dictionary = new HashMap<>();
    private static String dicPath = "";
    private static int port = 0;
    private static final String DEFAULT_DICTIONARY_PATH = "src/Dictionary.json";

    public static void main(String[] args) {
        validation(args);
        loadDictionaryFromFile(dicPath);
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server is listening on port " + port);

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

    private static void validation(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java -jar <port> <filepath>");
            System.exit(1);
        }

        try {
            port = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.err.println("Error: Invalid port number");
            System.exit(1);
        }

        if (port < 1 || port > 65535) {
            System.err.println("Error: Invalid port number");
            System.exit(1);
        }

        dicPath = args[1];
        File file = new File(dicPath);

        //if dictionary file path is not valid, the path will be set into a default value and a new dictionary will be created.
        if (!file.exists() || !file.canRead()) {
            System.err.println("Error: Invalid dictionary file path");
            dicPath = DEFAULT_DICTIONARY_PATH;
            createEmptyDictionaryFile();
        }
    }

    private static void loadDictionaryFromFile(String dicPath) {
        JSONParser parser = new JSONParser();
        try {
            FileReader fileReader = new FileReader(dicPath);
            JSONObject jsonObject = (JSONObject) parser.parse(fileReader);

            // Assuming the JSON file has a key-value structure like {"word": "meaning"}
            for (Object key : jsonObject.keySet()) {
                String word = (String) key;
                String meaning = (String) jsonObject.get(key);
                dictionary.put(word, meaning);
            }

            fileReader.close();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }


    private static void createEmptyDictionaryFile() {
        try (FileWriter fileWriter = new FileWriter(dicPath)) {
            fileWriter.write("{}"); // An empty JSON object
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

