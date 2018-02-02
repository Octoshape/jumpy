package jumpy;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class Level {
    public List<Platform> allPlatforms, visiblePlatforms;
    public double startLocationX, startLocationY, levelEndY;
    
    public Level(List<Platform> platforms, double startX, double startY, double endY) {
        allPlatforms = platforms;
        visiblePlatforms = new ArrayList<>();
        startLocationX = startX;
        startLocationY = startY;
        levelEndY = endY;
    }
    
    public Level() {
        allPlatforms = new ArrayList<>();
        visiblePlatforms = new ArrayList<>();

        double SIZE = 20;
        double JUMP = 100;
        double x = 1000.0, y = 500, z = 4 * JUMP + 4 * SIZE;
        double GAP = 400;
        double GROUND = 7 * JUMP + 5 * SIZE;
        startLocationX = 50;
        startLocationY = GROUND - Utils.PLAYER_SIZE - 1;
        levelEndY = GROUND + 400;
        allPlatforms.add(new Platform(new Rectangle2D.Double(0, -200, SIZE, GROUND + 200 + SIZE))); // 1
        allPlatforms.add(new Platform(new Rectangle2D.Double(SIZE, GROUND, x, SIZE))); // 2
        allPlatforms.add(new Platform(new Rectangle2D.Double(2*x/3, GROUND - JUMP, SIZE, JUMP))); // 3
        allPlatforms.add(new Platform(new Rectangle2D.Double(x - 50, -200, SIZE, GROUND + 100))); // 4
        allPlatforms.add(new Platform(new Rectangle2D.Double(x + 50 + GAP, -200, SIZE, GROUND + 100))); // 5
        allPlatforms.add(new Platform(new Rectangle2D.Double(x + GAP, GROUND, y, SIZE))); // 6
        allPlatforms.add(new Platform(new Rectangle2D.Double(x + GAP + y - SIZE, GROUND - z, SIZE, z))); // 7
        allPlatforms.add(new Platform(new Rectangle2D.Double(x + GAP + 50 + 2*(y - 50)/3, GROUND - JUMP, (y - 50)/3, SIZE))); // 8
        allPlatforms.add(new Platform(new Rectangle2D.Double(x + GAP + 50 + SIZE, GROUND - 2*JUMP, (y - 50)/3, SIZE))); // 9
        allPlatforms.add(new Platform(new Rectangle2D.Double(x + GAP + 50 + 2*(y - 50)/3, GROUND - 3*JUMP, (y - 50)/3, SIZE))); // 10
        allPlatforms.add(new Platform(new Rectangle2D.Double(x + GAP + 50 + SIZE, GROUND - 4*JUMP, 2*(y - 50)/3, SIZE))); // 11
        allPlatforms.add(new Platform(new Rectangle2D.Double(x + GAP + y - 100, -200, SIZE, 430))); // 12
        allPlatforms.add(new Platform(new Rectangle2D.Double(x + 2*GAP + y + 100, -200, SIZE, 430))); // 13
        allPlatforms.add(new Platform(new Rectangle2D.Double(x + 2*GAP + y, GROUND - z, SIZE, z))); // 14
        allPlatforms.add(new Platform(new Rectangle2D.Double(x + 2*GAP + y + SIZE, GROUND - 4*JUMP, (y - 50)/3, SIZE))); // 15
        allPlatforms.add(new Platform(new Rectangle2D.Double(x + 2*GAP + y + y/3 + SIZE, GROUND - 3*JUMP, 2*y/3, SIZE))); // 16
        allPlatforms.add(new Platform(new Rectangle2D.Double(x + 2*GAP + y + SIZE, GROUND - 2*JUMP, (y - 50)/3, SIZE))); // 17
        allPlatforms.add(new Platform(new Rectangle2D.Double(x + 2*GAP + y, GROUND, y, SIZE))); // 18
        allPlatforms.add(new Platform(new Rectangle2D.Double(x + 2 * GAP + 2 * y, -200, SIZE, GROUND + 200 + SIZE))); // right wall

        allPlatforms.add(new Goal(new Rectangle2D.Double(x + 2 * GAP + 2 * y - 50, GROUND - 50, 33, 33)));

        allPlatforms.add(new Obstacle(new Rectangle2D.Double(x/3, GROUND-JUMP, SIZE, JUMP), Obstacle.ObstacleState.RED)); // 19
        allPlatforms.add(new Obstacle(new Rectangle2D.Double(2*x/3, GROUND-2*JUMP, SIZE, JUMP), Obstacle.ObstacleState.RED)); // 20
        allPlatforms.add(new Obstacle(new Rectangle2D.Double(x + GAP + 50 + SIZE, GROUND - 3*JUMP, 2*(y - 50)/3, SIZE), Obstacle.ObstacleState.RED)); // 21
        allPlatforms.add(new Obstacle(new Rectangle2D.Double(x + 2*GAP + y + SIZE, GROUND - JUMP, y - SIZE, SIZE), Obstacle.ObstacleState.RED)); // 22

        allPlatforms.add(new Obstacle(new Rectangle2D.Double(x - 50 + SIZE, GROUND - JUMP - SIZE, GAP + 100 - SIZE, SIZE), Obstacle.ObstacleState.GREEN)); // 23
        allPlatforms.add(new Obstacle(new Rectangle2D.Double(x + GAP + y - 100 + SIZE, 230 - SIZE, GAP + 200 - SIZE, SIZE), Obstacle.ObstacleState.GREEN)); // 24
    }

    public void paint(Graphics2D g) {
        // Calculate visible platforms
        visiblePlatforms.clear();
        double offset = g.getTransform().getTranslateX();
        for (Platform r : allPlatforms) {
            if (r.body.getMinX() < offset + Utils.GAME_WINDOW_WIDTH || r.body.getMaxX() > offset) {
                visiblePlatforms.add(r);
            }
        }

        // Draw all visible platforms of the level.
        for (Platform r : visiblePlatforms) {
            r.draw(g);
        }
   }
}