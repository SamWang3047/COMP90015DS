package Client;

import Server.StateCode;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;


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
                if (isOnlyChar(word) && !word.isEmpty()) {
                    try {
                        String meaning = dictionaryClient.queryWord(word);
                        if (meaning != null) {
                            resultArea.append("From " + dictionaryClient.getSocket().getInetAddress().toString() + "\n");
                            resultArea.append(printTitle(StateCode.QUERY,StateCode.SUCCESS)+ "\n");
                            resultArea.append("-Word: " + word + "\n");
                            resultArea.append("-Meaning: " + meaning+ "\n\n");
                        } else {
                            resultArea.append("From " + dictionaryClient.getSocket().getInetAddress().toString() + "\n");
                            resultArea.append(printTitle(StateCode.QUERY,StateCode.FAIL)+ "\n");
                            resultArea.append("There is no such word in dictionary." + "\n\n");
                        }
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
                if (isOnlyChar(word) && !meanings.isEmpty()) {
                    try {
                        boolean success = dictionaryClient.addWord(word, meanings);
                        resultArea.append("From " + dictionaryClient.getSocket().getInetAddress().toString() + "\n");
                        if (success) {
                            resultArea.append(printTitle(StateCode.ADD,StateCode.SUCCESS)+ "\n\n");
                        } else {
                            resultArea.append(printTitle(StateCode.ADD,StateCode.FAIL)+ "\n");
                            resultArea.append("Word: \"" + word + "\" already exist!" + "\n\n");
                        }
                    } catch (IOException | ParseException ex) {
                        ex.printStackTrace();
                    }
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
                if (isOnlyChar(word) && !word.isEmpty()) {
                    try {
                        boolean success = dictionaryClient.removeWord(word);
                        resultArea.append("From " + dictionaryClient.getSocket().getInetAddress().toString() + "\n");
                        if (success) {
                            resultArea.append(printTitle(StateCode.REMOVE, StateCode.SUCCESS) + "\n\n");
                        } else {
                            resultArea.append(printTitle(StateCode.REMOVE, StateCode.FAIL) + "\n");
                            resultArea.append("There is no such word in dictionary." + "\n\n");
                        }
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
                if (isOnlyChar(word) && !newMeanings.isEmpty()) {
                    try {
                        dictionaryClient.updateWord(word, newMeanings);
                        resultArea.append("From " + dictionaryClient.getSocket().getInetAddress().toString() + "\n");
                        resultArea.append(printTitle(StateCode.UPDATE, StateCode.SUCCESS) + "\n\n");
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
}
