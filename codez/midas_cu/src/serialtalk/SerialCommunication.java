package serialtalk;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.awt.AWTException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JLabel;

import actions.SocketTalkAction;
import bridge.ArduinoToDisplayBridge;

/**
 * This code was inspired by the Internet.
 */

public class SerialCommunication implements SerialPortEventListener {
  SerialPort serialPort;

  private static final String PORT_NAMES[] = { 
      "/dev/tty.usbmodemfa131", // Mac, Arduino Uno
      "/dev/tty.usbmodemfa121", // Mac, Arduino Uno
      "/dev/tty.usbmodem12341", // Mac, Teensy
      "/dev/ttyACM0", // Linux, Arduino Uno
      "COM3", // Windows
  };
  /** Buffered input stream from the port */
  private InputStream input;
  /** The output stream to the port */
  private OutputStream output;
  /** Milliseconds to block while waiting for port open */
  private static final int TIME_OUT = 2000;
  /** Default bits per second for COM port. */
  private static final int DATA_RATE = 9600;

  public ArduinoDispatcher dispatcher;
  public List<ArduinoToDisplayBridge> bridgeObjects;

  boolean paused = false;
  private String currentSerialInfo = new String();

  private Pattern matchOneLine = Pattern.compile(".*x");
  private Pattern matchOneArduinoMessage = Pattern
      .compile("K:(\\d{1,2}) (U|D)");
  private Pattern matchOneArduinoSliderMessage = Pattern
      .compile("S:(\\d{1,3}) (U|D)");
  
  private static final String ARDUINO_TOUCH = "D";
  @SuppressWarnings("unused")
  private static final String ARDUINO_RELEASE = "U";

