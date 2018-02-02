package jumpy;

import java.util.List;

public class Command {
    
    protected Platform platform;
    
    public Command(Platform p) {
        platform = p;
    }
    
    public void undo (List<Platform> platforms) {
    }
    
    public void redo (List<Platform> platforms) {
    }
}
