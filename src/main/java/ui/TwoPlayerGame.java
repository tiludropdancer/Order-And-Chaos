package ui;

import application.Application;

import java.util.EnumSet;

/**
 * @author Anastasia Radchenko
 */
public class TwoPlayerGame extends AbstractGame  {

    public TwoPlayerGame(Application app) {
        super(app);
    }

    @Override
    protected EnumSet<Command> getButtonCommands() {
        return EnumSet.of(Command.SAVE_GAME, Command.EXIT_GAME, Command.QUIT_GAME);
    }

}
