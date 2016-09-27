package ui;

import application.Application;
import models.PlayerRole;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Anastasia Radchenko
 */
public class NetworkGameConfiguration extends AbstractPane implements ActionListener {
    private static String HOST_IP_ADDRESS = "127.0.0.1";

    static {
        try {
            HOST_IP_ADDRESS =  InetAddress.getLocalHost().toString();
        } catch (UnknownHostException e) {
        }
    }

    private enum Commands {
        INVITE,
        CANCEL
    }

    private final JTextField tfPlayerName;
    private final ButtonGroup rbgPlayerRole;
    private final JRadioButton rbPlayAsOrder;

    public NetworkGameConfiguration(Application app) {
        super(app, new BorderLayout(5, 20));
        setBorder(BorderFactory.createEmptyBorder(90, 40, 90, 40));

        JLabel lPlayerName = new JLabel("Network Game Configuration");
        lPlayerName.setHorizontalAlignment(JLabel.CENTER);
        add(lPlayerName, BorderLayout.PAGE_START);

        SpringLayout layout = new SpringLayout();
        JPanel controlsPanel = new JPanel(layout);

        JLabel line1 = new JLabel("Please ask remote player to join your game at ");
        controlsPanel.add(line1);
        layout.putConstraint(SpringLayout.WEST, line1, 10, SpringLayout.WEST, controlsPanel);
        layout.putConstraint(SpringLayout.NORTH, line1, 5, SpringLayout.NORTH, controlsPanel);

        JLabel line2 = new JLabel(HOST_IP_ADDRESS+" and wait for them to connect.");
        controlsPanel.add(line2);
        layout.putConstraint(SpringLayout.WEST, line2, 10, SpringLayout.WEST, controlsPanel);
        layout.putConstraint(SpringLayout.NORTH, line2, 15, SpringLayout.NORTH, line1);

        lPlayerName = new JLabel("Player name:");
        lPlayerName.setHorizontalAlignment(JLabel.TRAILING);
        controlsPanel.add(lPlayerName);

        tfPlayerName = new JTextField(20);
        tfPlayerName.setHorizontalAlignment(JTextField.LEADING);
        controlsPanel.add(tfPlayerName);

        layout.putConstraint(SpringLayout.EAST, lPlayerName, 100, SpringLayout.WEST, controlsPanel);
        layout.putConstraint(SpringLayout.NORTH, lPlayerName, 35, SpringLayout.SOUTH, line2);

        layout.putConstraint(SpringLayout.WEST, tfPlayerName, 5, SpringLayout.EAST, lPlayerName);
        layout.putConstraint(SpringLayout.NORTH, tfPlayerName, 30, SpringLayout.SOUTH, line2);

        JLabel lPlayerRole = new JLabel("Player role:");
        lPlayerRole.setHorizontalAlignment(JLabel.TRAILING);
        lPlayerRole.setVerticalAlignment(JLabel.TOP);
        controlsPanel.add(lPlayerRole);

        layout.putConstraint(SpringLayout.EAST, lPlayerRole, 100, SpringLayout.WEST, controlsPanel);
        layout.putConstraint(SpringLayout.NORTH, lPlayerRole, 15, SpringLayout.SOUTH, lPlayerName);

        rbgPlayerRole = new ButtonGroup();

        rbPlayAsOrder = new JRadioButton("Play as Order");
        rbPlayAsOrder.setActionCommand(PlayerRole.ORDER.name());
        rbPlayAsOrder.setSelected(true);
        rbgPlayerRole.add(rbPlayAsOrder);
        controlsPanel.add(rbPlayAsOrder);

        layout.putConstraint(SpringLayout.WEST, rbPlayAsOrder, 5, SpringLayout.EAST, lPlayerRole);
        layout.putConstraint(SpringLayout.NORTH, rbPlayAsOrder, 0, SpringLayout.NORTH, lPlayerRole);

        JRadioButton rb = new JRadioButton("Play as Chaos");
        rb.setActionCommand(PlayerRole.CHAOS.name());
        rbgPlayerRole.add(rb);
        controlsPanel.add(rb);

        layout.putConstraint(SpringLayout.WEST, rb, 5, SpringLayout.EAST, lPlayerRole);
        layout.putConstraint(SpringLayout.NORTH, rb, 0, SpringLayout.SOUTH, rbPlayAsOrder);

        controlsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 30));
        add(controlsPanel, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2));

        JButton b = new JButton("Connect");
        b.setToolTipText("Connect with remote player for this game");
        b.setVerticalTextPosition(AbstractButton.CENTER);
        b.setHorizontalTextPosition(AbstractButton.CENTER);
        b.setActionCommand(Commands.INVITE.name());
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
    }

    private PlayerRole getPlayerRole() {
        return PlayerRole.valueOf(rbgPlayerRole.getSelection().getActionCommand());
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        Commands command = Commands.valueOf(evt.getActionCommand());
        switch (command) {
            case INVITE:
                String playerName = tfPlayerName.getText();
                if (playerName == null || playerName.trim().isEmpty()) {
                    warn("Player name is required", "Warning");
                    tfPlayerName.selectAll();
                    tfPlayerName.requestFocus();
                } else {
                    try {
                        PlayerRole playerRole = getPlayerRole();
                        app.networkGame(playerName, playerRole);
                        reset();
                    } catch (IOException e) {
                        error("Failed to connect with remote player", "Error");
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
        tfPlayerName.setText("");
        rbPlayAsOrder.setSelected(true);
    }

    @Override
    public void setFocus() {
        tfPlayerName.requestFocus();
    }

}
