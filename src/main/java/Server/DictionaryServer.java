package Server;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DictionaryServer {
    private static Map<String, String> dictionary = new HashMap<>();
    private static String dicPath = "";
    private static int port = 0;
    private static final String DEFAULT_DICTIONARY_PATH = "Dictionary.json";

    private void run(String[] args) {
        validation(args);
        loadDictionaryFromFile(dicPath);

        int corePoolSize = 10; // Adjust the number of threads as needed
        int maxPoolSize = 20;  // Maximum number of threads in the pool
        long keepAliveTime = 60L; // Thread idle time before it's terminated

        ExecutorService threadPool = new ThreadPoolExecutor(
                corePoolSize, maxPoolSize, keepAliveTime,
                TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server is listening on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted connection from " + clientSocket.getInetAddress());
                //thread pool
                threadPool.execute(new RequestHandlerThread(clientSocket, this));
            }
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void validation(String[] args) {
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

        // if the dictionary file path is not valid, the path will be set into a default value and a new dictionary will be created.
        if (!file.exists() || !file.canRead()) {
            System.err.println("Error: Invalid dictionary file path");
            System.out.println("A new empty dictionary has been created");
            dicPath = DEFAULT_DICTIONARY_PATH;
            createEmptyDictionaryFile();
        }
    }
    private void loadDictionaryFromFile(String dicPath) {
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

    private void createEmptyDictionaryFile() {
        try (FileWriter fileWriter = new FileWriter(dicPath)) {
            fileWriter.write("{}"); // An empty JSON object
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        DictionaryServer dictionaryServer = new DictionaryServer();
        dictionaryServer.run(args);
    }

    public Map<String, String> getDictionary() {
        return dictionary;
    }

    public String getDicPath() {
        return dicPath;
    }

    public int getPort() {
        return port;
    }
}
