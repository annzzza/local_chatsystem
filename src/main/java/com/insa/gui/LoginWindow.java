package com.insa.gui;

import com.insa.network.discovery.DiscoveryManager;
import com.insa.users.ConnectedUserList;
import com.insa.utils.MyLogger;

import javax.swing.*;
import java.awt.*;

public class LoginWindow {

    private static final MyLogger LOGGER = new MyLogger(LoginWindow.class.getName());


    private final JFrame window = new JFrame();
    private final JPanel panel = new JPanel();
    private final JLabel usernameLabel = new JLabel("Enter your username:");
    private final JTextField inputUsername = new JTextField();
    private final JButton enterButton = new JButton("Enter");
    private final JLabel chooseOtherUsernameLabel = new JLabel();

    public void start() {
        window.setTitle("Salutnomo");
        window.setSize(400, 275);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel.setBackground(new Color(242, 241, 235));

        panel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 10, 10, 10);

        JLabel welcomeText = new JLabel("Welcome");
        welcomeText.setBackground(new Color(242, 241, 235));
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        panel.add(welcomeText, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        panel.add(usernameLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 1;
        inputUsername.setPreferredSize(new Dimension(100, 30));
        panel.add(inputUsername, constraints);

        inputUsername.addActionListener(e -> enterButtonHandler());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(enterButton);
        enterButton.setBackground(new Color(175, 200, 173));
        enterButton.addActionListener(e -> enterButtonHandler());

        constraints.gridx = 1;
        constraints.gridy = 2;
        panel.add(buttonPanel, constraints);

        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 2;
        panel.add(chooseOtherUsernameLabel, constraints);

        window.add(panel);
        window.setVisible(true);
    }

    private void enterButtonHandler() {
        DiscoveryManager nwm = DiscoveryManager.getInstance();

        LOGGER.info("Begin client discovery");

        String username = inputUsername.getText();
        nwm.discoverNetwork(username);

        LOGGER.info("Checking whether the username is already taken...");
        if (ConnectedUserList.getInstance().hasUsername(username)) {
            chooseOtherUsernameLabel.setForeground(Color.RED);
            chooseOtherUsernameLabel.setText("Username not available, please choose a new one.");
        } else {
            chooseOtherUsernameLabel.setForeground(Color.GREEN);
            chooseOtherUsernameLabel.setText("Connected");

            enterButton.setVisible(false);
            MainWindow mainWindow = new MainWindow();
            mainWindow.start(username);

            // Close login window
            window.dispose();
        }
    }
}