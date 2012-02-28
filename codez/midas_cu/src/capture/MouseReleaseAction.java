package capture;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.event.InputEvent;

public class MouseReleaseAction extends RobotAction implements UIAction {
  
  Point p;
  int buttons;
  
  public MouseReleaseAction(Point p, int buttons) throws AWTException {
    super();
    this.p = p;
    this.buttons = RobotAction.cleanMouseButtons(buttons);
  }

  public void doAction() {
    robot.mouseMove(p.x, p.y);
    robot.mouseRelease(InputEvent.BUTTON1_MASK);
  }
  
  public String toString() {
    return "";
  }
}
