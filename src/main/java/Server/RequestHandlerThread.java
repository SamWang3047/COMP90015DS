package Server;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;

public class RequestHandlerThread extends Thread {
    private Socket clientSocket;
    private DictionaryServer dicServer;
    private JSONParser jsonParser = new JSONParser();
    private static final Object dictionaryLock = new Object(); // Lock for dictionary access

    public RequestHandlerThread(Socket clientSocket, DictionaryServer dicServer) {
        this.clientSocket = clientSocket;
        this.dicServer = dicServer;
    }

    @Override
    public void run() {
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
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private JSONObject processRequest(String request) throws ParseException {
        JSONObject requestData = (JSONObject) jsonParser.parse(request);
        JSONObject responseData = new JSONObject();

        int command = Integer.parseInt(requestData.get("command").toString());
        String word = (String) requestData.get("word");
        String meanings = (String) requestData.get("meanings");

        synchronized (dicServer.getDictionary()) {
            switch (command) {
                case StateCode.QUERY:
                    String meaning = dicServer.getDictionary().get(word);
                    if (meaning != null) {
                        responseData.put("status", StateCode.SUCCESS);
                        responseData.put("meanings", meaning);
                    } else {
                        responseData.put("status", StateCode.NOT_FOUND);
                    }
                    break;

                case StateCode.ADD:
                    if (!dicServer.getDictionary().containsKey(word)) {
                        if (meanings != null && !meanings.isEmpty()) {
                            dicServer.getDictionary().put(word, meanings);
                            saveDictionaryToFile();
                            responseData.put("status", StateCode.SUCCESS);
                        } else {
                            responseData.put("status", StateCode.EMPTY_MEANING);
                            responseData.put("message", "Meanings cannot be empty");
                        }
                    } else {
                        responseData.put("status", StateCode.DUPLICATE);
                    }
                    break;

                // Handle other commands (REMOVE, UPDATE) similarly

                default:
                    responseData.put("status", StateCode.UNKNOWN);
                    responseData.put("message", "Unknown Error");
            }
        }

        return responseData;
    }

    private void saveDictionaryToFile() {
        JSONObject jsonObject = new JSONObject();
        synchronized (dictionaryLock) {
            for (String word : dicServer.getDictionary().keySet()) {
                jsonObject.put(word, dicServer.getDictionary().get(word));
            }
        }

        try (FileWriter fileWriter = new FileWriter(dicServer.getDicPath())) {
            fileWriter.write(jsonObject.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

