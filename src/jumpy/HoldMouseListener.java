package jumpy;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.TimerTask;

import javax.swing.JTextField;

public class HoldMouseListener implements MouseListener {
    volatile private boolean mouseDown = false;
    volatile private boolean isRunning = false;
    private JTextField textField;
    private boolean increasing;
    private java.util.Timer t;
    
    public HoldMouseListener(JTextField field, boolean increasing) {
        this.textField = field;
        this.increasing = increasing;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            mouseDown = true;
            if (increasing) {
                textField.setText(String.valueOf(Double.parseDouble(textField.getText()) + 1));
            } else {
                textField.setText(String.valueOf(Double.parseDouble(textField.getText()) - 1));
            }
            
            if (t == null)
            {
                t = new java.util.Timer();
            }
            initTimedTask();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            mouseDown = false;
            
            if (t != null) {
                t.cancel();
                t = null;
            }
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    private synchronized boolean checkAndMark() {
        if (isRunning) {
            return false;
        }
        isRunning = true;
        return true;
    }

    private void initTimedTask()  {
        if (checkAndMark()) {
            t.schedule(new TimerTask() {
                
                @Override
                public void run(){
                    do {
                        if (increasing) {
                            textField.setText(String.valueOf(Double.parseDouble(textField.getText()) + 1));
                        } else {
                            textField.setText(String.valueOf(Double.parseDouble(textField.getText()) - 1));
                        }
                        try { Thread.sleep(15); } catch (InterruptedException e) {}
                    } while (mouseDown);
                    isRunning = false;
                }
            }, 500);
        }
    }
}
