package Client;

import Server.StateCode;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class DictionaryClient {
    private static String address = ""; // Change to server IP if needed
    private static int serverPort = 0; // Change to server port if needed

    public static void main(String[] args) {
        validation(args);
        try (Socket socket = new Socket(address, serverPort);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

            Scanner scanner = new Scanner(System.in);
            while (true) {
                try {
                    run(scanner, reader, writer);
                } catch (IOException e) {
                    System.out.println("Input Exception, please try again!");
                    e.printStackTrace();
                } catch (ParseException e) {
                    System.out.println("Parse Exception");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void run(Scanner scanner, BufferedReader reader, BufferedWriter writer) throws IOException, ParseException {

        System.out.println("-----------------------------------");
        System.out.println("Choose an option:");
        System.out.println("1. Query a word");
        System.out.println("2. Add a word");
        System.out.println("3. Remove a word");
        System.out.println("4. Update a word");
        System.out.print("Enter your choice: ");

        int choice = scanner.nextInt() - 1;
        scanner.nextLine(); // Consume newline

        JSONObject request = new JSONObject();

        switch (choice) {
            case StateCode.QUERY:
                System.out.print("Enter the word to query: ");
                String wordToQuery = scanner.nextLine();
                request = createQueryRequest(StateCode.QUERY, wordToQuery, null);
                break;
            case StateCode.ADD:
                System.out.print("Enter the word to add: ");
                String wordToAdd = scanner.nextLine();
                System.out.print("Enter the meanings: ");
                String meaningsToAdd = scanner.nextLine();
                request = createQueryRequest(StateCode.ADD, wordToAdd, meaningsToAdd);
                break;
            case StateCode.REMOVE:
                System.out.print("Enter the word to remove: ");
                String wordToRemove = scanner.nextLine();
                request = createQueryRequest(StateCode.REMOVE, wordToRemove, null);
                break;
            case StateCode.UPDATE:
                System.out.print("Enter the word to update: ");
                String wordToUpdate = scanner.nextLine();
                System.out.print("Enter the new meanings: ");
                String newMeanings = scanner.nextLine();
                request = createQueryRequest(StateCode.UPDATE, wordToUpdate, newMeanings);
                break;
            default:
                System.out.println("Invalid choice.");
        }

        if (!request.isEmpty()) {
            sendRequest(writer, request);
            String response = reader.readLine();
            JSONObject jsonResponse = (JSONObject) new JSONParser().parse(response);
            processResponse(jsonResponse);
        }
    }

    private static JSONObject createQueryRequest(int command, String word, String meanings) {
        JSONObject request = new JSONObject();
        request.put("command", command);
        request.put("word", word);
        request.put("meanings", meanings);
        return request;
    }

    private static void sendRequest(BufferedWriter writer, JSONObject request) throws IOException {
        writer.write(request.toJSONString() + "\n");
        writer.flush();
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
}
