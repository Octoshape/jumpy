package jumpy;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.MouseInputListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import jumpy.Obstacle.ObstacleState;

public class LevelEditor extends JPanel implements MouseInputListener {

    private final double MIN_WIDTH = 30;
    private final double MIN_HEIGHT = 30;

    List<Platform> platforms;
    List<Command> undoList;
    public enum Type { PLATFORM, OBSTACLE, GOAL }
    public enum ObstacleType { RED, GREEN, BLUE }

    private Type currentType;
    private ObstacleType currentObstacleType;
    private String currentTypeDescription;
    private boolean settingSpawnPoint;
    private boolean movingPlatform;
    private Platform selectedPlatform;
    private JMenuBar menuBar;

    private Rectangle currentSelectionRectangle;
    private Point startingPoint, lastPoint = null;
    private double currentCameraOffsetX = 0, currentCameraOffsetY = 0;
    private int undoListIndex = -1;
    private Ellipse2D.Double playerShape;
    private double deathY;

    public LevelEditor (final Controller c) {
        resetVars();
        setLayout(new BorderLayout());

        JMenu levelMenu = new JMenu("File");
        JMenuItem menuStartPointItem = new JMenuItem("Set spawn point");
        menuStartPointItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
                settingSpawnPoint = true;
            }
        });
        JMenuItem menuPlayItem = new JMenuItem("Play");
        menuPlayItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                Utils.EDITOR_RUNNING = false;
                c.runGame(new Level(platforms, playerShape.getX(), playerShape.getY(), deathY));
            }
        });
        JMenuItem menuSaveItem = new JMenuItem("Save");
        menuSaveItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                String fileName = null;
                RestrictedFileSystemView fsv = new RestrictedFileSystemView(new File(Utils.LEVEL_DIRECTORY));
                JFileChooser c = new JFileChooser(fsv.getHomeDirectory(), fsv);
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Jumpy Levels", "level");
                c.setFileFilter(filter);
                c.setAcceptAllFileFilterUsed(false);
                boolean keepAsking;
                do {
                    keepAsking = false;
                    int rVal = c.showSaveDialog(LevelEditor.this);
                    if (rVal == JFileChooser.APPROVE_OPTION) {
                        fileName = c.getSelectedFile().getName();
                        if (c.getSelectedFile().exists()) {
                            // Overwriting an existing level
                            int selectedOption = JOptionPane.showConfirmDialog(c, 
                                    fileName + " already exists. Do you want to overwrite it?", 
                                    "File already exists", 
                                    JOptionPane.YES_NO_OPTION);

                            if (selectedOption == JOptionPane.YES_OPTION) {
                                c.getSelectedFile().delete();
                            } else {
                                keepAsking = true;
                            }
                        }
                    } 
                    if (rVal == JFileChooser.CANCEL_OPTION) {
                        // User cancelled saving of level.
                        return;
                    }
                } while (keepAsking);

                if (!fileName.contains(".level")) {
                    fileName = fileName + ".level";
                }

                try (FileWriter writer = new FileWriter(Utils.LEVEL_DIRECTORY + fileName)) {
                    JSONObject level = new JSONObject();
                    JSONArray platforms = new JSONArray();

                    for (Platform p : LevelEditor.this.platforms) {
                        platforms.put(p.getJSONFormat());
                    }

                    level.put("platforms", platforms);
                    level.put("spawnPointX", LevelEditor.this.playerShape.x);
                    level.put("spawnPointY", LevelEditor.this.playerShape.y);
                    level.put("deathY", LevelEditor.this.deathY);

                    writer.write(level.toString());
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        JMenuItem menuLoadItem = new JMenuItem("Load");
        menuLoadItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {

                String fileName = null;
                RestrictedFileSystemView fsv = new RestrictedFileSystemView(new File(Utils.LEVEL_DIRECTORY));
                JFileChooser c = new JFileChooser(fsv.getHomeDirectory(), fsv);
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Jumpy Levels", "level");
                c.setFileFilter(filter);
                c.setAcceptAllFileFilterUsed(false);
                int rVal = c.showOpenDialog(LevelEditor.this);
                if (rVal == JFileChooser.APPROVE_OPTION) {
                    fileName = c.getSelectedFile().getName();
                } 
                if (rVal == JFileChooser.CANCEL_OPTION) {
                    // User cancelled loading of level.
                    return;
                }

                try (BufferedReader breader = new BufferedReader(new FileReader(Utils.LEVEL_DIRECTORY + fileName))) {
                    JSONObject level = new JSONObject(breader.readLine());

                    JSONArray platformsArray = (JSONArray) level.get("platforms");
                    double spawnPointX = level.getDouble("spawnPointX");
                    double spawnPointY = level.getDouble("spawnPointY");
                    double deathY = level.getDouble("deathY");

                    List<Platform> loadedPlatforms = new ArrayList<Platform>();

                    for (int i = 0; i < platformsArray.length(); i++) {
                        JSONObject nextPlatform = platformsArray.getJSONObject(i);
                        double x = nextPlatform.getDouble("x");
                        double y = nextPlatform.getDouble("y");
                        double width = nextPlatform.getDouble("width");
                        double height = nextPlatform.getDouble("height");
                        String type = nextPlatform.getString("type");

                        switch (type) {
                        case "Platform":
                            loadedPlatforms.add(new Platform(new Rectangle2D.Double(x, y, width, height)));
                            break;
                        case "Obstacle":
                            ObstacleState state = ObstacleState.valueOf(nextPlatform.getString("subtype"));
                            loadedPlatforms.add(new Obstacle(new Rectangle2D.Double(x, y, width, height), state));
                            break;
                        case "Goal":
                            loadedPlatforms.add(new Goal(new Rectangle2D.Double(x, y, width, height)));
                        default:
                            break;
                        }
                    }

                    LevelEditor.this.platforms = new ArrayList<Platform>(loadedPlatforms);
                    LevelEditor.this.playerShape = new Ellipse2D.Double(spawnPointX, spawnPointY, Utils.PLAYER_SIZE, Utils.PLAYER_SIZE);
                    LevelEditor.this.deathY = deathY;

                } catch (JSONException e) {
                    JOptionPane.showMessageDialog(LevelEditor.this, "Level file " + fileName + " is corrupted.");
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    JOptionPane.showMessageDialog(LevelEditor.this, "Level file " + fileName + " not found.");
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }            	
            }
        });
        JMenuItem menuResetItem = new JMenuItem("Reset");
        menuResetItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                resetVars();
            }
        });

        levelMenu.add(menuStartPointItem);
        levelMenu.add(menuPlayItem);
        levelMenu.add(menuSaveItem);
        levelMenu.add(menuLoadItem);
        levelMenu.add(menuResetItem);

        JMenu editMenu = new JMenu("Edit");
        JMenuItem menuUndoItem = new JMenuItem("Undo");
        menuUndoItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                undoAction();
            }
        });
        JMenuItem menuRedoItem = new JMenuItem("Redo");
        menuRedoItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                redoAction();
            }
        });

        editMenu.add(menuUndoItem);
        editMenu.add(menuRedoItem);

        JMenu typeMenu = new JMenu("Select Type");
        JMenuItem menuPlatformItem = new JMenuItem("Platform");
        menuPlatformItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                currentType = Type.PLATFORM;
                currentTypeDescription = "Selection: Platforms";
            }
        });

        JMenuItem menuGoalItem = new JMenuItem("Goal");
        menuGoalItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                currentType = Type.GOAL;
                currentTypeDescription = "Selection: Goals";
            }
        });

        JMenu obstacleSubMenu = new JMenu("Obstacles");

        JMenuItem menuRedObstacleItem = new JMenuItem("Red Obstacle");
        menuRedObstacleItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                currentType = Type.OBSTACLE;
                currentObstacleType = ObstacleType.RED;
                currentTypeDescription = "Selection: Red Obstacles";
            }
        });

        JMenuItem menuGreenObstacleItem = new JMenuItem("Green Obstacle");
        menuGreenObstacleItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                currentType = Type.OBSTACLE;
                currentObstacleType = ObstacleType.GREEN;
                currentTypeDescription = "Selection: Green Obstacles";
            }
        });

        JMenuItem menuBlueObstacleItem = new JMenuItem("Blue Obstacle");
        menuBlueObstacleItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                currentType = Type.OBSTACLE;
                currentObstacleType = ObstacleType.BLUE;
                currentTypeDescription = "Selection: Blue Obstacles";
            }
        });

        obstacleSubMenu.add(menuRedObstacleItem);
        obstacleSubMenu.add(menuGreenObstacleItem);
        obstacleSubMenu.add(menuBlueObstacleItem);

        typeMenu.add(menuPlatformItem);
        typeMenu.add(obstacleSubMenu);
        typeMenu.add(menuGoalItem);

        menuBar = new JMenuBar();
        menuBar.add(levelMenu);
        menuBar.add(editMenu);
        menuBar.add(typeMenu);
        add(menuBar, BorderLayout.NORTH);

        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    Utils.EDITOR_RUNNING = false;
                    CardLayout cl = (CardLayout)LevelEditor.this.getParent().getLayout();
                    cl.show(LevelEditor.this.getParent(), "Menu");
                }

                if ((e.getKeyCode() == KeyEvent.VK_Z) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                    // Undo
                    undoAction();
                }

                if ((e.getKeyCode() == KeyEvent.VK_Y) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                    // Redo
                    redoAction();
                }
            }
        });
    }

    private void resetVars() {
        platforms = new ArrayList<>();
        undoList = new ArrayList<>();
        undoListIndex = -1;
        currentType = Type.PLATFORM;
        currentObstacleType = ObstacleType.RED;
        currentTypeDescription = "Selection: Platforms";
        currentSelectionRectangle = new Rectangle();
        deathY = 1000;
        startingPoint = null;
        playerShape = new Ellipse2D.Double(100, 100, Utils.PLAYER_SIZE, Utils.PLAYER_SIZE);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Do nothing.
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // Do nothing.
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // Do nothing.
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3) {
            lastPoint = e.getPoint();
            Point currentPoint = new Point(lastPoint);
            currentPoint.translate((int)currentCameraOffsetX, (int)currentCameraOffsetY);
            selectedPlatform = isMouseOnPlatform(currentPoint);
            if (selectedPlatform != null) {
                PlatformPopupMenu menu = new PlatformPopupMenu(selectedPlatform);
                menu.show(e.getComponent(), e.getXOnScreen(), e.getYOnScreen() - menuBar.getHeight());
            }
        } else {
            startingPoint = e.getPoint();
            startingPoint.translate((int)currentCameraOffsetX, (int)currentCameraOffsetY);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (settingSpawnPoint) {
            Point currentPoint = e.getPoint();
            currentPoint.translate((int)currentCameraOffsetX, (int)currentCameraOffsetY);
            playerShape = new Ellipse2D.Double(currentPoint.getX() - Utils.PLAYER_SIZE / 2, currentPoint.getY() - Utils.PLAYER_SIZE / 2, Utils.PLAYER_SIZE, Utils.PLAYER_SIZE);
            settingSpawnPoint = false;
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            return;
        }

        if (e.getButton() == MouseEvent.BUTTON1) {
            addPlatform();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (settingSpawnPoint) {
            return;
        }

        if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) == MouseEvent.BUTTON3_MASK) {
            // Right mouse button is pressed.
            currentCameraOffsetX += lastPoint.getX() - e.getPoint().getX();
            currentCameraOffsetY += lastPoint.getY() - e.getPoint().getY();
            lastPoint = e.getPoint();
        } else {
            currentSelectionRectangle = new Rectangle(startingPoint);
            Point newPoint = e.getPoint();
            newPoint.translate((int)currentCameraOffsetX, (int)currentCameraOffsetY);
            currentSelectionRectangle.add(newPoint);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    private void addPlatform() {
        if (currentSelectionRectangle.getWidth() == 0 || currentSelectionRectangle.getHeight() == 0) {
            return;
        }

        Rectangle2D.Double newRect = new Rectangle2D.Double (currentSelectionRectangle.getX(), 
                currentSelectionRectangle.getY(), 
                Math.max(currentSelectionRectangle.getWidth(), MIN_WIDTH), 
                Math.max(currentSelectionRectangle.getHeight(), MIN_HEIGHT));
        currentSelectionRectangle = new Rectangle();

        switch (currentType) {
        case GOAL:
            platforms.add(new Goal(newRect));
            break;
        case OBSTACLE:
            ObstacleState obsState = null;
            switch (currentObstacleType) {
            case BLUE:
                obsState = ObstacleState.BLUE;
                break;
            case GREEN:
                obsState = ObstacleState.GREEN;
                break;
            case RED:
                obsState = ObstacleState.RED;
                break;
            default:
                break;
            }
            platforms.add(new Obstacle(newRect, obsState));
            break;
        case PLATFORM:
            platforms.add(new Platform(newRect));
        default:
            break;
        }

        undoListIndex++;
        undoList.add(undoListIndex, new CreateCommand(platforms.get(platforms.size() - 1)));
        cleanUpUndoList();
    }

    private void cleanUpUndoList () {
        // Deletion of undoList from undoListIndex + 1 to undoList.size().
        int tempIndex = undoListIndex + 1;
        while (tempIndex < undoList.size()) {
            undoList.remove(tempIndex);
        }
    }

    private void undoAction() {
        if (undoListIndex >= 0) {
            Command lastCommand = undoList.get(undoListIndex);
            undoListIndex--;
            lastCommand.undo(platforms);
        }
    }

    private void redoAction() {
        if (undoListIndex < undoList.size() - 1) {
            undoListIndex++;
            Command nextCommand = undoList.get(undoListIndex);

            nextCommand.redo(platforms);
        }
    }

    private Platform isMouseOnPlatform(Point point) {
        for (Platform p : platforms) {
            if (p.body.contains(point)) {
                return p;
            }
        }
        return null;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(-currentCameraOffsetX, -currentCameraOffsetY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.drawString(currentTypeDescription, (int)currentCameraOffsetX + 10, (int)currentCameraOffsetY + 50);
        g2d.draw(currentSelectionRectangle);
        g2d.fill(playerShape);

        // Paint level
        for (Platform p : platforms) {
            p.draw(g2d);
        }
    }

    private class PlatformPopupMenu extends JPopupMenu {
        public PlatformPopupMenu (final Platform p) {
            JMenuItem moveItem = new JMenuItem("Move");
            moveItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent arg0) {
                    movingPlatform = true;
                }
            });
            JMenuItem deleteItem = new JMenuItem("Delete");
            deleteItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent arg0) {
                    platforms.remove(p);
                    undoListIndex++;
                    undoList.add(undoListIndex, new DeleteCommand(p));
                    cleanUpUndoList();
                }
            });
            JMenuItem setPropertiestItem = new JMenuItem("Set properties");
            setPropertiestItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    JOptionPane.showMessageDialog(null, new PropertyPanel(selectedPlatform), "Please enter the desired values.", JOptionPane.QUESTION_MESSAGE);
                }
            });

            add(moveItem);
            add(deleteItem);
            add(setPropertiestItem);
        }
    }
}