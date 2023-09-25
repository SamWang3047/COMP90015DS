package Assignment2;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class TicTacToeServer {
    private static final int PORT = 10240;
    private static final int MAX_PLAYERS = 100;  // Adjust based on your expected number of concurrent players
    public static ConcurrentLinkedQueue<Player> waitingPlayers = new ConcurrentLinkedQueue<>();
    public static ConcurrentHashMap<String, PlayerData> playerDatabase = new ConcurrentHashMap<>();
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
    // Add this method to TicTacToeServer.java
    // In TicTacToeServer.java
    public static void updatePlayerRanks() {
        List<Map.Entry<String, PlayerData>> sortedEntries = new ArrayList<>(playerDatabase.entrySet());

        sortedEntries.sort((e1, e2) -> Integer.compare(e2.getValue().getPoints(), e1.getValue().getPoints())); // Sort in descending order of points

        int rank = 1;
        for (Map.Entry<String, PlayerData> entry : sortedEntries) {
            entry.getValue().setRank(rank++);
        }
    }

}


