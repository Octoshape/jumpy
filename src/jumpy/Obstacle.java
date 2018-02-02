package jumpy;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import org.json.JSONException;
import org.json.JSONObject;

public class Obstacle extends Platform {
    public enum ObstacleState { RED, GREEN, BLUE };

    ObstacleState state;
    private BufferedImage platformImage;

    public Obstacle(Rectangle2D.Double b, ObstacleState state) {
        super(b);
        this.state = state;
    }

    @Override
    public void draw(Graphics2D g) {
        switch (state) {
        case RED:
            drawRed(g);
            return;
        case GREEN:
            g.setColor(Color.GREEN);
            break;
        default:
            g.setColor(Color.BLACK);
            break;
        }
        g.fill(body);
    }

    private void drawRed(Graphics2D g) {
        platformImage = new BufferedImage((int)body.getWidth(), (int)body.getHeight(), BufferedImage.TYPE_INT_RGB);

        Graphics2D graph = platformImage.createGraphics();
        int currentWidth = 0;
        while (currentWidth < body.width) {
            graph.drawImage(Utils.RED_OBSTACLE_IMAGE, currentWidth, 0, null);
            int currentHeight = Utils.RED_OBSTACLE_IMAGE.getHeight();
            while (currentHeight < body.height) {
                graph.drawImage(Utils.RED_OBSTACLE_IMAGE, currentWidth, currentHeight, null);
                currentHeight += Utils.RED_OBSTACLE_IMAGE.getHeight();
            }
            currentWidth += Utils.RED_OBSTACLE_IMAGE.getWidth();
        }


        g.drawImage(platformImage, null, (int)body.getX(), (int)body.getY());
    }

    @Override
    public void performCollision(Player p) {
        switch (this.state) {
        case BLUE:
            // TODO Implement me.
            break;
        case GREEN:
            p.yVelocity = 0;
            p.y = body.getMaxY() - 1;
            if (p.playerState != Player.State.GREEN) {
                p.y = body.getMaxY();
            }
            break;
        case RED:
            if (p.playerState != Player.State.RED) {
                Utils.GAME_INSTANCE.gameOver();
            }
            break;
        default:
            break;

        }
    }
    
    @Override
    public JSONObject getJSONFormat () throws JSONException {
    	JSONObject result = super.getJSONFormat();
    	result.put("type", "Obstacle");
    	result.put("subtype", state);
    	return result;
    }
}
