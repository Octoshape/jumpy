package jumpy;

import java.util.List;

public class CreateCommand extends Command {

    public CreateCommand(Platform p) {
        super(p);
        
    }
    
    @Override
    public void undo(List<Platform> platforms) {
        if (platforms.contains(this.platform)) {
            platforms.remove(platforms.indexOf(this.platform));
        } else {
            platforms.add(this.platform);
        }
    }
    
    @Override
    public void redo(List<Platform> platforms) {
        if (platforms.contains(this.platform)) {
            platforms.remove(platforms.indexOf(this.platform));
        } else {
            platforms.add(this.platform);
        }
    }
}
