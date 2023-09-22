package Assignment2;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.*;

public class TicTacToeServer {
    private static final int PORT = 10240;
    private static final int MAX_PLAYERS = 100;  // Adjust based on your expected number of concurrent players
    public static ConcurrentLinkedQueue<Player> waitingPlayers = new ConcurrentLinkedQueue<>();
    private static ExecutorService threadPool = Executors.newFixedThreadPool(MAX_PLAYERS);

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("TicTacToe Server started on port " + PORT);
            while (true) {
                Player player = new Player(serverSocket.accept());
                threadPool.execute(player);

                // Pair two players together if possible
//                synchronized(waitingPlayers) {
//                    if (waitingPlayers.size() >= 2) {
//                        Player player1 = waitingPlayers.poll();
//                        Player player2 = waitingPlayers.poll();
//
//                        if (player1 != null && player2 != null) {
//                            Game game = new Game(player1, player2);
//                            threadPool.execute(game);
//                        }
//                    }
//                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


