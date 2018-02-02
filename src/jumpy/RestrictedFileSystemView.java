package jumpy;

import java.io.File;
import java.io.IOException;

import javax.swing.filechooser.FileSystemView;

public class RestrictedFileSystemView extends FileSystemView {

    private final File rootDirectory;

    public RestrictedFileSystemView(File rootDirectory)
    {
        this.rootDirectory = rootDirectory;
    }

    @Override
    public File createNewFolder(File containingDir) throws IOException
    {       
        return null;
    }

    @Override
    public File[] getRoots()
    {
        return new File[] {rootDirectory};
    }

    @Override
    public boolean isRoot(File file)
    {
        if (this.rootDirectory.equals(file)) {
            return true;
        }
        return false;
    }

    @Override
    public File getHomeDirectory()
    {
        return rootDirectory;
    }
    
    @Override
    public File getParentDirectory(File dir) {
        return dir;
    }
    
    
}
