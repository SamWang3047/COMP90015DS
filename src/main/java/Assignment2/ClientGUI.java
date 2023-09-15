package Assignment2;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

    public ClientGUI() {
        // Initialize the game board to be empty and display "Finding Player" message
        initializeBoard();
        playerTurn.setText("Finding Player");

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
                clickedButton.setText("X");  // For demonstration purposes, always sets "X"
                clickedButton.setEnabled(false);
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
        frame.setContentPane(new ClientGUI().gamePanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
