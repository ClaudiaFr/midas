package display;

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import util.Direction;

public class SensorButtonGroup extends JPanel {
  private static final long serialVersionUID = -3154036436928212098L;
  private static final int BASE = 30;
  private static final int MIN_SIZE = 30;
  public static final int SIZE_CHANGE = 5;
  public static final int BUFFER = 2;


  public List<ArduinoSensorButton> triggerButtons = new ArrayList<ArduinoSensorButton>();
  private Point base = new Point(BASE, BASE);

  public JButton orientationFlip = new JButton("make horizontal");
  public JButton larger = new JButton("larger");
  public JButton smaller = new JButton("smaller");
  public JButton delete = new JButton("delete");
  public JTextField nameField;

  private SensorShape.shapes shape;
  private int spacing = 5;
  private int size = MIN_SIZE + 4*SIZE_CHANGE;
  private Direction orientation = Direction.VERTICAL;

  public String name;

  public boolean deleteMe = false;
  public boolean isSlider;
  public boolean isPad;
  public Integer sensitivity;
  
  private boolean isIntersecting = false;

  public SensorButtonGroup(SensorShape.shapes shape) {
    isSlider = (shape == SensorShape.shapes.SLIDER);
    isPad = (shape == SensorShape.shapes.PAD);

    setLayout(new BorderLayout());

    initializeButtons();

    name = shape.name().toLowerCase();
    this.shape = shape;
    nameField = new JTextField(name);
    nameField.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void changedUpdate(DocumentEvent event) {
        JTextField target = (JTextField)event.getDocument();
        name = target.getText();
      }

      @Override
      public void insertUpdate(DocumentEvent event) {}
      @Override
      public void removeUpdate(DocumentEvent event) {} 
    });
  }

  public void setSensitivity(Integer sensitivity) {
    this.sensitivity = sensitivity;
    triggerButtons = new ArrayList<ArduinoSensorButton>();
    if (!isPad()) { // we have a slider or a single button
      if (orientation == Direction.VERTICAL) {
        for (int i = 0; i < sensitivity.intValue(); i++) {
          triggerButtons.add(new ArduinoSensorButton(shape, new Point(base.x, i*(size + spacing) + base.y), size));
        }
      } else {
        for (int i = 0; i < sensitivity.intValue(); i++) {
          triggerButtons.add(new ArduinoSensorButton(shape, new Point(i*(size + spacing) + base.x,base.y), size));
        }
      }
    } else { // we have a pad!
      for (int i = 0; i < Math.floor(Math.sqrt(sensitivity)); i++) {
        for (int j = 0; j < Math.floor(Math.sqrt(sensitivity)); j++) {
          triggerButtons.add(new ArduinoSensorButton(shape, new Point(i*(size + spacing) + base.x,
                                                                      j*(size + spacing) + base.y),
                                                     size));
        }
      }
    }
    repaint();
  }

  private void initializeButtons() {
    orientationFlip.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent event) {
        if (orientation == Direction.VERTICAL) {
          orientation = Direction.HORIZONTAL;
          orientationFlip.setText("make vertical");
        }
        else {
          orientation = Direction.VERTICAL;
          orientationFlip.setText("make horizontal");
        }
        setSensitivity(sensitivity);
        repaint();
      }
    });
    smaller.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent event) {
        size -= SIZE_CHANGE;
        if (size < MIN_SIZE) { size = MIN_SIZE; }
        else {
          for (ArduinoSensorButton button : triggerButtons) {
            button.smaller();
          }
        }
        moveTo(base);
        repaint();
      }
    });
    larger.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent event) {
        size += SIZE_CHANGE;
        for (ArduinoSensorButton button : triggerButtons) {
          button.larger();
        }
        moveTo(base);
        repaint();
      }
    });
    delete.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent event) {
        deleteMe = true;
      }
    });
  }

  public void paint(Graphics2D g) {
    if (!deleteMe) {
      for (ArduinoSensorButton button : triggerButtons) {
        button.paint(g);
      }
    }
  }

  public String toString() {
    return name;
  }

  @Override
  public boolean contains(Point p) {
    boolean contains = false;
    for (ArduinoSensorButton button : triggerButtons) {
      contains |= button.contains(p);
    }
    return contains;
  }

  public boolean contains(ArduinoSensorButton button) {
    return triggerButtons.contains(button);
  }

  public void moveTo(Point p) {
    base = p;
    if (isPad()) { // we have a pad
      for (int i = 0; i < Math.floor(Math.sqrt(sensitivity)); i++) {
        for (int j = 0; j < Math.floor(Math.sqrt(sensitivity)); j++) {
          triggerButtons.get((int) (i * Math.floor(Math.sqrt(sensitivity)) + j))
                         .moveTo(new Point(i * (size + spacing) + base.x,
                                           j * (size + spacing) + base.y));
        }
      }
    } else { // we have a slider or single button
      if(orientation == Direction.HORIZONTAL) {
        for (int i=0; i<sensitivity; i++) {
          triggerButtons.get(i).moveTo(new Point(i * (size + spacing) + base.x,
                                                 base.y));
        }
      }
      else {
        for (int i=0; i<sensitivity; i++) {
          triggerButtons.get(i).moveTo(new Point(base.x,
                                                 i * (size + spacing) + base.y));
        }
      }
    }
    repaint();
  }
  
  private boolean isPad() {
    return sensitivity >= 9;
  }
  
  public JTextField nameField() {
    return nameField;
  }
  
  public void setSelected(boolean selected) {
    for (ArduinoSensorButton button : triggerButtons) {
      button.setSelected(selected);
    }
  }
  
  public void setIntersecting(boolean intersecting) {
    for (ArduinoSensorButton button : triggerButtons) {
      button.setIntersecting(intersecting);
    }
    isIntersecting = intersecting;
  }
  
  public boolean isIntersecting() {
    return isIntersecting;
  }
  
  public boolean intersects(Rectangle rectangle) {
    for(ArduinoSensorButton button : triggerButtons) {
      if (button.intersects(rectangle)) {
        return true;
      }
    }
    return false;
  }
  
  @Override
  public Rectangle getBounds() {
    
    // we want the upper corner of the first one and the lower corner of the last one
    Rectangle first = triggerButtons.get(0).getBounds();
    Rectangle last = triggerButtons.get(triggerButtons.size() -1).getBounds();
    
    Rectangle bounds = new Rectangle(first.x, first.y, (last.x-first.x) + last.width, (last.y-first.y) + last.width);
    bounds.grow(BUFFER, BUFFER);
    return bounds;
  }
}
