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
import java.awt.event.KeyEvent;

/**
 * @author Anastasia Radchenko
 */
public class OnePlayerGameConfiguration extends AbstractPane implements ActionListener {

    private enum Commands {
        PLAY,
        CANCEL
    }

    private final JTextField tfPlayerName;
    private final ButtonGroup rbgPlayerRole;
    private final JRadioButton rbPlayAsOrder;

    public OnePlayerGameConfiguration(Application app) {
        super(app, new BorderLayout(5, 50));

        JLabel l = new JLabel("One Player Game Configuration");
        l.setHorizontalAlignment(JLabel.CENTER);
        add(l, BorderLayout.PAGE_START);

        SpringLayout layout = new SpringLayout();
        JPanel controlsPanel = new JPanel(layout);

        JLabel lPlayerName = new JLabel("Player name:");
        lPlayerName.setHorizontalAlignment(JLabel.TRAILING);
        controlsPanel.add(lPlayerName);

        tfPlayerName = new JTextField(20);
        tfPlayerName.setHorizontalAlignment(JTextField.LEADING);
        controlsPanel.add(tfPlayerName);

        layout.putConstraint(SpringLayout.EAST, lPlayerName, 100, SpringLayout.WEST, controlsPanel);
        layout.putConstraint(SpringLayout.NORTH, lPlayerName, 5, SpringLayout.NORTH, controlsPanel);

        layout.putConstraint(SpringLayout.WEST, tfPlayerName, 5, SpringLayout.EAST, lPlayerName);

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

        controlsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 50));
        add(controlsPanel, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2));

        JButton b = new JButton("Play Game");
        b.setVerticalTextPosition(AbstractButton.CENTER);
        b.setHorizontalTextPosition(AbstractButton.CENTER);
        b.setActionCommand(Commands.PLAY.name());
        b.setFocusPainted(false);
        b.addActionListener(this);
        buttonsPanel.add(b);

        b = new JButton("Cancel");
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
    public void actionPerformed(ActionEvent e) {
        Commands command = Commands.valueOf(e.getActionCommand());
        switch (command) {
            case PLAY:
                String playerName = tfPlayerName.getText();
                if (playerName == null || playerName.trim().isEmpty()) {
                    warn("Player name is required", "Warning");
                    tfPlayerName.selectAll();
                    tfPlayerName.requestFocus();
                } else {
                    app.onePlayerGame(playerName, PlayerRole.valueOf(rbgPlayerRole.getSelection().getActionCommand()));
                    reset();
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
