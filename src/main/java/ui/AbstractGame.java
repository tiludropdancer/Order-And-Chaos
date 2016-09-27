package ui;

import application.Application;
import models.IllegalMoveException;
import models.Mark;
import models.Move;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.EnumSet;

/**
 * @author Anastasia Radchenko
 */
public abstract class AbstractGame  extends AbstractPane implements ActionListener {
    protected enum Command {
        UNDO_MOVE("Undo", "Undo last move"),
        SAVE_GAME("Save", "Save the game and continue playing it"),
        EXIT_GAME("Exit", "Save the game and exit it"),
        QUIT_GAME("Quit", "Quit the game without saving it");

        final String displayText;
        final String toolTipText;

        Command(String display, String toolTip) {
            displayText = display;
            toolTipText = toolTip;
        }
    }

    private JLabel lOrderPlayer;
    private JLabel lChaosPlayer;
    private JLabel lInfo;

    private JTextField[][] tfSquares;
    private long time;

    private volatile boolean updating;

    protected AbstractGame(Application app) {
        super(app, new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JPanel titlePanel = new JPanel(new GridLayout(1, 3));

        lOrderPlayer = new JLabel();
        lOrderPlayer.setText("Order");
        lOrderPlayer.setHorizontalAlignment(JLabel.LEADING);
        titlePanel.add(lOrderPlayer);

        JLabel l = new JLabel();
        l.setText("vs.");
        l.setHorizontalAlignment(JLabel.CENTER);
        titlePanel.add(l);

        lChaosPlayer = new JLabel();
        lChaosPlayer.setText("Chaos");
        lChaosPlayer.setHorizontalAlignment(JLabel.TRAILING);
        titlePanel.add(lChaosPlayer);

        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titlePanel, BorderLayout.PAGE_START);

        JPanel squaresPanel = new JPanel(new GridLayout(6, 6));
        squaresPanel.setBorder(BorderFactory.createEmptyBorder(0, 65, 0, 65));

        Font font = new Font("SansSerif", Font.BOLD, 22);
        tfSquares = new JTextField[6][6];
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 6; col++) {
                final JTextField tf = new JTextField(1); //limit to one char only
                tfSquares[row][col] = tf;
                tf.setFont(font);
                tf.setHorizontalAlignment(JTextField.CENTER);
                final int finalRow = row;
                final int finalCol = col;
                Document doc = new PlainDocument() {
                    @Override
                    public void insertString(int offs, String str, AttributeSet attr) throws BadLocationException {
                        String newStr = str.replaceAll(" ", "");
                        super.insertString(offs, newStr, attr);
                    }

                    @Override
                    public void replace(int offs, int len, String str, AttributeSet attr) throws BadLocationException {
                        String newStr = str.replaceAll("[^xXoO]", "").toUpperCase(); //regular expression - all but xXoO
                        if (!newStr.isEmpty() && !updating && localPlayerMovesNext()) {
                            makeMove(finalRow, finalCol, newStr);
                        }
                        super.replace(offs, len, newStr, attr);
                    }
                };
                tf.setDocument(doc);
                tf.setFocusable(false);
                tf.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        String text = tf.getText();
                        tf.setFocusable(localPlayerMovesNext() && text.equals(""));
                    }
                });
                tf.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        tf.setFocusable(false);
                    }
                });
                squaresPanel.add(tf);
            }
        }

        add(squaresPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new GridLayout(2, 1));

        EnumSet<Command> commands = getButtonCommands();

        JPanel buttonsPanel = new JPanel(new GridLayout(1, commands.size()));

        for (Command command : commands) {
            JButton b = new JButton(command.displayText);
            b.setToolTipText(command.toolTipText);
            b.setActionCommand(command.name());
            b.setFocusPainted(false);
            b.addActionListener(this);
            buttonsPanel.add(b);
        }

        bottomPanel.add(buttonsPanel);

        lInfo = new JLabel("");
        lInfo.setHorizontalAlignment(JLabel.LEADING);
        bottomPanel.add(lInfo);

        add(bottomPanel, BorderLayout.PAGE_END);

        time = System.currentTimeMillis();
    }

    protected abstract EnumSet<Command> getButtonCommands();

    private boolean localPlayerMovesNext() {
        return app.getGameState().localPlayerMovesNext();
    }

    private void makeMove(int row, int col, String value) {
        Move move = new Move(app.getGameState().nextTurn(), col + 1, 6 - row, Mark.valueOf(value));
        try {
            doMove(move);
            if (app.getGameState().isOver()) {
                recordAchievement();
                info(app.getGameState().getWinner()+" wins!", "Congratulations");
                cancel();
            } else {
                refresh();
            }
        } catch (IllegalMoveException e) {
            error("The move "+move+" is illegal", "Illegal Move");
        } catch (IOException ioe) {
            error("Failed to make remote move", "Communication Error");
            cancel();
        } finally {
            resetInfo();
        }
    }

    protected void doMove(Move move) throws IllegalMoveException, IOException {
        app.getGameState().makeMove(move);
    }

    private void highlightNextTurn() {
        if (app.getGameState().isOrderNextTurn()) {
            lOrderPlayer.setFont(FNT_SS_BOLD_12);
            lChaosPlayer.setFont(FNT_SS_PLAIN_12);
        } else {
            lOrderPlayer.setFont(FNT_SS_PLAIN_12);
            lChaosPlayer.setFont(FNT_SS_BOLD_12);
        }
    }

    public void updateControls() {
        updating = true;
        highlightNextTurn();
        lOrderPlayer.setText(app.getGameState().getOrderPlayer() + " (Order)");
        lChaosPlayer.setText(app.getGameState().getChaosPlayer() + " (Chaos)");
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 6; col++) {
                tfSquares[row][col].setText(app.getGameState().getBoardMark(col + 1, 6 - row).getDisplayValue());
            }
        }
        time = System.currentTimeMillis();
        updating = false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Command command = Command.valueOf(e.getActionCommand());
        switch (command) {
            case UNDO_MOVE:
                undoLastMove();
                break;
            case SAVE_GAME:
                saveGame();
                break;
            case EXIT_GAME:
                if (confirm("Do you really want to exit this game?", "Confirm Your Action"))  {
                    saveGame();
                    cancel();
                }
                break;
            case QUIT_GAME:
                if (confirm("Do you really want to quit this game?", "Confirm Your Action"))  {
                    cancel();
                }
        }
    }

    private void undoLastMove() {
        try {
            app.undoLastMove();
            lInfo.setText("The last move is undone");
            refresh();
        } catch (IllegalMoveException e) {
            info("No moves made yet", "Information");
        }
    }

    private void saveGame() {
        try {
            app.saveGame();
            lInfo.setText("The game is saved");
        } catch (IOException e) {
            error("Failed to save the game: " + e.getMessage(), "Error");
        }
    }

    private void recordAchievement() {
        try {
            app.recordAchievement();
            lInfo.setText("Achievement recorded");
        } catch (IOException e) {
            error("Failed to record achievement: "+e.getMessage(), "Error");
        }
    }

    private void resetInfo() {
        lInfo.setText("");
    }

    @Override
    public void reset() {
        resetInfo();
        lOrderPlayer.setText("Order");
        lChaosPlayer.setText("Chaos");
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 6; col++) {
                tfSquares[row][col].setText("");
            }
        }
    }

}
