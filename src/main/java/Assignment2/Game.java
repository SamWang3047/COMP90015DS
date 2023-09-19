package Assignment2;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Random;

public class Game extends Thread {
    private Player player1;
    private Player player2;
    private char[][] board = new char[3][3];
    private char currentPlayerMark;
    private GameState currentState;

    public Game(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = '-';
            }
        }
        Random random = new Random();
        if (random.nextBoolean()) {
            player1.mark = 'X';
            player2.mark = 'O';
        } else {
            player1.mark = 'O';
            player2.mark = 'X';
        }
        currentPlayerMark = 'X'; // Let's start with 'X'
        this.currentState = GameState.WAITING_FOR_PLAYER;
    }

    @Override
    public void run() {
        player1.out.println(createJsonMessage("status", "START", "mark", String.valueOf(player1.mark)));
        player2.out.println(createJsonMessage("status", "START", "mark", String.valueOf(player2.mark)));
        while (true) {
            handlePlayerMove(player1);
            handlePlayerMove(player2);
            // Handle game logic, communication between players, and checking for win conditions
            if (checkWin()) {
                // Inform players about the winner and break out of the loop
                if (currentPlayerMark == 'X') {
                    currentState = GameState.PLAYER_X_WON;
                } else {
                    currentState = GameState.PLAYER_O_WON;
                }
                String winnerMessage = currentPlayerMark + " wins!";
                player1.out.println(createJsonMessage("status", winnerMessage));
                player2.out.println(createJsonMessage("status", winnerMessage));
                break;
            } else if (checkDraw()) {
                currentState = GameState.DRAW;
                // Inform players about the draw and break out of the loop
                player1.out.println(createJsonMessage("status", "Draw"));
                player2.out.println(createJsonMessage("status", "Draw"));
                break;
            }
            switchPlayer();
        }
    }

    private boolean checkWin() {
        return (checkRows() || checkColumns() || checkDiagonals());
    }

    private boolean checkRows() {
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == currentPlayerMark && board[i][1] == currentPlayerMark && board[i][2] == currentPlayerMark) {
                return true;
            }
        }
        return false;
    }

    private boolean checkColumns() {
        for (int i = 0; i < 3; i++) {
            if (board[0][i] == currentPlayerMark && board[1][i] == currentPlayerMark && board[2][i] == currentPlayerMark) {
                return true;
            }
        }
        return false;
    }

    private boolean checkDiagonals() {
        return ((board[0][0] == currentPlayerMark && board[1][1] == currentPlayerMark && board[2][2] == currentPlayerMark) ||
                (board[0][2] == currentPlayerMark && board[1][1] == currentPlayerMark && board[2][0] == currentPlayerMark));
    }

    private boolean checkDraw() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == '-') {
                    return false;
                }
            }
        }
        return true;
    }

    private void switchPlayer() {
        currentPlayerMark = (currentPlayerMark == 'X') ? 'O' : 'X';
    }
    private String createJsonMessage(String key, String value) {
        JSONObject json = new JSONObject();
        json.put(key, value);
        return json.toJSONString();
    }
    private String createJsonMessage(String key1, String value1, String key2, String value2) {
        JSONObject json = new JSONObject();
        json.put(key1, value1);
        json.put(key2, value2);
        return json.toJSONString();
    }

    private void handlePlayerMove(Player player) {
        try {
            String message = player.in.readLine(); // Assuming 'in' is a BufferedReader in the Player class
            JSONObject json = (JSONObject) new JSONParser().parse(message);
            String action = (String) json.get("action");
            if ("move".equals(action)) {
                int row = ((Long) json.get("row")).intValue();
                int col = ((Long) json.get("col")).intValue();
                makeMove(row, col);
                sendUpdatedBoardToClients();
                // After making the move, update both clients with the new game state
                updateClients();
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
            // Handle exceptions (e.g., player disconnection)
        }
    }

    private void makeMove(int row, int col) {
        if (board[row][col] == '-') {
            board[row][col] = currentPlayerMark;
        }
        // You can also add checks to ensure the move is valid
    }

    private void sendUpdatedBoardToClients() {
        JSONObject boardJson = new JSONObject();
        boardJson.put("action", "updateBoard");
        boardJson.put("board", convertBoardToString());
        player1.out.println(boardJson.toJSONString());
        player2.out.println(boardJson.toJSONString());
    }

    private String convertBoardToString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                sb.append(board[i][j]);
            }
        }
        return sb.toString();
    }

    private void updateClients() {
        // Convert the current game state to a JSONObject
        JSONObject gameStateJson = new JSONObject();
        // Add game state details to the gameState object
        gameStateJson.put("gameState", currentState.getDescription());
        // Send the gameState to both players
        player1.out.println(gameStateJson.toJSONString());
        player2.out.println(gameStateJson.toJSONString());
    }



}


