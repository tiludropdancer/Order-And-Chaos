package ui;

import application.Application;
import models.IllegalMoveException;
import models.Move;

import java.io.IOException;
import java.util.EnumSet;

/**
 * @author Anastasia Radchenko
 */
public class NetworkGame extends AbstractGame  {
    private volatile Thread workerThread;

    public NetworkGame(final Application app) {
        super(app);
    }

    @Override
    protected EnumSet<Command> getButtonCommands() {
        return EnumSet.of(Command.QUIT_GAME);
    }

    @Override
    protected void doMove(Move move) throws IllegalMoveException, IOException {
        super.doMove(move);
        try {
            app.sendMove(move);
        } catch (IOException e) {
            e.printStackTrace();
            lostConnection();
        }
        app.makeRemoteMove();
    }

    public void setWorkerThread(Thread workerThread) {
        this.workerThread = workerThread;
    }

    @Override
    protected void cancel() {
        if (workerThread != null) {
            workerThread.interrupt();
            workerThread = null;
        }
        super.cancel();
    }

    public void lostConnection() {
    	error("Connection to remote player has been lost.\nThe game will stop now.", "Communication Error");
    	cancel();
    }
}
