package jumpy;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import org.json.JSONException;
import org.json.JSONObject;

public class Goal extends Platform {
	
	public Goal(Rectangle2D.Double b) {
		super(b);
	}
	
	public void draw(Graphics2D g) {
   	 g.setColor(Color.MAGENTA);
        g.fill(body);
   }
	
	@Override
	public void performCollision(Player p) {
		Utils.GAME_INSTANCE.gameWon();
	}
	
	@Override
    public JSONObject getJSONFormat () throws JSONException {
    	JSONObject result = super.getJSONFormat();
    	result.put("type", "Goal");
    	return result;
    }
}
