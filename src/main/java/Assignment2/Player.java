package Assignment2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Player extends Thread {
    private Socket socket;
    PrintWriter out;
    BufferedReader in;
    private String name;

    public Player(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            name = in.readLine();
            out.println("Welcome " + name + "! Waiting for another player...");

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
}
