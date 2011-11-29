package serialtalk;

import java.util.List;

public class ArduinoSlider {
  private List<ArduinoSensor> sensors;
  
  public ArduinoSlider(List<ArduinoSensor> sensors) {
    this.sensors = sensors;
  }
  
  public boolean isPartOfSlider(ArduinoSensor sensor) {
    return sensors.contains(sensor);
  }
  
  public Direction ascOrDesc(ArduinoSensor previous, ArduinoSensor current) {
    if (sensors.indexOf(previous) > sensors.indexOf(current)) {
      return Direction.ASCENDING;
    }
    return Direction.DESCENDING;
  }
  
  public int howFar(List<ArduinoSensor> sensorsTouched) {
    return Math.abs(sensors.indexOf(sensorsTouched.get(0)) - sensors.indexOf(sensorsTouched.get(sensorsTouched.size())));
  }
  
  public String toString() {
    return "slider : " + sensors.toString();
  }
  
  public int hashCode() {
    String allSensors = "";
    for (ArduinoSensor as : sensors) {
      allSensors += as.which;
    }
    return Integer.parseInt(allSensors);
  }
  
  public boolean equals(Object o) {
    if (o.getClass() == ArduinoSlider.class && o.hashCode() == this.hashCode()) {
      return true;
    }
    return false;
  }
}
