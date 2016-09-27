package ui;

import application.Application;

import java.util.EnumSet;

/**
 * @author Anastasia Radchenko
 */
public class OnePlayerGame extends AbstractGame  {

    public OnePlayerGame(Application app) {
        super(app);
    }

    @Override
    protected EnumSet<Command> getButtonCommands() {
        return EnumSet.of(Command.UNDO_MOVE, Command.SAVE_GAME, Command.EXIT_GAME, Command.QUIT_GAME);
    }

}
