package jumpy;

import java.awt.CardLayout;
import java.awt.Container;

import javax.swing.JPanel;

public class Controller {
    private Game game;
    private LevelEditor editor;
    private JPanel menuPanel;
    
	public void addComponentToPane(Container pane) {
	    Utils.loadImages();
		menuPanel = new JPanel(new CardLayout());
		game = new Game();
		game.loadLevel(new Level());
		editor = new LevelEditor(this);
		Menu menu =  new Menu(this);
		
		// Add all the different screens to the layout. First layout to be added is shown at the start, hence it's menu.
		menuPanel.add(menu, "Menu");
		menuPanel.add(game, "Game");
		menuPanel.add(editor, "Editor");
		pane.add(menuPanel);
	}
	
	public void runGame (Level lvl) {
	    game.loadLevel(lvl);
	    runGame();
	}
	
	public void runGame () {
	    ((CardLayout)menuPanel.getLayout()).show(menuPanel, "Game");
	    Utils.GAME_RUNNING = true;
	    Thread gameThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    while (Utils.GAME_RUNNING) {
                        long startTime =  System.nanoTime();
                        
                        if (!Utils.PAINTING_LEVEL) {
                        	game.update();
                        }
                        game.repaint();
                        Thread.sleep(Math.max(0, (Utils.OPTIMAL_TIME - (System.nanoTime() - startTime)) / 1000000));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.out.println("Game loop exited with : " + e.getCause().getMessage());
                }
            }
        });
        gameThread.start();
        game.requestFocus();
	}
	
	public void runEditor () {
        ((CardLayout)menuPanel.getLayout()).show(menuPanel, "Editor");
        Utils.EDITOR_RUNNING = true;
        Thread editorThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    while (Utils.EDITOR_RUNNING) {
                        editor.repaint();
                        Thread.sleep(10);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.out.println("Editor loop exited with : " + e.getCause().getMessage());
                }
            }
        });
        editorThread.start();
        editor.requestFocus();
	}
}
