package jumpy;

import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicArrowButton;

public class PropertyPanel extends JPanel {

    final JTextField xField = new JTextField(5);
    final JTextField yField = new JTextField(5);
    final JTextField hField = new JTextField(5);
    final JTextField wField = new JTextField(5);
    private Platform selectedPlatform;

    DocumentListener dl = new DocumentListener() {

        @Override
        public void removeUpdate(DocumentEvent arg0) {
            updateRectangle();
        }

        @Override
        public void insertUpdate(DocumentEvent arg0) {
            updateRectangle();
        }

        @Override
        public void changedUpdate(DocumentEvent arg0) {
            updateRectangle();
        }
    };

    public PropertyPanel(Platform platform) {
        this.selectedPlatform = platform;
        
        xField.setText(Double.toString(selectedPlatform.body.getX()));
        yField.setText(Double.toString(selectedPlatform.body.getY()));
        wField.setText(Double.toString(selectedPlatform.body.getWidth()));
        hField.setText(Double.toString(selectedPlatform.body.getHeight()));

        xField.getDocument().addDocumentListener(dl);
        yField.getDocument().addDocumentListener(dl);
        wField.getDocument().addDocumentListener(dl);
        hField.getDocument().addDocumentListener(dl);

        JPanel heightArrows = new JPanel();
        heightArrows.setLayout(new BoxLayout(heightArrows, BoxLayout.Y_AXIS));
        BasicArrowButton upHeight = new BasicArrowButton(BasicArrowButton.NORTH);
        upHeight.addMouseListener(new HoldMouseListener(hField, true));
        BasicArrowButton downHeight = new BasicArrowButton(BasicArrowButton.SOUTH);
        downHeight.addMouseListener(new HoldMouseListener(hField, false));
        heightArrows.add(upHeight);
        heightArrows.add(downHeight);

        JPanel widthArrows = new JPanel();
        widthArrows.setLayout(new BoxLayout(widthArrows, BoxLayout.Y_AXIS));
        BasicArrowButton upWidth = new BasicArrowButton(BasicArrowButton.NORTH);
        upWidth.addMouseListener(new HoldMouseListener(wField, true));
        BasicArrowButton downWidth = new BasicArrowButton(BasicArrowButton.SOUTH);
        downWidth.addMouseListener(new HoldMouseListener(wField, false));
        widthArrows.add(upWidth);
        widthArrows.add(downWidth);

        JPanel xArrows = new JPanel();
        xArrows.setLayout(new BoxLayout(xArrows, BoxLayout.Y_AXIS));
        BasicArrowButton upX = new BasicArrowButton(BasicArrowButton.NORTH);
        upX.addMouseListener(new HoldMouseListener(xField, true));
        BasicArrowButton downX = new BasicArrowButton(BasicArrowButton.SOUTH);
        downX.addMouseListener(new HoldMouseListener(xField, false));
        xArrows.add(upX);
        xArrows.add(downX);

        JPanel yArrows = new JPanel();
        yArrows.setLayout(new BoxLayout(yArrows, BoxLayout.Y_AXIS));
        BasicArrowButton upY = new BasicArrowButton(BasicArrowButton.NORTH);
        upY.addMouseListener(new HoldMouseListener(yField, true));
        BasicArrowButton downY = new BasicArrowButton(BasicArrowButton.SOUTH);
        downY.addMouseListener(new HoldMouseListener(yField, false));
        yArrows.add(upY);
        yArrows.add(downY);

        this.setLayout(new GridLayout(2, 5, 10, 5));
        this.add(new JLabel("x:"));
        this.add(xField);
        this.add(xArrows);
        this.add(new JLabel("height:"));
        this.add(hField);
        this.add(heightArrows);
        this.add(new JLabel("y:"));
        this.add(yField);
        this.add(yArrows);
        this.add(new JLabel("width:"));
        this.add(wField);
        this.add(widthArrows);
    }

    private void updateRectangle() {
        try {
            double x = Double.parseDouble(xField.getText());
            double y = Double.parseDouble(yField.getText());
            double w = Double.parseDouble(wField.getText());
            double h = Double.parseDouble(hField.getText());

            if (x * y * w * h != 0) { // Check if all values are nonzero.
                selectedPlatform.body.setRect(x, y, w, h);
                selectedPlatform.invalidate();
            }
        } catch (NumberFormatException e) {

        }
    }
}
