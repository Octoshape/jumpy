package jumpy;

import javax.swing.JFrame;

public class Main {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Jumpy");
        Controller layout = new Controller();
        layout.addComponentToPane(frame.getContentPane());
        frame.setResizable(false);
        frame.setSize((int)Utils.GAME_WINDOW_WIDTH, (int)Utils.GAME_WINDOW_HEIGHT);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
