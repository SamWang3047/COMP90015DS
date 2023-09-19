package Assignment2;

import org.json.simple.JSONObject;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class ClientGUI {
    private JPanel gamePanel;
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
    private BufferedWriter writer;
    private BufferedReader reader;

    public ClientGUI(BufferedWriter writer, BufferedReader reader) {
        this.writer = writer;
        this.reader = reader;
        // Initialize the game board to be empty and display "Finding Player" message
        initializeBoard();
        playerTurn.setText("Finding Player");
        button1.setActionCommand("0,0");
        button2.setActionCommand("0,1");
        button3.setActionCommand("0,2");
        button4.setActionCommand("1,0");
        button5.setActionCommand("1,1");
        button6.setActionCommand("1,2");
        button7.setActionCommand("2,0");
        button8.setActionCommand("2,1");
        button9.setActionCommand("2,2");


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

        button1.addActionListener(gameButtonListener);
        button2.addActionListener(gameButtonListener);
        button3.addActionListener(gameButtonListener);
        button4.addActionListener(gameButtonListener);
        button5.addActionListener(gameButtonListener);
        button6.addActionListener(gameButtonListener);
        button7.addActionListener(gameButtonListener);
        button8.addActionListener(gameButtonListener);
        button9.addActionListener(gameButtonListener);
    }

    private void initializeBoard() {
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

    public void init() {
        JFrame frame = new JFrame("ClientGUI");
        frame.setContentPane(this.gamePanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
    private void sendMoveToServer(int row, int col) {
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
        String status = (String) receivedJson.get("status");
        if ("START".equals(status)) {
            String mark = (String) receivedJson.get("mark");
            playerTurn.setText("Game Started! You are: " + mark);
            return;
        }

        String gameStateDescription = (String) receivedJson.get("gameState");
        GameState receivedState;
        try {
             receivedState = GameState.valueOf(gameStateDescription.replace(" ", "_").toUpperCase());
            switch (receivedState) {
                case WAITING_FOR_PLAYER:
                    playerTurn.setText("Waiting for another player...");
                    break;
                case IN_PROGRESS:
                    // Update the board and other game details
                    break;
                case PLAYER_X_WON:
                    playerTurn.setText("Player X won!");
                    break;
                case PLAYER_O_WON:
                    playerTurn.setText("Player O won!");
                    break;
                case DRAW:
                    playerTurn.setText("It's a draw!");
                    break;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        if ("updateBoard".equals(receivedJson.get("action"))) {
            String boardString = (String) receivedJson.get("board");
            updateBoardFromReceivedString(boardString);
        }


    }
    private void updateBoardFromReceivedString(String boardString) {
        JButton[] buttons = {button1, button2, button3, button4, button5, button6, button7, button8, button9};
        for (int i = 0; i < boardString.length(); i++) {
            buttons[i].setText(String.valueOf(boardString.charAt(i)));
        }
    }

}
