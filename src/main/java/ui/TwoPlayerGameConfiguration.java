package ui;

import application.Application;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Anastasia Radchenko
 */
public class TwoPlayerGameConfiguration extends AbstractPane implements ActionListener, PropertyChangeListener {

    private enum Commands {
        PLAY,
        CANCEL
    }

    private final JTextField tfOrderPlayerName;
    private final JTextField tfChaosPlayerName;

    public TwoPlayerGameConfiguration(Application app) {
        super(app, new BorderLayout(5, 75));

        JLabel l = new JLabel("Two Player Game Configuration");
        l.setHorizontalAlignment(JLabel.CENTER);
        add(l, BorderLayout.PAGE_START);

        JPanel controlsPanel = new JPanel(new GridLayout(2, 2));

        l = new JLabel("Order player name:");
        l.setHorizontalAlignment(JLabel.TRAILING);
        controlsPanel.add(l);

        tfOrderPlayerName = new JTextField(30);
        tfOrderPlayerName.setHorizontalAlignment(JTextField.LEADING);
        controlsPanel.add(tfOrderPlayerName);

        l = new JLabel("Chaos player name:");
        l.setHorizontalAlignment(JLabel.TRAILING);
        controlsPanel.add(l);

        tfChaosPlayerName = new JTextField(30);
        tfChaosPlayerName.setHorizontalAlignment(JTextField.LEADING);
        controlsPanel.add(tfChaosPlayerName);

        controlsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 50));

        add(controlsPanel, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2));

        JButton b = new JButton("Play Game");
        b.setVerticalTextPosition(AbstractButton.CENTER);
        b.setHorizontalTextPosition(AbstractButton.CENTER);
        b.setMnemonic(KeyEvent.VK_P);
        b.setActionCommand(Commands.PLAY.name());
        b.setFocusPainted(false);
        b.addActionListener(this);
        buttonsPanel.add(b);

        b = new JButton("Cancel");
        b.setVerticalTextPosition(AbstractButton.CENTER);
        b.setHorizontalTextPosition(AbstractButton.CENTER);
        b.setMnemonic(KeyEvent.VK_C);
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
                String orderPlayerName = tfOrderPlayerName.getText();
                String chaosPlayerName = tfChaosPlayerName.getText();
                if (orderPlayerName == null || orderPlayerName.trim().isEmpty()) {
                    warn("Order player name is required", "Warning");
                    tfOrderPlayerName.selectAll();
                    tfOrderPlayerName.requestFocus();
                } else if (chaosPlayerName == null || chaosPlayerName.trim().isEmpty()) {
                    warn("Chaos player name is required", "Warning");
                    tfChaosPlayerName.selectAll();
                    tfChaosPlayerName.requestFocus();
                } else if (orderPlayerName.equals(chaosPlayerName)) {
                    warn("The player names must be different", "Warning");
                    tfChaosPlayerName.selectAll();
                    tfChaosPlayerName.requestFocus();
                } else {
                    app.twoPlayerGame(orderPlayerName, chaosPlayerName);
                    reset();
                }
                break;
            case CANCEL:
                cancel();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }

    @Override
    public void reset() {
        tfOrderPlayerName.setText("");
        tfChaosPlayerName.setText("");
    }

    @Override
    public void setFocus() {
        tfOrderPlayerName.requestFocus();
    }

}
