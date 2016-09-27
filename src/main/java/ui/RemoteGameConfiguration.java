package ui;

import application.Application;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * @author Anastasia Radchenko
 */
public class RemoteGameConfiguration extends AbstractPane implements ActionListener {
    private enum Commands {
        JOIN,
        CANCEL
    }

    private final JTextField tfGameHostname;
    private final JTextField tfPlayerName;

    public RemoteGameConfiguration(Application app) {
        super(app, new BorderLayout(5, 75));

        JLabel l = new JLabel("Remote Game Configuration");
        l.setHorizontalAlignment(JLabel.CENTER);
        add(l, BorderLayout.PAGE_START);

        SpringLayout layout = new SpringLayout();
        JPanel controlsPanel = new JPanel(layout);

        JLabel lGameHostname = new JLabel("Game hostname:");
        lGameHostname.setHorizontalAlignment(JLabel.TRAILING);
        controlsPanel.add(lGameHostname);

        tfGameHostname = new JTextField(20);
        tfGameHostname.setHorizontalAlignment(JTextField.LEADING);
        controlsPanel.add(tfGameHostname);

        layout.putConstraint(SpringLayout.EAST, lGameHostname, 105, SpringLayout.WEST, controlsPanel);
        layout.putConstraint(SpringLayout.NORTH, lGameHostname, 5, SpringLayout.NORTH, controlsPanel);

        layout.putConstraint(SpringLayout.WEST, tfGameHostname, 5, SpringLayout.EAST, lGameHostname);

        JLabel lPlayerName = new JLabel("Player name:");
        lPlayerName.setHorizontalAlignment(JLabel.TRAILING);
        controlsPanel.add(lPlayerName);

        tfPlayerName = new JTextField(20);
        tfPlayerName.setHorizontalAlignment(JTextField.LEADING);
        controlsPanel.add(tfPlayerName);

        layout.putConstraint(SpringLayout.EAST, lPlayerName, 105, SpringLayout.WEST, controlsPanel);
        layout.putConstraint(SpringLayout.NORTH, lPlayerName, 15, SpringLayout.SOUTH, lGameHostname);

        layout.putConstraint(SpringLayout.WEST, tfPlayerName, 5, SpringLayout.EAST, lPlayerName);
        layout.putConstraint(SpringLayout.NORTH, tfPlayerName, 5, SpringLayout.SOUTH, tfGameHostname);

        controlsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 50));
        add(controlsPanel, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2));

        JButton b = new JButton("Join");
        b.setToolTipText("Join remote game");
        b.setVerticalTextPosition(AbstractButton.CENTER);
        b.setHorizontalTextPosition(AbstractButton.CENTER);
        b.setActionCommand(Commands.JOIN.name());
        b.setFocusPainted(false);
        b.addActionListener(this);
        buttonsPanel.add(b);

        b = new JButton("Cancel");
        b.setToolTipText("Return to main menu");
        b.setVerticalTextPosition(AbstractButton.CENTER);
        b.setHorizontalTextPosition(AbstractButton.CENTER);
        b.setActionCommand(Commands.CANCEL.name());
        b.setFocusPainted(false);
        b.addActionListener(this);
        buttonsPanel.add(b);

        add(buttonsPanel, BorderLayout.PAGE_END);

        setBorder(BorderFactory.createEmptyBorder(90, 50, 90, 50));
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        Commands command = Commands.valueOf(evt.getActionCommand());
        switch (command) {
            case JOIN:
                String gameHostname = tfGameHostname.getText();
                String playerName = tfPlayerName.getText();
                if (gameHostname == null || gameHostname.trim().isEmpty()) {
                    warn("Game hostname is required", "Warning");
                    tfGameHostname.selectAll();
                    tfGameHostname.requestFocus();
                } else if (playerName == null || playerName.trim().isEmpty()) {
                    warn("Player name is required", "Warning");
                    tfPlayerName.selectAll();
                    tfPlayerName.requestFocus();
                } else {
                    try {
                        app.remoteGame(gameHostname, playerName);
                        reset();
                    } catch (IOException e) {
                        error("Failed to join remote game: "+e.getMessage(), "Error");
                        cancel();
                    }
                }
                break;
            case CANCEL:
                cancel();
        }
    }

    @Override
    public void reset() {
        tfGameHostname.setText("");
        tfPlayerName.setText("");
    }

    @Override
    public void setFocus() {
        tfGameHostname.requestFocus();
    }

}
