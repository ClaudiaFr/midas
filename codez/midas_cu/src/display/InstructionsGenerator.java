package display;

public class InstructionsGenerator {
  private static final String MIDAS_DIR = (SetUp.class.getProtectionDomain().getCodeSource().getLocation()).toString().replace("codez/midas_cu/bin/", "");
  private static final String MIDAS_FIGS = MIDAS_DIR + "other/instructions_images/";
  private static final String OUTLINE_FILE = MIDAS_DIR + "codez/midas_cu/outline.svg";
  private static final String MASK_FILE = MIDAS_DIR + "codez/midas_cu/mask.svg";
  
  private static int currentInstruction = 0;

  private InstructionsGenerator() {}
  
  public static String instructions(boolean hellaSlider, boolean noTails) {
    currentInstruction = 1;
    
    String ret = "";
    ret += head();
    ret += body(hellaSlider, noTails);
    return ret;
  }
  
  private static String head() {
    return "<html><head><title align='center'>Instructions to make shiny new sensors!</title></head>";
  }
  
  private static String body(boolean hellaSlider, boolean noTails) {
    String ret = "<body><h2>Instructions to make shiny new sensors</h2>";
    ret += "<table border=0>";
    ret += row(number() + "Load the copper sheet into the cutter", img200(MIDAS_FIGS+"loadcopper.gif"));

    if (noTails)
      ret += row(number() + "Download <a href='"+MASK_FILE+"'>this file</a>", svg(MASK_FILE));
    else
      ret += row(number() + "Download <a href='"+OUTLINE_FILE+"'>this file</a>", svg(OUTLINE_FILE));

    ret += row(number() + "Open SignCutPro",img(MIDAS_FIGS+"signcut-icon.png"));
    ret += row(number() + "Open your downloaded file", img(MIDAS_FIGS+"signcut-open.png"));
    ret += row(number() + "Use the mirror tool at the bottom to flip the image",img(MIDAS_FIGS+"mirror-button.png"));
    ret += row(number() + "Cut out the file", "");
    
    if (noTails) {
      ret += row(number() + "Remove the unnecessary background pieces from the sensors",img200(MIDAS_FIGS+"weedcopper.gif"));
      ret += row(number() + "Move your sensors on your object as you like", "");
      ret += row(number() + "Attach one wire to each sensor", "");
      ret += row(number() + "Return to the Midas interface and register the connections using each sensor's \"register\" button", img(MIDAS_FIGS+"register-sensors.tiff"));
    }
    else {
      ret += row(number() + "Remove the unnecessary background pieces from the sensors and tails, leaving the sensors undisturbed if possible",img200(MIDAS_FIGS+"weedcopper.gif"));
      ret += row(number() + "Using the transfer tape, put the newly-cut sensors onto your object", img200(MIDAS_FIGS+"transfertape.gif"));
      ret += row(number() + "Attach one rainbow wire to each copper tail that leads to a button. Note that you should begin with the brown wire attaching to the top tail.", img200(MIDAS_FIGS+"connectwires.png"));
      if(hellaSlider)
        ret += row(number() + "Attach the grey, white, and black wires to the copper tails leading to the slider","");
      ret += row(number() + "Load a vinyl sheet into the cutter", img200(MIDAS_FIGS+"loadvinyl.gif"));
      ret += row(number() + "Download <a href='"+MASK_FILE+"'>this file</a>", svg(MASK_FILE));
      ret += row(number() + "Open your downloaded file in SignCutPro", img(MIDAS_FIGS+"signcut-icon.png"));
      ret += row(number() + "Use the mirror tool at the bottom to flip the image",img(MIDAS_FIGS+"mirror-button.png"));
      ret += row(number() + "Cut out the file", "");
      ret += row(number() + "Remove the positive parts of the cutout, i.e. the shapes", img200(MIDAS_FIGS+"weedvinyl.gif"));
      ret += row(number() + "Transfer the mask onto your object so that your sensors show through the holes", img200(MIDAS_FIGS+"vinyloncopper.gif"));
    }
    ret += row(number() + "In the Midas interface, program some interactions!", "");
    
    ret += "</table>";

    ret += "</body></html>";
    return ret;
  }
  
  private static String svg(String svgLoc) {
    return "<embed src=\""+svgLoc+"\" type=\"image/svg+xml\" height='"+SetUp.CANVAS_Y +"' width='"+SetUp.CANVAS_X+"' />";
  }
  
  private static String img200(String imgLoc) {
    return "<img width=\"200\" src='" + imgLoc + "'>";
  }
  
  private static String img(String imgLoc) {
    return "<img src='" + imgLoc + "'>";
  }
  
  private static String row(String col1, String col2) {
    if (currentInstruction%2 == 1) {
      return "<tr><td width=50% style='vertical-align:top;padding:5px'>"+col1+"</td><td width=50% style='vertical-align:top;padding:5px'>"+col2+"</tr>\n";
    } else {
      return "<tr bgcolor='#E0E0E0'><td width=50% bgcolor='#E0E0E0' style='vertical-align:top;padding:5px'>"+col1+"</td><td width=50% bgcolor='#E0E0E0' style='vertical-align:top;padding:5px'>"+col2+"</tr>\n";
    }
  }
  
  private static String number() {
    return "<div style=\"color:grey;font-size:30\">"+ currentInstruction++ +"</div>";
  }
}
