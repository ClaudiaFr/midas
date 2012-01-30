package capture;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Icon;
import javax.swing.JButton;

import serialtalk.ArduinoEvent;
import serialtalk.ArduinoSensor;
import serialtalk.TouchDirection;

public class ArduinoJButton extends JButton {
  private static final long serialVersionUID = -5603499266721585353L;
  public ArduinoSensor sensor;
  String name = null;
  boolean locationChecked = false;
  
  public ArduinoJButton(Icon shape, ArduinoSensor sensor) {
    super(shape);
    this.sensor = sensor;
    this.addMouseListener(new MouseListener() {
      public void mousePressed(MouseEvent event) {
        ArduinoEvent triggered = new ArduinoEvent(((ArduinoJButton)event.getComponent()).sensor,
                                                  TouchDirection.DOWN);
        SetUp.serialCommunication.handleCompleteEvent(triggered);
        activate();
      }

      public void mouseReleased(MouseEvent event) {
        ArduinoEvent triggered = new ArduinoEvent(((ArduinoJButton)event.getComponent()).sensor,
                                                  TouchDirection.UP);
        SetUp.serialCommunication.handleCompleteEvent(triggered);
        deactivate();
      }
      
      public void mouseClicked(MouseEvent event) {
        this.mousePressed(event);
        this.mouseReleased(event);
      }

      public void mouseEntered(MouseEvent event) {}
      public void mouseExited(MouseEvent event) {}

    });
  }
  
  public void rotateLeft() {
    
  }
  public void rotateRight() {
    
  }
  public void activate() {
    setBackground(Color.orange);
  }
  public void deactivate() {
    setBackground(null);
  }
  
  public void name(String name) {
    this.name = name;
  }
  public boolean locationChecked() {
    return locationChecked;
  }
}