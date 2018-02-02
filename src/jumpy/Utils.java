package jumpy;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;


public class Utils {
	public static final double GAME_WINDOW_HEIGHT = 800;
	public static final double GAME_WINDOW_WIDTH = 1200;
	public static final double PLAYER_SPEED = 9;
	public static final double PLAYER_SIZE = 30;
	public static final double GRAVITY = 0.4;
	public static final double cameraDelay = 3;
	public static final double CAMERA_DELAY_X = 60;
	public static final double CAMERA_DELAY_Y = 120;
	public static boolean GAME_RUNNING = false;
	public static boolean EDITOR_RUNNING = false;
	public static final double PLAYER_JUMP_STRENGTH = 10;
	public static Game GAME_INSTANCE;
	public static boolean PAINTING_LEVEL = false;
	public static final int TARGET_FPS = 60;
	public static final long OPTIMAL_TIME = 1000000000 / TARGET_FPS; // Time a frame should take to reach target FPS (in ns).
	public static BufferedImage PLATFORM_IMAGE = null;
	public static BufferedImage DIRT_IMAGE = null;
	public static BufferedImage RED_OBSTACLE_IMAGE = null;
	public static final String LEVEL_DIRECTORY = "levels/";

	public static void loadImages() {
		try {
			PLATFORM_IMAGE = ImageIO.read(new File("res/grass_sprite.jpg"));
			DIRT_IMAGE = ImageIO.read(new File("res/dirt_sprite.jpg"));
			RED_OBSTACLE_IMAGE = ImageIO.read(new File("res/lava.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static BufferedImage flipImage (BufferedImage image, boolean horizontal) {
		AffineTransform at = new AffineTransform();

		if (horizontal) {
			at.concatenate(AffineTransform.getScaleInstance(-1, 1));
			at.concatenate(AffineTransform.getTranslateInstance(-image.getWidth(), 0));
		} else {
			at.concatenate(AffineTransform.getScaleInstance(1, -1));
			at.concatenate(AffineTransform.getTranslateInstance(0, -image.getHeight()));
		}

		BufferedImage newImage = new BufferedImage(
				image.getWidth(), image.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = newImage.createGraphics();
		g.transform(at);
		g.drawImage(image, 0, 0, null);
		g.dispose();
		return newImage;
	}
}   
