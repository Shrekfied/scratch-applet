import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.util.Vector;

class Bubble extends StretchyBox {
  boolean pointLeft = false;
  
  BufferedImage leftPointer;
  
  BufferedImage rightPointer;
  
  int fontSize = 13;
  
  Font font = new Font("Verdana", 0, this.fontSize);
  
  FontRenderContext renderContext;
  
  int wrapWidth = 145;
  
  String contents;
  
  String[] lines;
  
  Bubble() {
    this.renderContext = Skin.bubbleFrame.createGraphics().getFontRenderContext();
    setFrameImage(Skin.bubbleFrame);
    beThinkBubble(false);
  }
  
  void beThinkBubble(boolean paramBoolean) {
    if (paramBoolean) {
      this.leftPointer = Skin.thinkPointerL;
      this.rightPointer = Skin.thinkPointerR;
    } else {
      this.leftPointer = Skin.talkPointerL;
      this.rightPointer = Skin.talkPointerR;
    } 
  }
  
  void setContents(String paramString) {
    this.contents = paramString;
    Vector vector = new Vector();
    int i = 0;
    while (i < paramString.length()) {
      int j = findLineEnd(paramString, i);
      vector.addElement(paramString.substring(i, j));
      i = j;
    } 
    this.lines = new String[vector.size()];
    this.w = 50;
    for (byte b = 0; b < this.lines.length; b++) {
      this.lines[b] = vector.get(b);
      this.w = Math.max(this.w, widthOf(this.lines[b]) + 20);
    } 
    this.h = this.lines.length * (this.fontSize + 2) + 18;
  }
  
  int findLineEnd(String paramString, int paramInt) {
    int i = paramInt + 1;
    for (; i < paramString.length() && widthOf(paramString.substring(paramInt, i + 1)) < this.wrapWidth; i++);
    if (i == paramString.length())
      return i; 
    if (widthOf(paramString.substring(paramInt, i + 1)) < this.wrapWidth)
      return i + 1; 
    int j = i + 1;
    while (i > paramInt + 1) {
      if (i < paramString.length() && paramString.charAt(i) == ' ')
        return i + 1; 
      i--;
    } 
    return j;
  }
  
  int widthOf(String paramString) {
    if (paramString.length() == 0)
      return 0; 
    return (int)(new TextLayout(paramString, this.font, this.renderContext)).getBounds().getWidth();
  }
  
  public Rectangle rect() {
    return new Rectangle(this.x, this.y, this.w, this.h + this.leftPointer.getHeight(null));
  }
  
  public void paint(Graphics paramGraphics) {
    super.paint(paramGraphics);
    if (this.pointLeft) {
      paramGraphics.drawImage(this.leftPointer, this.x, this.y + this.h - 3, null);
    } else {
      paramGraphics.drawImage(this.rightPointer, this.x + this.w - this.rightPointer.getWidth(null), this.y + this.h - 3, null);
    } 
    paramGraphics.setColor(new Color(0, 0, 0));
    paramGraphics.setFont(this.font);
    int i = this.y + this.fontSize + 8;
    for (byte b = 0; b < this.lines.length; b++) {
      paramGraphics.drawString(this.lines[b], this.x + 11, i);
      i += this.fontSize + 2;
    } 
  }
}
