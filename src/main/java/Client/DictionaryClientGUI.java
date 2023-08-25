package Client;

import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class DictionaryClientGUI {
    private DictionaryClient dictionaryClient;
    private JTextField resultArea;
    private JButton addButton;
    private JButton updateButton;
    private JButton quitButton;
    private JButton removeButton;
    private JButton searchButton;
    private JTextField inputArea;
    private JTextField meaningArea;
    private JPanel dictionaryPanel;

    public DictionaryClientGUI(DictionaryClient dictionaryClient) {
        this.dictionaryClient = dictionaryClient;
        init();
    }

    public void init() {
        JFrame frame = new JFrame("DictionaryClientGUIForm");
        frame.setContentPane(dictionaryPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String word = inputArea.getText();
                String meanings = meaningArea.getText();

                if (isOnlyChar(word) && !meanings.isEmpty()) {
                    try {
                        dictionaryClient.addWord(word, meanings);
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

            }
        });
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    private boolean isOnlyChar(String str) {
        return str.matches("^([a-zA-Z][\\-]?)*[a-zA-Z]+$");
    }
}
