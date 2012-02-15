package bridge;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import serialtalk.ArduinoObject;
import serialtalk.ArduinoSensor;
import capture.UIScript;
import display.SensorButtonGroup;
import display.SensorShape;

public class ArduinoToButtonBridge implements ArduinoToDisplayBridge {
  private static final SensorButtonGroup nullInterface = new SensorButtonGroup(SensorShape.shapes.SQUARE);
  private static final ArduinoSensor nullSensor = new ArduinoSensor(-1,-1);
  
  public SensorButtonGroup interfacePiece = nullInterface;
  public ArduinoObject arduinoPiece = nullSensor;
  public UIScript interactivePiece = new UIScript();
    
  public ArduinoToButtonBridge() {}
    
  public String toString() {
    if (interfacePiece.name != null) {
      return interfacePiece.name;
    }
    return "unknown";
  }
  
  public ArduinoObject arduinoPiece() {
    return arduinoPiece;
  }
  
  public SensorButtonGroup interfacePiece() {
    return interfacePiece;
  }
  
  public void paint(Graphics2D g) {
    if (interfacePiece != nullInterface) {
      interfacePiece.paint(g);
    }
  }
  
  public void executeScript() {
    interactivePiece.execute();
  }
  
  public void setInterfacePiece(SensorButtonGroup interfacePiece) {
    this.interfacePiece = interfacePiece;
  }
  
  public JButton interactionButton() {
    JButton change;
    if (interactivePiece.actions.size() > 0) {
      change = new JButton(interactivePiece.toString() + " (change)");
    }
    else {
      change = new JButton("record interaction");
    }
    change.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent event) {
        if (!interactivePiece.isRecording) {
          interactivePiece.record();
          ((JButton)event.getSource()).setText("stop recording");
        } else {
          interactivePiece.stopRecording();
          ((JButton)event.getSource()).setText(interactivePiece.toString() + " (change)");
        }
      }
    });
    return change;
  }
  
  public JButton goButton() {
    JButton change;
    if (interactivePiece.actions.size() > 0) {
      change = new JButton("go");
    }
    else {
      change = new JButton("can't go");
    }
    change.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent event) {
        interactivePiece.execute();
      }
    });
    return change;
  }
}
