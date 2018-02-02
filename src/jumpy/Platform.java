package jumpy;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import org.json.JSONException;
import org.json.JSONObject;

public class Platform {
    Rectangle2D.Double body;
    private BufferedImage platformImage = null;

    public Platform(Rectangle2D.Double b) {
        body = b;
    }
    
    public void invalidate() {
        platformImage = null;
    }

    public void draw(Graphics2D g) {
            	 g.setColor(Color.BLACK);
                 g.fill(body);
        if (platformImage == null) {
            //join
            platformImage = new BufferedImage((int)body.getWidth(), (int)body.getHeight(), BufferedImage.TYPE_INT_RGB);

            Graphics2D graph = platformImage.createGraphics();
            int currentWidth = 0;
            while (currentWidth < body.width) {
                graph.drawImage(Utils.PLATFORM_IMAGE, currentWidth, 0, null);
                int currentHeight = Utils.PLATFORM_IMAGE.getHeight();
                while (currentHeight < body.height) {
                    graph.drawImage(Utils.DIRT_IMAGE, currentWidth, currentHeight, null);
                    currentHeight += Utils.DIRT_IMAGE.getHeight();
                }
                currentWidth += Utils.PLATFORM_IMAGE.getWidth();
            }
            
            
        }
        g.drawImage(platformImage, null, (int)body.getX(), (int)body.getY());
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof Platform) {
            return this.body.equals(((Platform) o).body);
        }
        return false;
    }

    @Override
    public int hashCode(){
        return body.hashCode();
    }

    public void performCollision (Player p) {
        if (body.getMinY() >= p.old_y + Utils.PLAYER_SIZE) {
            // Top collision
            p.y = body.getMinY() - Utils.PLAYER_SIZE;
            p.yVelocity = Utils.GRAVITY;
            p.jumpPossible = true;
        } else if (body.getMaxY() <= p.old_y) {
            // Bottom collision
            p.yVelocity = 0;
            p.y = body.getMaxY();
        } else if (body.getMinX() >= p.old_x + Utils.PLAYER_SIZE) {
            // Left collision
            p.x = body.getMinX() - Utils.PLAYER_SIZE;
        } else if (body.getMaxX() <= p.old_x) {
            // Right collision
            p.x = body.getMaxX();
        }
    }
    
    public JSONObject getJSONFormat () throws JSONException {
    	JSONObject result = new JSONObject();
    	result.put("x", body.getX());
    	result.put("y", body.getY());
    	result.put("width", body.getWidth());
    	result.put("height", body.getHeight());
    	result.put("type", "Platform");
    	return result;
    }
}
