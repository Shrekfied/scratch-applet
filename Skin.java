import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

class Skin {
  static boolean skinInitialized;
  
  static BufferedImage bubbleFrame;
  
  static BufferedImage goButton;
  
  static BufferedImage goButtonOver;
  
  static BufferedImage sliderKnob;
  
  static BufferedImage sliderSlot;
  
  static BufferedImage stopButton;
  
  static BufferedImage stopButtonOver;
  
  static BufferedImage talkPointerL;
  
  static BufferedImage talkPointerR;
  
  static BufferedImage thinkPointerL;
  
  static BufferedImage thinkPointerR;
  
  static BufferedImage watcherOuterFrame;
  
  static synchronized void readSkin(Component paramComponent) {
    if (skinInitialized)
      return; 
    bubbleFrame = readImage("talkBubbleFrame.gif", paramComponent);
    goButton = readImage("goButton.gif", paramComponent);
    goButtonOver = readImage("goButtonOver.gif", paramComponent);
    sliderKnob = readImage("sliderKnob.gif", paramComponent);
    sliderSlot = readImage("sliderSlot.gif", paramComponent);
    stopButton = readImage("stopButton.gif", paramComponent);
    stopButtonOver = readImage("stopButtonOver.gif", paramComponent);
    talkPointerL = readImage("talkBubbleTalkPointer.gif", paramComponent);
    talkPointerR = flipImage(talkPointerL);
    thinkPointerL = readImage("talkBubbleThinkPointer.gif", paramComponent);
    thinkPointerR = flipImage(thinkPointerL);
    watcherOuterFrame = readImage("watcherOuterFrame.png", paramComponent);
    skinInitialized = true;
  }
  
  static BufferedImage readImage(String paramString, Component paramComponent) {
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    Image image = toolkit.createImage(paramComponent.getClass().getResource("skin/" + paramString));
    MediaTracker mediaTracker = new MediaTracker(paramComponent);
    mediaTracker.addImage(image, 0);
    try {
      mediaTracker.waitForID(0);
    } catch (InterruptedException interruptedException) {}
    int i = image.getWidth(null);
    int j = image.getHeight(null);
    BufferedImage bufferedImage = new BufferedImage(i, j, 2);
    Graphics graphics = bufferedImage.getGraphics();
    graphics.drawImage(image, 0, 0, i, j, null);
    graphics.dispose();
    return bufferedImage;
  }
  
  static BufferedImage flipImage(BufferedImage paramBufferedImage) {
    int i = paramBufferedImage.getWidth(null);
    int j = paramBufferedImage.getHeight(null);
    BufferedImage bufferedImage = new BufferedImage(i, j, 2);
    Graphics graphics = bufferedImage.getGraphics();
    graphics.drawImage(paramBufferedImage, i, 0, 0, j, 0, 0, i, j, null);
    graphics.dispose();
    return bufferedImage;
  }
}
