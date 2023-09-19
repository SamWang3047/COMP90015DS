package Assignment2;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Player extends Thread {
    private Socket socket;
    PrintWriter out;
    BufferedReader in;
    private String username;
    private JSONParser parser = new JSONParser();
    public char mark;

    public Player(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Read the name from a JSON message
            JSONObject json = (JSONObject) parser.parse(in.readLine());
            username = (String) json.get("username");
            System.out.println(username);

            // Send a welcome message in JSON format
            JSONObject welcomeMessage = new JSONObject();
            welcomeMessage.put("message", "Welcome " + username + "! Waiting for another player...");
            out.println(welcomeMessage.toJSONString());

            TicTacToeServer.waitingPlayers.add(this);

            while (true) {
                if (TicTacToeServer.waitingPlayers.size() >= 2 && TicTacToeServer.waitingPlayers.contains(this)) {
                    Player opponent = TicTacToeServer.waitingPlayers.poll();
                    if (opponent != this) {
                        TicTacToeServer.waitingPlayers.remove(this);
                        new Game(this, opponent).start();
                        break;
                    } else {
                        TicTacToeServer.waitingPlayers.add(opponent);
                    }
                }
                Thread.sleep(100);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }
}
