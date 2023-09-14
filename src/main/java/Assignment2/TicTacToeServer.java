package Assignment2;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.*;

public class TicTacToeServer {
    private static final int PORT = 8080;
    private static final int MAX_PLAYERS = 100;  // Adjust based on your expected number of concurrent players
    public static ConcurrentLinkedQueue<Player> waitingPlayers = new ConcurrentLinkedQueue<>();
    private static ExecutorService threadPool = Executors.newFixedThreadPool(MAX_PLAYERS);

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("TicTacToe Server started on port " + PORT);
            while (true) {
                Player player = new Player(serverSocket.accept());
                threadPool.execute(player);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


