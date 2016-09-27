package application;

import io.ClientGameConnector;
import io.ServerGameConnector;
import io.GameConnector;
import io.FileGameReaderWriter;
import io.GameReaderWriter;
import models.Achievement;
import models.GameState;
import models.IllegalMoveException;
import models.Mark;
import models.Move;
import models.NetworkGameState;
import models.OnePlayerGameState;
import models.PlayerRole;
import models.TwoPlayerGameState;
import ui.Achievements;
import ui.Menu;
import ui.NetworkGame;
import ui.NetworkGameConfiguration;
import ui.OnePlayerGame;
import ui.OnePlayerGameConfiguration;
import ui.RemoteGameConfiguration;
import ui.TwoPlayerGame;
import ui.TwoPlayerGameConfiguration;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import java.awt.Dimension;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * To build jar:
 * ./gradlew build
 * 
 * To view contents of jar:
 * jar -tvf build/libs/order-and-chaos-ui-1.0.jar
 * 
 * To run jar:
 * java -cp build/libs/order-and-chaos-ui-1.0.jar application.Application
 * 
 * @author Anastasia Radchenko
 */
public class Application {

    private static final GameReaderWriter gameReaderWriter = new FileGameReaderWriter();
    private static final AchievementManager achievementManager = AchievementManager.getInstance();

    private final JFrame frame;
    private final OnePlayerGameConfiguration onePlayerGameConfigurationPane;
    private final OnePlayerGame onePlayerGamePane;
    private final TwoPlayerGameConfiguration twoPlayerGameConfigurationPane;
    private final TwoPlayerGame twoPlayerGamePane;
    private final NetworkGameConfiguration networkGameConfigurationPane;
    private final NetworkGame networkGamePane;
    private final RemoteGameConfiguration remoteGameConfigurationPane;
    private final Achievements achievementsPane;
    private final Menu menuPane;

    private GameState gameState;
    private GameConnector gameConnector;

    public Application() {
        //Create and set up the window.
        frame = new JFrame("Order and Chaos");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setPreferredSize(new Dimension(500, 500));
        frame.setResizable(false);

        //Create and set up the content panes.
        //Content panes must be opaque.
        onePlayerGameConfigurationPane = new OnePlayerGameConfiguration(this);
        onePlayerGameConfigurationPane.setOpaque(true);

        onePlayerGamePane = new OnePlayerGame(this);
        onePlayerGamePane.setOpaque(true);

        twoPlayerGameConfigurationPane = new TwoPlayerGameConfiguration(this);
        twoPlayerGameConfigurationPane.setOpaque(true);

        twoPlayerGamePane = new TwoPlayerGame(this);
        twoPlayerGamePane.setOpaque(true);

        networkGameConfigurationPane = new NetworkGameConfiguration(this);
        networkGameConfigurationPane.setOpaque(true);

        networkGamePane = new NetworkGame(this);
        networkGamePane.setOpaque(true);

        remoteGameConfigurationPane = new RemoteGameConfiguration(this);
        remoteGameConfigurationPane.setOpaque(true);

        achievementsPane = new Achievements(this);
        achievementsPane.setOpaque(true);

        menuPane = new Menu(this);
        menuPane.setOpaque(true);
        frame.setContentPane(menuPane);
    }

    public JFrame getFrame() {
        return frame;
    }

    public GameState getGameState() {
        return gameState;
    }

    public List<Achievement> getAchievements() {
        return achievementManager.getAchievements();
    }

    public void recordAchievement() throws IOException {
        achievementManager.recordAchievement(gameState.getWinner());
    }

    public void undoLastMove() throws IllegalMoveException {
        gameState.undoLastMove();
    }

    public void saveGame() throws IOException {
        gameReaderWriter.writeState(gameState);
    }

    public GameState restoreGame() throws IOException {
        return gameReaderWriter.readState();
    }

    public void menu() {
        if (gameConnector != null) {
            try {
                gameConnector.disconnect();
            } catch (Exception e) {
                // ignore
            } finally {
                gameConnector = null;
            }
        }
        frame.setContentPane(menuPane);
        frame.revalidate();
        frame.repaint();
    }

    public void viewAchievements() {
        frame.setContentPane(achievementsPane);
        frame.revalidate();
        frame.repaint();
    }

    public void onePlayerGameConfiguration() {
        frame.setContentPane(onePlayerGameConfigurationPane);
        onePlayerGameConfigurationPane.reset();
        onePlayerGameConfigurationPane.setFocus();
        frame.revalidate();
        frame.repaint();
    }

    public void onePlayerGame(String playerName, PlayerRole playerRole) {
        if (playerRole == PlayerRole.ORDER) {
            gameState = OnePlayerGameState.playAsOrder(playerName);
        } else if (playerRole == PlayerRole.CHAOS) {
            gameState = OnePlayerGameState.playAsChaos(playerName);
        } else {
            throw new IllegalArgumentException("Invalid player role: "+playerRole);
        }
        onePlayerGamePane.updateControls();
        frame.setContentPane(onePlayerGamePane);
        frame.revalidate();
        frame.repaint();
    }

