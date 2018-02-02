package jumpy;

import java.util.List;

public class MoveCommand extends Command {
    
    private Platform movedPlatform;
    
    public MoveCommand (Platform p, Platform newP) {
        super(p);
        movedPlatform = newP;
    }
    
    @Override
    public void undo(List<Platform> platforms) {
        
    }
    
    @Override
    public void redo(List<Platform> platforms) {

    }
}
