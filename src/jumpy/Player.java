package jumpy;

import java.awt.CardLayout;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Player {
	public enum State { RED, BLUE, GREEN, BLACK }

	State playerState;
	boolean jumpPressed = false;
	boolean leftPressed = false;
	boolean rightPressed = false;
	boolean jumpPossible = false;
	boolean greenPressed = false;
	boolean bluePressed = false;
	boolean redPressed = false;
	public double x, y, xVelocity, yVelocity, old_x, old_y;
	private Animation playerAnimation;

	private Animation walkRight;
	private Animation walkLeft;
	private Animation standing;

	public Player () {
		BufferedImage[] walkingRight = {Sprite.getSprite(0, 0),
				Sprite.getSprite(1, 0),
				Sprite.getSprite(2, 0),
				Sprite.getSprite(3, 0),
				Sprite.getSprite(4, 0),
				Sprite.getSprite(5, 0),
				Sprite.getSprite(6, 0),
				Sprite.getSprite(7, 0),
				Sprite.getSprite(8, 0),
				Sprite.getSprite(9, 0),
				Sprite.getSprite(10, 0),
				Sprite.getSprite(11, 0),
				Sprite.getSprite(0, 1),};
		
		BufferedImage[] walkingLeft = {Utils.flipImage(Sprite.getSprite(0, 0), true),
				Utils.flipImage(Sprite.getSprite(1, 0), true),
				Utils.flipImage(Sprite.getSprite(2, 0), true),
				Utils.flipImage(Sprite.getSprite(3, 0), true),
				Utils.flipImage(Sprite.getSprite(4, 0), true),
				Utils.flipImage(Sprite.getSprite(5, 0), true),
				Utils.flipImage(Sprite.getSprite(6, 0), true),
				Utils.flipImage(Sprite.getSprite(7, 0), true),
				Utils.flipImage(Sprite.getSprite(8, 0), true),
				Utils.flipImage(Sprite.getSprite(9, 0), true),
				Utils.flipImage(Sprite.getSprite(10, 0), true),
				Utils.flipImage(Sprite.getSprite(11, 0), true),
				Utils.flipImage(Sprite.getSprite(0, 1), true)};
		
		BufferedImage[] standingImages = {Sprite.getSprite(0, 2), Sprite.getSprite(1, 2)};

		walkLeft = new Animation(walkingLeft, 1);
		walkRight = new Animation(walkingRight, 1);
		standing = new Animation(standingImages, 10);

		playerAnimation = standing;
		playerAnimation.start();

		playerState = State.BLACK;
		x = Utils.GAME_INSTANCE.currentLevel.startLocationX;
		y = Utils.GAME_INSTANCE.currentLevel.startLocationY;
	}

	public void update() {
		move();
		playerAnimation.update();
	}

	private void move() {
		old_x = x;
		old_y = y;
		x += xVelocity;
		y += yVelocity;

		yVelocity += Utils.GRAVITY;
		// Jumping is disabled by default, to prevent mid-air jumps, and only enabled again if there is a top collision on a platform.
		jumpPossible = false;


		// Collision checking
		ArrayList<Integer> collisionIndices = new ArrayList<Integer> (Utils.GAME_INSTANCE.platformCollision());


		while (!collisionIndices.isEmpty()) {
			Platform p = Utils.GAME_INSTANCE.currentLevel.visiblePlatforms.get(collisionIndices.get(0));
			collisionIndices.remove(0);

			p.performCollision(this);
		}

		// Check if the player jumps.
		if (jumpPressed && jumpPossible) {
			jumpPossible = false;
			yVelocity = -Utils.PLAYER_JUMP_STRENGTH;
		}

		if (y > Utils.GAME_INSTANCE.currentLevel.levelEndY) {
			Utils.GAME_INSTANCE.gameOver();
		}
	}

	public void paint(Graphics2D g) {
		g.drawImage(playerAnimation.getSprite(), (int)x, (int)y, (int)Utils.PLAYER_SIZE, (int)Utils.PLAYER_SIZE, null);
	}

	public Rectangle2D.Double getBounds() {
		return new Rectangle2D.Double(x, y, Utils.PLAYER_SIZE, Utils.PLAYER_SIZE);
	}

	public void keyReleased(KeyEvent e) {
		//        System.out.println("Key Released: " + e.getKeyText(e.getKeyCode()));
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			leftPressed = false;
			break;
		case KeyEvent.VK_RIGHT:
			rightPressed = false;
			break;
		case KeyEvent.VK_SPACE:
		case KeyEvent.VK_UP:
			jumpPressed = false;
			break;
		case KeyEvent.VK_Q:
			greenPressed = false;
			break;
		case KeyEvent.VK_W:
			bluePressed = false;
			break;
		case KeyEvent.VK_E:
			redPressed = false;
			break;
		default:
			break;
		}
		adjustDirection();
		adjustColor();
	}

	public void keyPressed(KeyEvent e) {
		//        System.out.println("Key Pressed: " + e.getKeyText(e.getKeyCode()));
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			leftPressed = true;
			break;
		case KeyEvent.VK_RIGHT:
			rightPressed = true;
			break;
		case KeyEvent.VK_SPACE:
		case KeyEvent.VK_UP:
			jumpPressed = true;
			break;
		case KeyEvent.VK_Q:
			greenPressed = true;
			break;
		case KeyEvent.VK_W:
			bluePressed = true;
			break;
		case KeyEvent.VK_E:
			redPressed = true;
			break;
		case KeyEvent.VK_ESCAPE:
			Utils.GAME_RUNNING = false;
			CardLayout cl = (CardLayout)Utils.GAME_INSTANCE.getParent().getLayout();
			cl.show(Utils.GAME_INSTANCE.getParent(), "Menu");
		default:
			break;
		}
		adjustDirection();
		adjustColor();
	}

	private void adjustDirection() {
		if (leftPressed) {
			xVelocity = -Utils.PLAYER_SPEED;
			if (playerAnimation != walkLeft) {
				changeAnimationTo(walkLeft);
			}
		}
		if (rightPressed) {
			xVelocity = Utils.PLAYER_SPEED;
			if (playerAnimation != walkRight) {
				changeAnimationTo(walkRight);
			}
		}
		if (leftPressed && rightPressed || !leftPressed && !rightPressed) {
			xVelocity = 0;
			if (playerAnimation != standing) {
				changeAnimationTo(standing);
			}
		}
	}
	
	private void changeAnimationTo (Animation a) {
		playerAnimation.stop();
		playerAnimation.reset();
		playerAnimation = a;
		playerAnimation.start();
	}

	private void adjustColor() {
		if (greenPressed) {
			playerState = State.GREEN;
		} else if (bluePressed) {
			playerState = State.BLUE;
		} else if (redPressed) {
			playerState = State.RED;
		} else {
			playerState = State.BLACK;
		}
	}
}
