package jumpy;

import java.util.List;

public class DeleteCommand extends Command {

    public DeleteCommand(Platform p) {
        super(p);
    }
    
    @Override
    public void undo(List<Platform> platforms) {
        super.undo(platforms);
    }
    
    @Override
    public void redo(List<Platform> platforms) {
        super.redo(platforms);
    }
}
