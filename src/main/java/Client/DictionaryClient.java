package Client;

import Server.StateCode;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.Socket;

public class DictionaryClient {
    private static String address = ""; // Change to server IP if needed
    private static int serverPort = 0; // Change to server port if needed

    public static void main(String[] args) {
        validation(args);
        try (Socket socket = new Socket(address, serverPort);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

            JSONObject query = createQueryRequest(StateCode.QUERY, "apple", null);
            sendRequest(writer, query);

            String response = reader.readLine();
            JSONObject jsonResponse = (JSONObject) new JSONParser().parse(response);
            processResponse(jsonResponse);

        } catch (IOException | ParseException e) {
            e.printStackTrace();
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
                // Process other response data as needed
                break;
            case StateCode.FAIL:
                System.out.println("Operation failed");
                String message = response.get("message").toString();
                System.out.println("Error message: " + message);
                break;
            // Handle other response statuses as needed
            default:
                System.out.println("Unexpected response status");
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

