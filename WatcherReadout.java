import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.TextLayout;

class WatcherReadout {
  int x;
  
  int y;
  
  int w = 40;
  
  int h = 14;
  
  Color color = new Color(100, 60, 20);
  
  String contents = "123";
  
  boolean isLarge = false;
  
  static final Color white = new Color(255, 255, 255);
  
  static final Font smallFont = new Font("Verdana", 1, 10);
  
  static final Font bigFont = new Font("Verdana", 1, 14);
  
  WatcherReadout(boolean paramBoolean) {
    beLarge(paramBoolean);
  }
  
  void beLarge(boolean paramBoolean) {
    if (this.isLarge == paramBoolean)
      return; 
    this.isLarge = paramBoolean;
    this.w = this.isLarge ? 50 : 40;
    this.h = this.isLarge ? 23 : 14;
  }
  
  void adjustWidth(Graphics paramGraphics) {
    Font font = this.isLarge ? bigFont : smallFont;
    this.w = Math.max(this.w, stringWidth(this.contents, font, paramGraphics) + 12);
  }
  
  void paint(Graphics paramGraphics) {
    paramGraphics.setColor(white);
    paramGraphics.fillRect(this.x + 2, this.y, this.w - 4, 1);
    paramGraphics.fillRect(this.x + 2, this.y + this.h - 1, this.w - 4, 1);
    paramGraphics.fillRect(this.x, this.y + 2, 1, this.h - 4);
    paramGraphics.fillRect(this.x + this.w - 1, this.y + 2, 1, this.h - 4);
    paramGraphics.fillRect(this.x + 1, this.y + 1, 1, 1);
    paramGraphics.fillRect(this.x + this.w - 2, this.y + 1, 1, 1);
    paramGraphics.fillRect(this.x + 1, this.y + this.h - 2, 1, 1);
    paramGraphics.fillRect(this.x + this.w - 2, this.y + this.h - 2, 1, 1);
    paramGraphics.setColor(darker(darker(this.color)));
    paramGraphics.fillRect(this.x + 2, this.y + 1, this.w - 4, 1);
    paramGraphics.fillRect(this.x + 1, this.y + 2, 1, this.h - 4);
    paramGraphics.setColor(darker(this.color));
    paramGraphics.fillRect(this.x + 2, this.y + 2, this.w - 4, 1);
    paramGraphics.fillRect(this.x + 2, this.y + this.h - 2, this.w - 4, 1);
    paramGraphics.fillRect(this.x + 2, this.y + 2, 1, this.h - 3);
    paramGraphics.fillRect(this.x + this.w - 2, this.y + 2, 1, this.h - 4);
    paramGraphics.setColor(this.color);
    paramGraphics.fillRect(this.x + 3, this.y + 3, this.w - 5, this.h - 5);
    Font font = this.isLarge ? bigFont : smallFont;
    byte b = this.isLarge ? 17 : 11;
    paramGraphics.setColor(white);
    paramGraphics.setFont(font);
    paramGraphics.drawString(this.contents, this.x + (this.w - stringWidth(this.contents, font, paramGraphics)) / 2 - 1, this.y + b);
  }
  
  int stringWidth(String paramString, Font paramFont, Graphics paramGraphics) {
    if (paramString.length() == 0)
      return 0; 
    TextLayout textLayout = new TextLayout(paramString, paramFont, ((Graphics2D)paramGraphics).getFontRenderContext());
    return (int)textLayout.getBounds().getBounds().getWidth();
  }
  
  Color darker(Color paramColor) {
    double d = 0.8333D;
    return new Color((int)(d * paramColor.getRed()), (int)(d * paramColor.getGreen()), (int)(d * paramColor.getBlue()));
  }
}
