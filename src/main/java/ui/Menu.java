package ui;

import application.Application;
import models.GameState;
import models.OnePlayerGameState;
import models.TwoPlayerGameState;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;

/**
 * @author Anastasia Radchenko
 */
public class Menu extends AbstractPane implements ActionListener {
     private enum Commands {
        START_1_PLAYER_GAME,
        START_2_PLAYER_GAME,
        START_NETWORK_GAME,
        JOIN_NETWORK_GAME,
        RESTORE_GAME,
        VIEW_ACHIEVEMENTS,
        QUIT
    }

    public Menu(Application app) {
        super(app, new BorderLayout());

        JPanel menuPanel = new JPanel(new GridLayout(0, 1));

        JButton b = new JButton("Start One-Player Game");
        b.setVerticalTextPosition(AbstractButton.CENTER);
        b.setHorizontalTextPosition(AbstractButton.CENTER);
        b.setActionCommand(Commands.START_1_PLAYER_GAME.name());
        b.setFocusPainted(false);
        b.addActionListener(this);
        menuPanel.add(b);

        b = new JButton("Start Two-Player Game");
        b.setVerticalTextPosition(AbstractButton.CENTER);
        b.setHorizontalTextPosition(AbstractButton.CENTER);
        b.setActionCommand(Commands.START_2_PLAYER_GAME.name());
        b.setFocusPainted(false);
        b.addActionListener(this);
        menuPanel.add(b);

        b = new JButton("Start Network Game");
        b.setVerticalTextPosition(AbstractButton.CENTER);
        b.setHorizontalTextPosition(AbstractButton.CENTER);
        b.setActionCommand(Commands.START_NETWORK_GAME.name());
        b.setFocusPainted(false);
        b.addActionListener(this);
        menuPanel.add(b);

        b = new JButton("Join Network Game");
        b.setVerticalTextPosition(AbstractButton.CENTER);
        b.setHorizontalTextPosition(AbstractButton.CENTER);
        b.setActionCommand(Commands.JOIN_NETWORK_GAME.name());
        b.setFocusPainted(false);
        b.addActionListener(this);
        menuPanel.add(b);

        b = new JButton("Restore Game");
        b.setVerticalTextPosition(AbstractButton.CENTER);
        b.setHorizontalTextPosition(AbstractButton.CENTER);
        b.setActionCommand(Commands.RESTORE_GAME.name());
        b.setFocusPainted(false);
        b.addActionListener(this);
        menuPanel.add(b);

        b = new JButton("View Achievements");
        b.setVerticalTextPosition(AbstractButton.CENTER);
        b.setHorizontalTextPosition(AbstractButton.CENTER);
        b.setActionCommand(Commands.VIEW_ACHIEVEMENTS.name());
        b.setFocusPainted(false);
        b.addActionListener(this);
        menuPanel.add(b);

        b = new JButton("Quit");
        b.setVerticalTextPosition(AbstractButton.CENTER);
        b.setHorizontalTextPosition(AbstractButton.CENTER);
        b.setActionCommand(Commands.QUIT.name());
        b.setFocusPainted(false);
        b.addActionListener(this);
        menuPanel.add(b);

        add(menuPanel, BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        Commands command = Commands.valueOf(evt.getActionCommand());
        switch (command) {
            case START_1_PLAYER_GAME:
                app.onePlayerGameConfiguration();
                break;
            case START_2_PLAYER_GAME:
                app.twoPlayerGameConfiguration();
                break;
            case START_NETWORK_GAME:
                app.networkGameConfiguration();
                break;
            case JOIN_NETWORK_GAME:
                app.remoteGameConfiguration();
                break;
            case RESTORE_GAME:
                try {
                    GameState gameState = app.restoreGame();
                    if (gameState == null) {
                        info("Saved game not found", "Information");
                    } else if (gameState instanceof TwoPlayerGameState) {
                        app.twoPlayerGame((TwoPlayerGameState) gameState);
                    } else if (gameState instanceof OnePlayerGameState) {
                        app.onePlayerGame((OnePlayerGameState) gameState);
                    } else {
                        error("Saved game is corrupt or invalid", "Error");
                    }
                } catch (IOException e) {
                    error("Failed to restore the game: "+e.getMessage(), "Error");
                }
                break;
            case VIEW_ACHIEVEMENTS:
                app.viewAchievements();
                break;
            case QUIT:
                if (confirm("Do you really want to quit?", "Confirm Your Action"))  {
                    app.stop();
                }
        }
    }

}