  public void initialize(boolean test, ArduinoDispatcher dispatcher) throws AWTException {
    ArduinoSetup.initialize(test);

    CommPortIdentifier portId = null;
    // the following line is useful for computers with AMD processors. it's
    // stupid.
    // System.setProperty("gnu.io.rxtx.SerialPorts", "/dev/ttyACM0");

    @SuppressWarnings("rawtypes")
    Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
    
    setUpDispatcher(dispatcher);

    // iterate through, looking for the port
    while (portEnum.hasMoreElements()) {
      CommPortIdentifier currPortId = (CommPortIdentifier) portEnum
          .nextElement();
      for (String portName : PORT_NAMES) {
        if (currPortId.getName().equals(portName)) {
          portId = currPortId;
          break;
        }
      }
    }

    if (portId == null) {
      System.out.println("Could not find COM port.");
      if (test) {
        // we want to make a new window that we can use to inject ArduinoEvents
        ArduinoTestWindow window = new ArduinoTestWindow();
        window.setDispatcher(this.dispatcher);
      }
      return;
    }

    try {
      // open serial port, and use class name for the appName.
      serialPort = (SerialPort) portId
          .open(this.getClass().getName(), TIME_OUT);

      // set port parameters
      serialPort.setSerialPortParams(DATA_RATE, SerialPort.DATABITS_8,
          SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

      // open the streams
      input = serialPort.getInputStream();
      output = serialPort.getOutputStream();

      // add event listeners
      serialPort.addEventListener(this);
      serialPort.notifyOnDataAvailable(true);

    } catch (Exception e) {
      System.err.println(e.toString());
    }
  }

  public void setUpDispatcher(ArduinoDispatcher dispatcher) throws AWTException {
    if (dispatcher == null) {
      dispatcher = new ArduinoDispatcher();
    }
    this.dispatcher = dispatcher;
    ArduinoToDisplayBridge.setDispatcher(dispatcher);
    SocketTalkAction.setDispatcher(dispatcher);
    bridgeObjects = dispatcher.bridgeObjects;
  }

  /**
   * This should be called when you stop using the port. This will prevent port
   * locking on platforms like Linux.
   */
  public synchronized void close() {
    if (serialPort != null) {
      serialPort.removeEventListener();
      serialPort.close();
    }
  }

  public synchronized void sendMessage(String message) {
    try {
      output.write(message.getBytes());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public synchronized boolean isPaused() {
    return paused;
  }

  public synchronized void togglePaused() {
    paused = !paused;
  }

  /**
   * Handle an event on the serial port. Read the data and print it.
   */
  public synchronized void serialEvent(SerialPortEvent oEvent) {
    if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
      byte touched[] = new byte[6];
      try {
        input.read(touched, 0, 6);

        // Displayed results are codepage dependent
      } catch (Exception e) {
        System.err.println(e.toString());
        return;
      }

      currentSerialInfo = (currentSerialInfo + new String(touched));
      while(currentSerialInfo.startsWith("9")) {
        currentSerialInfo = currentSerialInfo.substring(1);
        currentSerialInfo = currentSerialInfo.trim();
      }
      Matcher oneMessage;

      while (((oneMessage = matchOneLine.matcher(currentSerialInfo))
          .lookingAt())) {
        
        Matcher firstMessage, secondMessage;

        String singleMessage = currentSerialInfo.substring(0, oneMessage.end() - "x".length()); // strip the x
        currentSerialInfo = currentSerialInfo.substring(oneMessage.end());
        
        if((firstMessage = matchOneArduinoMessage.matcher(singleMessage)).lookingAt()
            && ArduinoSetup.griddedSensors.contains(Integer.parseInt(firstMessage.group(1)))) {
          // we have a sensor that's part of a 2D pad, so we need to wait for the other direction
          if ((secondMessage = matchOneLine.matcher(currentSerialInfo)).lookingAt() && 
              ArduinoSetup.griddedSensors.contains(Integer.parseInt(secondMessage.group(1)))) {
            // we have two 2D sensors, yay!
            TouchDirection direction;
            if (firstMessage.group(2).equals(ARDUINO_TOUCH)
                && firstMessage.group(2).equals(firstMessage.group(4))) {
              direction = TouchDirection.TOUCH;
            } else {
              direction = TouchDirection.RELEASE;
            }
            ArduinoEvent currentEvent = new ArduinoEvent(
                ArduinoSetup.gridSensors[Integer.parseInt(firstMessage.group(1))][Integer
                    .parseInt(firstMessage.group(3))], direction);
            handleCompleteEvent(currentEvent);
          } else {
            // if we only have one message, we can't do jack.
            return;
          }
        } else if ((firstMessage = matchOneArduinoSliderMessage.matcher(singleMessage))
            .lookingAt()) { // we have a slider thing to look at
          TouchDirection direction;
          if (firstMessage.group(2).equals(ARDUINO_TOUCH)) {
            direction = TouchDirection.TOUCH;
          } else {
            direction = TouchDirection.RELEASE;
          }
          ArduinoEvent currentEvent = new ArduinoEvent(
              Integer.parseInt(firstMessage.group(1)), direction);
          handleCompleteEvent(currentEvent);
        } else if ((firstMessage = matchOneArduinoMessage.matcher(singleMessage))
            .lookingAt()) { // we are looking at one touch information at a time
          System.out.println("woo, party, we are wrong");
          TouchDirection direction;
          if (firstMessage.group(2).equals(ARDUINO_TOUCH)) {
            direction = TouchDirection.TOUCH;
          } else {
            direction = TouchDirection.RELEASE;
          }
          ArduinoEvent currentEvent = new ArduinoEvent(
              ArduinoSetup.sensors[Integer.parseInt(firstMessage.group(1))],
              direction);
          handleCompleteEvent(currentEvent);
        }
      }
    }
  }

  public synchronized void handleCompleteEvent(ArduinoEvent e) {
    if (paused) {
      dispatcher.clearRecentEvents();
      return;
    }
    dispatcher.handleEvent(e);
  }

  public JLabel whatISee() {
    return dispatcher.whatISee;
  }
}