    public void onePlayerGame(OnePlayerGameState gameState) {
        this.gameState = gameState;
        onePlayerGamePane.updateControls();
        frame.setContentPane(onePlayerGamePane);
        frame.revalidate();
        frame.repaint();
    }

    public void twoPlayerGameConfiguration() {
        frame.setContentPane(twoPlayerGameConfigurationPane);
        twoPlayerGameConfigurationPane.reset();
        twoPlayerGameConfigurationPane.setFocus();
        frame.revalidate();
        frame.repaint();
    }

    public void twoPlayerGame(String orderPlayerName, String chaosPlayerName) {
        gameState = new TwoPlayerGameState(orderPlayerName, chaosPlayerName);
        twoPlayerGamePane.updateControls();
        frame.setContentPane(twoPlayerGamePane);
        frame.revalidate();
        frame.repaint();
    }

    public void twoPlayerGame(TwoPlayerGameState gameState) {
        this.gameState = gameState;
        twoPlayerGamePane.updateControls();
        frame.setContentPane(twoPlayerGamePane);
        frame.revalidate();
        frame.repaint();
    }

    public void networkGameConfiguration() {
        frame.setContentPane(networkGameConfigurationPane);
        networkGameConfigurationPane.reset();
        networkGameConfigurationPane.setFocus();
        frame.revalidate();
        frame.repaint();
    }

    public void networkGame(String hostPlayer, PlayerRole hostPlayerRole) throws IOException {
        String remotePlayer = null;
        try {
            gameConnector = new ServerGameConnector();
            gameConnector.connect("");
            gameConnector.writeString(hostPlayer);
            gameConnector.writeString(hostPlayerRole.name());
            remotePlayer = gameConnector.readString();
            if (hostPlayerRole == PlayerRole.ORDER) {
                gameState = new NetworkGameState(hostPlayer, remotePlayer, true);
            } else {
                gameState = new NetworkGameState(remotePlayer, hostPlayer, false);
            }
            networkGamePane.updateControls();
            frame.setContentPane(networkGamePane);
            frame.revalidate();
            frame.repaint();
            makeRemoteMove();
        } catch (NoSuchElementException e) {
            throw new IOException("Remote player has not entered their name.");
        }
        if (remotePlayer == null || remotePlayer.isEmpty()) {
            throw new IOException("Remote player has not entered their name.");
        }
    }

    public void remoteGameConfiguration() {
        frame.setContentPane(remoteGameConfigurationPane);
        remoteGameConfigurationPane.reset();
        remoteGameConfigurationPane.setFocus();
        frame.revalidate();
        frame.repaint();
    }

    public void remoteGame(String gameHostname, String playerName) throws IOException {
        String remotePlayer = null;
        try {
            gameConnector = new ClientGameConnector();
            gameConnector.connect(gameHostname);
            remotePlayer = gameConnector.readString();
            PlayerRole remotePlayerRole = PlayerRole.valueOf(gameConnector.readString());
            if (remotePlayer.equals(playerName)) {
                throw new IOException("The player names must be different.");
            }
            gameConnector.writeString(playerName);
            if (remotePlayerRole == PlayerRole.ORDER) {
                gameState = new NetworkGameState(remotePlayer, playerName, false);
            } else {
                gameState = new NetworkGameState(playerName, remotePlayer, true);
            }
            networkGamePane.updateControls();
            frame.setContentPane(networkGamePane);
            frame.revalidate();
            frame.repaint();
            makeRemoteMove();
        } catch (NoSuchElementException e) {
            throw new IOException("Remote player has not entered their name.");
        }
        if (remotePlayer == null || remotePlayer.isEmpty()) {
            throw new IOException("Remote player has not entered their name.");
        }
    }

    public void sendMove(Move move) throws IOException {
        gameConnector.writeInt(move.getX());
        gameConnector.writeInt(move.getY());
        gameConnector.writeString(move.getMark().getDisplayValue());
    }

    public Move receiveMove() throws IOException {
        int x = gameConnector.readInt();
        int y = gameConnector.readInt();
        String markString = gameConnector.readString();
        Mark mark = Mark.valueOf(markString.toUpperCase());
        return new Move(gameState.nextTurn(), x, y, mark);
    }

    public void makeRemoteMove() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            private volatile boolean broken = false;

            @Override
            protected void done() {
                networkGamePane.setWorkerThread(null);
                if (broken) {
                    networkGamePane.lostConnection();
                } else {
                    networkGamePane.updateControls(); //update view (uses updating boolean)
                }
            }

            @Override
            protected Void doInBackground() throws Exception {
                if (!gameState.localPlayerMovesNext()) {
                    networkGamePane.setWorkerThread(Thread.currentThread());
                    Move move = null;
                    try {
                        move = receiveMove();
                        gameState.makeMove(move); //update model
                    } catch (IOException e) {
                        broken = true;
                    	e.printStackTrace();
                    }

                }
                return null;
            }
        };
        worker.execute();
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private void createAndShowGUI() {
        frame.pack();
        frame.setVisible(true);
    }

    public void start() {
        //Execute in dispatcher thread
        //in background
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    public void stop() {
        frame.dispose();
    }

    public static void main(String[] args) {
        Application app = new Application();
        app.start();
    }
}
