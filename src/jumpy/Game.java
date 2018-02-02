package jumpy;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
    
public class Game extends JPanel {

	public Level currentLevel;
	private Player player;
	private double currentCameraOffsetX = 0;
	private double currentCameraOffsetY = 0;
	private int framecounter = 0;
	private int currentFPS = 0;
	private long lastFPSUpdate = System.currentTimeMillis();

	public Game() {
		Utils.GAME_INSTANCE = this;
		addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				player.keyReleased(e);
			}

			@Override
			public void keyPressed(KeyEvent e) {
				player.keyPressed(e);
			}
		});
	}

	public void loadLevel (Level lvl) {
		currentLevel = lvl;
		player = new Player();
		currentCameraOffsetX = 0;
		currentCameraOffsetY = 0;
	}

	public void update() {
	    framecounter++;
	    if (System.currentTimeMillis() - lastFPSUpdate > 1000) {
	        currentFPS = framecounter;
	        framecounter = 0;
	        lastFPSUpdate = System.currentTimeMillis();
	    }
		player.update();
		checkGoalsCollision();
		double playerOffsetX = (player.getBounds().getCenterX() - currentCameraOffsetX -  Utils.GAME_WINDOW_WIDTH / 2) / Utils.CAMERA_DELAY_X;
		double playerOffsetY = (player.getBounds().getCenterY() - currentCameraOffsetY - Utils.GAME_WINDOW_HEIGHT / 2) / Utils.CAMERA_DELAY_Y;
		currentCameraOffsetX += playerOffsetX;
		currentCameraOffsetY += playerOffsetY;
	}

	@Override
	public void paint(Graphics g) {

		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.translate(-currentCameraOffsetX, -currentCameraOffsetY);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.drawString("FPS: " + currentFPS, (int)currentCameraOffsetX + 10, (int)currentCameraOffsetY + 30);

		// Paint game objects
		player.paint(g2d);
		Utils.PAINTING_LEVEL = true;
		currentLevel.paint(g2d);
		Utils.PAINTING_LEVEL = false;
	}

	public void checkGoalsCollision() {
		for (Platform p : currentLevel.visiblePlatforms) {
			if (p instanceof Goal) {
				if (p.body.intersects(player.getBounds())) {
					gameWon();
				}
			}
		}
	}

	public List<Integer> platformCollision() {
		ArrayList<Integer> platformIndices = new ArrayList<Integer>();

		for (Platform r : currentLevel.visiblePlatforms) {
			if (r.body.intersects(player.getBounds())) {
				platformIndices.add(currentLevel.visiblePlatforms.indexOf(r));
			}
		}

		return platformIndices;
	}

	public void gameWon() {
		gameOver();
	}

	public void gameOver() {
		player = new Player();
		currentCameraOffsetX = 0;
		currentCameraOffsetY = 0;
	}
}