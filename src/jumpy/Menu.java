package jumpy;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class Menu extends JPanel {
    public Menu(final Controller c) {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JButton startGameButton = new JButton("Start Game");
        startGameButton.setFocusable(false);
        startGameButton.setHorizontalTextPosition(SwingConstants.CENTER);
        startGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startGameButton.setMaximumSize(new Dimension(300, 70));
        startGameButton.setPreferredSize(new Dimension(300, 70));
        startGameButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                c.runGame();
            }
        });

        JButton exitGameButton = new JButton("Exit Game");
        exitGameButton.setFocusable(false);
        exitGameButton.setMaximumSize(new Dimension(300, 70));
        exitGameButton.setPreferredSize(new Dimension(300, 70));
        exitGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitGameButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        JButton levelEditorButton = new JButton("Level Editor");
        levelEditorButton.setFocusable(false);
        levelEditorButton.setMaximumSize(new Dimension(300, 70));
        levelEditorButton.setPreferredSize(new Dimension(300, 70));
        levelEditorButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        levelEditorButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                c.runEditor();
            }
        });

        JLabel welcomeLabel = new JLabel("Welcome to Jumpy!");
        welcomeLabel.setAlignmentX(CENTER_ALIGNMENT);

        this.add(Box.createVerticalGlue());
        this.add(welcomeLabel);
        this.add(Box.createVerticalGlue());
        this.add(startGameButton);
        this.add(Box.createVerticalGlue());
        this.add(levelEditorButton);
        this.add(Box.createVerticalGlue());
        this.add(exitGameButton);
        this.add(Box.createVerticalGlue());
    }
}
