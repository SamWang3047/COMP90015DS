package Assignment1.Client;

import Assignment1.Server.StateCode;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Author: Zhiyuan Wang, StudentID: 1406985,  COMP90015
public class DictionaryClientGUI {
    private DictionaryClient dictionaryClient;
    private JTextArea resultArea;
    private JButton addButton;
    private JButton updateButton;
    private JButton quitButton;
    private JButton removeButton;
    private JButton searchButton;
    private JTextField inputArea;
    private JTextArea meaningArea;
    private JPanel dictionaryPanel;


    public DictionaryClientGUI(DictionaryClient dictionaryClient) {
        this.dictionaryClient = dictionaryClient;
        init();
    }

    public void init() {
        JFrame frame = new JFrame("DictionaryClient");
        frame.setContentPane(dictionaryPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        resultArea.setLineWrap(true);
        meaningArea.setLineWrap(true);
        resultArea.setEditable(false);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String word = inputArea.getText().trim();
                StringBuilder sb = new StringBuilder();
                if (isOnlyChar(word) && !word.isEmpty()) {
                    try {
                        String meaning = dictionaryClient.queryWord(word);
                        if (meaning != null) {
                            resultArea.append("From " + dictionaryClient.getSocket().getInetAddress().toString() + "\n");
                            sb.append("From " + dictionaryClient.getSocket().getInetAddress().toString() + "\n");
                            resultArea.append(printTitle(StateCode.QUERY,StateCode.SUCCESS)+ "\n");
                            sb.append(printTitle(StateCode.QUERY,StateCode.SUCCESS)+ "\n");
                            resultArea.append("-Word: " + word + "\n");
                            sb.append("-Word: " + word + "\n");
                            resultArea.append("-Meaning: " + meaning+ "\n\n");
                            sb.append("-Meaning: " + meaning+ "\n\n");
                        } else {
                            resultArea.append("From " + dictionaryClient.getSocket().getInetAddress().toString() + "\n");
                            resultArea.append(printTitle(StateCode.QUERY,StateCode.FAIL)+ "\n");
                            resultArea.append("There is no such word in dictionary." + "\n\n");
                            sb.append("From " + dictionaryClient.getSocket().getInetAddress().toString() + "\n");
                            sb.append(printTitle(StateCode.QUERY,StateCode.FAIL)+ "\n");
                            sb.append("There is no such word in dictionary." + "\n\n");
                        }
                        logClientBehavior(sb.toString());
                    } catch (IOException | ParseException ex) {
                        JOptionPane.showMessageDialog(null, "Error searching word: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        printTitle(StateCode.QUERY,StateCode.FAIL);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid input. Word must contain only letters and meanings cannot be empty.",
                            "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    printTitle(StateCode.QUERY,StateCode.FAIL);
                }
            }
        });
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String word = inputArea.getText().trim();
                String meanings = meaningArea.getText().trim();
                StringBuilder sb = new StringBuilder();
                if (isOnlyChar(word) && !meanings.isEmpty()) {
                    try {
                        boolean success = dictionaryClient.addWord(word, meanings);
                        resultArea.append("From " + dictionaryClient.getSocket().getInetAddress().toString() + "\n");
                        sb.append("From " + dictionaryClient.getSocket().getInetAddress().toString() + "\n");
                        if (success) {
                            resultArea.append(printTitle(StateCode.ADD,StateCode.SUCCESS)+ "\n\n");
                            sb.append(printTitle(StateCode.ADD,StateCode.SUCCESS)+ "\n\n");
                        } else {
                            resultArea.append(printTitle(StateCode.ADD,StateCode.FAIL)+ "\n");
                            sb.append(printTitle(StateCode.ADD,StateCode.FAIL)+ "\n");
                            resultArea.append("Word: \"" + word + "\" already exist!" + "\n\n");
                            sb.append("Word: \"" + word + "\" already exist!" + "\n\n");
                        }
                    } catch (IOException | ParseException ex) {
                        ex.printStackTrace();
                    }
                    logClientBehavior(sb.toString());
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid input. Word must contain only letters and meanings cannot be empty.",
                            "Invalid Input", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String word = inputArea.getText().trim();
                StringBuilder sb = new StringBuilder();
                if (isOnlyChar(word) && !word.isEmpty()) {
                    try {
                        boolean success = dictionaryClient.removeWord(word);
                        resultArea.append("From " + dictionaryClient.getSocket().getInetAddress().toString() + "\n");
                        sb.append("From " + dictionaryClient.getSocket().getInetAddress().toString() + "\n");
                        if (success) {
                            resultArea.append(printTitle(StateCode.REMOVE, StateCode.SUCCESS) + "\n\n");
                            sb.append(printTitle(StateCode.REMOVE, StateCode.SUCCESS) + "\n\n");
                        } else {
                            resultArea.append(printTitle(StateCode.REMOVE, StateCode.FAIL) + "\n");
                            resultArea.append("There is no such word in dictionary." + "\n\n");
                            sb.append(printTitle(StateCode.REMOVE, StateCode.FAIL) + "\n");
                            sb.append("There is no such word in dictionary." + "\n\n");
                        }
                        logClientBehavior(sb.toString());
                    } catch (IOException | ParseException ex) {
                        JOptionPane.showMessageDialog(null, "Error removing word: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        printTitle(StateCode.REMOVE, StateCode.FAIL);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid input. Word must contain only letters.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    printTitle(StateCode.REMOVE, StateCode.FAIL);
                }
            }
        });
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String word = inputArea.getText().trim();
                String newMeanings = meaningArea.getText().trim();
                StringBuilder sb = new StringBuilder();
                if (isOnlyChar(word) && !newMeanings.isEmpty()) {
                    try {
                        dictionaryClient.updateWord(word, newMeanings);
                        resultArea.append("From " + dictionaryClient.getSocket().getInetAddress().toString() + "\n");
                        resultArea.append(printTitle(StateCode.UPDATE, StateCode.SUCCESS) + "\n\n");
                        sb.append("From " + dictionaryClient.getSocket().getInetAddress().toString() + "\n");
                        sb.append(printTitle(StateCode.UPDATE, StateCode.SUCCESS) + "\n\n");
                        logClientBehavior(sb.toString());
                    } catch (IOException | ParseException ex) {
                        JOptionPane.showMessageDialog(null, "Error updating word: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        printTitle(StateCode.UPDATE, StateCode.FAIL);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid input. Word must contain only letters and meanings cannot be empty.",
                            "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    printTitle(StateCode.UPDATE, StateCode.FAIL);
                }
            }
        });
        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int choice = JOptionPane.showConfirmDialog(null, "Are you sure you want to quit?", "Quit Confirmation", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    logClientBehavior("User Quit");
                    System.exit(0); // Close the application
                }
            }
        });
    }

    private boolean isOnlyChar(String str) {
        return str.matches("^([a-zA-Z][\\-]?)*[a-zA-Z]+$");
    }

    private String printTitle(int commandCode, int stateCode) {
        StringBuilder sb = new StringBuilder();
        sb.append("-Command: " + StateCode.codeToWord[commandCode] + "   ");
        sb.append(StateCode.codeToWord[stateCode]);
        return sb.toString();
    }

    /**
     * Store the log of user input and the result from server. User can get this file to check their use history
     * @param str the user action
     */

    private void logClientBehavior(String str) {
        try {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String timestamp = now.format(formatter);

            String logEntry = timestamp + "\n" + str + "\n";
            FileWriter writer = new FileWriter("client_log.txt", true); // Append to the log file
            writer.write(logEntry);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
