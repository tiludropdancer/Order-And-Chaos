package ui;

import application.Application;
import models.Achievement;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * @author Anastasia Radchenko
 */
public class Achievements extends AbstractPane implements ActionListener {

    public Achievements(final Application app) {
        super(app);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(30, 70, 30, 70));

        JLabel l = new JLabel("Achievements");
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(l);
        add(Box.createRigidArea(new Dimension(0, 10)));

        String[] columnNames = {"Player Name", "Score"};

        List<Achievement> achievements = app.getAchievements();
        int size = achievements.size();
        Object[][] data = new Object[size][2];

        for (int i = 0; i < size; i++) {
            Achievement achievement = achievements.get(i);
            data[i][0] = achievement.getPlayer();
            data[i][1] = achievement.getScore();
        }

        final JTable table = new JTable(data, columnNames);
        table.setPreferredScrollableViewportSize(new Dimension(80, 40));
        table.setFillsViewportHeight(true);

        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);

        //Add the scroll pane to this panel.
        add(scrollPane);
        add(Box.createRigidArea(new Dimension(0, 10)));

        JButton b = new JButton("Ok");
        b.setActionCommand("OK");
        b.addActionListener(this);
        b.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(b);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("OK".equals(e.getActionCommand())) {
            app.menu();
        }
    }

}
