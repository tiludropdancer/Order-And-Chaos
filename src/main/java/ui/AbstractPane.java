package ui;

import application.Application;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.Font;
import java.awt.LayoutManager;

/**
 * @author Anastasia Radchenko
 */
public abstract class AbstractPane  extends JPanel {
    protected static final Font FNT_SS_BOLD_12 = new Font("SansSerif", Font.BOLD, 12);
    protected static final Font FNT_SS_PLAIN_12 = new Font("SansSerif", Font.PLAIN, 12);

    protected final Application app;

    public AbstractPane(Application app, LayoutManager layout) {
        super(layout);
        this.app = app;
    }

    public AbstractPane(Application app) {
        this.app = app;
    }

    protected boolean confirm(String question, String title) {
        int n = JOptionPane.showConfirmDialog(app.getFrame(), question, title, JOptionPane.YES_NO_OPTION);
        return n == JOptionPane.YES_OPTION;
    }

    protected void info(String message, String title) {
        JOptionPane.showMessageDialog(app.getFrame(), message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    protected void warn(String message, String title) {
        JOptionPane.showMessageDialog(app.getFrame(), message, title, JOptionPane.WARNING_MESSAGE);
    }

    protected void error(String message, String title) {
        JOptionPane.showMessageDialog(app.getFrame(), message, title, JOptionPane.ERROR_MESSAGE);
    }

    public void reset() {
    }

    public void setFocus() {
    }

    protected void updateControls() {
    }

    /**
     * repaint the UI asynchronously
     */
    protected void refresh() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                updateControls();
            }
        });
    }

    protected void cancel() {
        reset();
        app.menu();
    }
}
