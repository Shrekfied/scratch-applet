import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.TextLayout;
import java.util.Hashtable;

class Watcher implements Drawable {
  static final Color black = new Color(0, 0, 0);
  
  static final Font labelFont = new Font("Verdana", 1, 10);
  
  static final int NORMAL_MODE = 1;
  
  static final int SLIDER_MODE = 2;
  
  static final int LARGE_MODE = 3;
  
  PlayerCanvas canvas;
  
  StretchyBox box;
  
  WatcherReadout readout;
  
  StretchyBox slider;
  
  String label = "x";
  
  int mode = 1;
  
  double sliderMin = 0.0D;
  
  double sliderMax = 100.0D;
  
  boolean isDiscrete = false;
  
  Watcher(LContext paramLContext) {
    if (paramLContext != null)
      this.canvas = paramLContext.canvas; 
    this.box = new StretchyBox();
    this.box.setFrameImage(Skin.watcherOuterFrame);
    this.box.x = 50;
    this.box.y = 50;
    this.readout = new WatcherReadout(false);
    this.readout.color = new Color(74, 107, 214);
    this.slider = new StretchyBox();
    this.slider.setFrameImage(Skin.sliderSlot);
    this.slider.h = 5;
  }
  
  public boolean isShowing() {
    return true;
  }
  
  public Rectangle rect() {
    return new Rectangle(this.box.x, this.box.y, this.box.w, this.box.h);
  }
  
  public Rectangle fullRect() {
    return rect();
  }
  
  void inval() {
    this.canvas.inval(rect());
  }
  
  public void paint(Graphics paramGraphics) {
    int i = labelWidth(paramGraphics);
    this.readout.beLarge((this.mode == 3));
    this.readout.adjustWidth(paramGraphics);
    this.box.w = i + this.readout.w + 17;
    this.box.h = (this.mode == 1) ? 21 : 31;
    if (this.mode == 3) {
      this.readout.x = this.box.x;
      this.readout.y = this.box.y;
      this.readout.paint(paramGraphics);
      return;
    } 
    this.box.paint(paramGraphics);
    drawLabel(paramGraphics);
    this.readout.x = this.box.x + i + 12;
    this.readout.y = this.box.y + 3;
    this.readout.paint(paramGraphics);
    if (this.mode == 2)
      drawSlider(paramGraphics); 
  }
  
  public void paintBubble(Graphics paramGraphics) {}
  
  void drawLabel(Graphics paramGraphics) {
    paramGraphics.setColor(black);
    paramGraphics.setFont(labelFont);
    paramGraphics.drawString(this.label, this.box.x + 6, this.box.y + 14);
  }
  
  int labelWidth(Graphics paramGraphics) {
    if (this.label.length() == 0)
      return 0; 
    TextLayout textLayout = new TextLayout(this.label, labelFont, ((Graphics2D)paramGraphics).getFontRenderContext());
    return (int)textLayout.getBounds().getBounds().getWidth();
  }
  
  void drawSlider(Graphics paramGraphics) {
    this.box.x += 6;
    this.box.y += 21;
    this.box.w -= 12;
    this.slider.paint(paramGraphics);
    int i = (int)Math.round((this.slider.w - 8) * (getSliderValue() - this.sliderMin) / (this.sliderMax - this.sliderMin));
    i = Math.max(0, Math.min(i, this.slider.w - 8));
    paramGraphics.drawImage(Skin.sliderKnob, this.slider.x + i - 1, this.slider.y - 3, null);
  }
  
  boolean inSlider(int paramInt1, int paramInt2) {
    if (this.mode != 2)
      return false; 
    if (paramInt2 < this.slider.y - 1 || paramInt2 > this.slider.y + this.slider.h + 4)
      return false; 
    if (paramInt1 < this.slider.x - 4 || paramInt1 > this.slider.x + this.slider.w + 5)
      return false; 
    return true;
  }
  
  public void dragTo(int paramInt1, int paramInt2) {
    double d = (paramInt1 - this.box.x - 10);
    setSliderValue(d * (this.sliderMax - this.sliderMin) / (this.slider.w - 8) + this.sliderMin);
  }
  
  void click(int paramInt1, int paramInt2) {
    double d = getSliderValue();
    int i = this.slider.x + (int)Math.round((this.slider.w - 8) * (d - this.sliderMin) / (this.sliderMax - this.sliderMin)) + 5;
    boolean bool = (paramInt1 < i) ? true : true;
    if (this.isDiscrete) {
      setSliderValue(d + bool);
    } else {
      setSliderValue(d + bool * (this.sliderMax - this.sliderMin) / 100.0D);
    } 
  }
  
  double getSliderValue() {
    String str = Logo.prs(getObjProp(this, "param"));
    Hashtable hashtable = getVarsTable();
    if (str == null || hashtable == null)
      return (this.sliderMin + this.sliderMax) / 2.0D; 
    Object object = hashtable.get(Logo.aSymbol(str, this.canvas.lc));
    return (object instanceof Number) ? ((Number)object).doubleValue() : ((this.sliderMin + this.sliderMax) / 2.0D);
  }
  
  void setSliderValue(double paramDouble) {
    double d = this.isDiscrete ? Math.round(paramDouble) : paramDouble;
    d = Math.min(this.sliderMax, Math.max(this.sliderMin, d));
    String str = Logo.prs(getObjProp(this, "param"));
    Hashtable hashtable = getVarsTable();
    if (str == null || hashtable == null)
      return; 
    hashtable.put(Logo.aSymbol(str, this.canvas.lc), new Double(d));
    inval();
  }
  
  Hashtable getVarsTable() {
    String str1 = Logo.prs(getObjProp(this, "op"));
    String str2 = Logo.prs(getObjProp(this, "param"));
    Object object1 = getObjProp(this, "target");
    if (object1 == null || !str1.equals("getVar:"))
      return null; 
    Object object2 = getObjProp(object1, "vars");
    if (object2 == null)
      return null; 
    return (Hashtable)this.canvas.lc.props.get(object2);
  }
  
  Object getObjProp(Object paramObject, String paramString) {
    Hashtable hashtable = (Hashtable)this.canvas.lc.props.get(paramObject);
    if (hashtable == null)
      return null; 
    return hashtable.get(Logo.aSymbol(paramString, this.canvas.lc));
  }
}
