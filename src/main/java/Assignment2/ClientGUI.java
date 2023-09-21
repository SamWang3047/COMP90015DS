package Assignment2;

import org.json.simple.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class ClientGUI {
    private JPanel gamePanel;
    private JTextArea usernameArea;
    private JButton quitButton;
    private JTextArea userMessageArea;
    private JLabel playerChat;
    private JLabel gameName;
    private JTextArea Timer;
    private JTextArea playerTurn;
    private JButton button1;
    private JButton button2;
    private JButton button3;
    private JButton button4;
    private JButton button5;
    private JButton button6;
    private JButton button7;
    private JButton button8;
    private JButton button9;
    private JTextArea userMessageInputArea;
    private JLabel timer;
    // below is the Non-UI variables
    private BufferedWriter writer;
    private BufferedReader reader;
    private char[][] localBoard = new char[3][3];
    private boolean isMyTurn = false;
    private String username;
    private String opponentName;



    public ClientGUI(BufferedWriter writer, BufferedReader reader, String username) {
        this.writer = writer;
        this.reader = reader;
        this.username = username;
        usernameArea.setText("username: " + username);
        // Initialize the game board to be empty and display "Finding Player" message
        initializeBoard();
        playerTurn.setText("Finding Player");
        setButtonCommands();
        // Quit button functionality
        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        // Example functionality for game buttons
        ActionListener gameButtonListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JButton clickedButton = (JButton) e.getSource();
                String actionCommand = clickedButton.getActionCommand();
                String[] position = actionCommand.split(",");
                int row = Integer.parseInt(position[0]);
                int col = Integer.parseInt(position[1]);
                sendMoveToServer(row, col);
            }
        };
        addButtonListeners(gameButtonListener);
    }

    private void initializeBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                localBoard[i][j] = '-';
            }
        }
        button1.setText("-");
        button2.setText("-");
        button3.setText("-");
        button4.setText("-");
        button5.setText("-");
        button6.setText("-");
        button7.setText("-");
        button8.setText("-");
        button9.setText("-");
    }
    private void setButtonCommands() {
        button1.setActionCommand("0,0");
        button2.setActionCommand("0,1");
        button3.setActionCommand("0,2");
        button4.setActionCommand("1,0");
        button5.setActionCommand("1,1");
        button6.setActionCommand("1,2");
        button7.setActionCommand("2,0");
        button8.setActionCommand("2,1");
        button9.setActionCommand("2,2");
    }
    private void addButtonListeners(ActionListener listener) {
        button1.addActionListener(listener);
        button2.addActionListener(listener);
        button3.addActionListener(listener);
        button4.addActionListener(listener);
        button5.addActionListener(listener);
        button6.addActionListener(listener);
        button7.addActionListener(listener);
        button8.addActionListener(listener);
        button9.addActionListener(listener);
    }

    public void init() {
        JFrame frame = new JFrame("ClientGUI");
        frame.setContentPane(this.gamePanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        Font buttonFont = new Font("Arial", Font.BOLD, 80);

        button1.setFont(buttonFont);
        button2.setFont(buttonFont);
        button3.setFont(buttonFont);
        button4.setFont(buttonFont);
        button5.setFont(buttonFont);
        button6.setFont(buttonFont);
        button7.setFont(buttonFont);
        button8.setFont(buttonFont);
        button9.setFont(buttonFont);
    }
    private void sendMoveToServer(int row, int col) {
        if (!isMyTurn) {
            JOptionPane.showMessageDialog(null, "It's not your turn!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (localBoard[row][col] != '-') {
            JOptionPane.showMessageDialog(null, "Invalid move! Cell is already occupied.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JSONObject moveJson = new JSONObject();
        moveJson.put("action", "move");
        moveJson.put("row", row);
        moveJson.put("col", col);

        try {
            // Convert the JSONObject to a string and send it to the server
            writer.write(moveJson.toJSONString());
            writer.newLine();  // Important to add a newline for the server's BufferedReader to know when the message ends
            writer.flush();    // Ensure the message is actually sent out
        } catch (IOException e) {
            e.printStackTrace();
            // Handle any IO exceptions here, such as showing an error message to the user or attempting to reconnect
        }
    }
    public void updateGUI(JSONObject receivedJson) {
        String action = (String) receivedJson.get("action");
        if ("updateBoard".equals(action)) {
            String boardString = (String) receivedJson.get("board");
            updateBoard(boardString);
        }

        String status = (String) receivedJson.get("status");
        if ("START".equals(status)) {
            String mark = (String) receivedJson.get("mark");
            opponentName = (String) receivedJson.get("opponentName"); // Get opponent's name from the server
            isMyTurn = mark.equals("X"); // If the player is 'X', they start the game
            playerTurn.setText(isMyTurn ? username + "'s turn" : opponentName + "'s turn"); // Set the name based on the turn
            JOptionPane.showMessageDialog(null, "Game Started! You hold: " + mark + ". Your opponent is: " + opponentName, "Game Start", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if ("Invalid move! Cell is already occupied.".equals(status) || "Not your turn!".equals(status)) {
            isMyTurn = false; // If the move was invalid or it's not their turn, set isMyTurn to false
        } else {
            isMyTurn = !isMyTurn; // Toggle the turn after a valid move
            playerTurn.setText(isMyTurn ? username + "'s turn" : opponentName + "'s turn"); // Set the name based on the turn
        }

        String gameStateDescription = (String) receivedJson.get("status");
        if (GameState.MOVE_ERROR.getDescription().equals(gameStateDescription)) {
            JOptionPane.showMessageDialog(null, "Invalid move! Cell is already occupied.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        GameState receivedState;
        if (gameStateDescription != null) {
            try {
                receivedState = GameState.valueOf(gameStateDescription.replace(" ", "_").toUpperCase());
                switch (receivedState) {
                    case WAITING_FOR_PLAYER:
                        playerTurn.setText("Waiting for another player...");
                        break;
                    case IN_PROGRESS:
                        break;
                    case PLAYER_X_WON:
                        playerTurn.setText("Player X won!");
                        JOptionPane.showMessageDialog(null, "Player X (" + receivedJson.get("playerName") + ") has won!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
                        break;
                    case PLAYER_O_WON:
                        playerTurn.setText("Player O won!");
                        JOptionPane.showMessageDialog(null, "Player O (" + receivedJson.get("playerName") + ") has won!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
                        break;
                    case DRAW:
                        playerTurn.setText("It's a draw!");
                        JOptionPane.showMessageDialog(null, "Draw!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
                        break;
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        setButtonsEnabled(isMyTurn);
    }
    private synchronized void updateBoard(String boardString) {
        char[][] board = new char[3][3];
        int index = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = boardString.charAt(index++);
            }
        }

        button1.setText(String.valueOf(board[0][0]));
        button2.setText(String.valueOf(board[0][1]));
        button3.setText(String.valueOf(board[0][2]));
        button4.setText(String.valueOf(board[1][0]));
        button5.setText(String.valueOf(board[1][1]));
        button6.setText(String.valueOf(board[1][2]));
        button7.setText(String.valueOf(board[2][0]));
        button8.setText(String.valueOf(board[2][1]));
        button9.setText(String.valueOf(board[2][2]));

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                localBoard[i][j] = board[i][j];
            }
        }
    }

    private void setButtonsEnabled(boolean enabled) {
        button1.setEnabled(enabled);
        button2.setEnabled(enabled);
        button3.setEnabled(enabled);
        button4.setEnabled(enabled);
        button5.setEnabled(enabled);
        button6.setEnabled(enabled);
        button7.setEnabled(enabled);
        button8.setEnabled(enabled);
        button9.setEnabled(enabled);
    }
}
