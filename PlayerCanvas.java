import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.Hashtable;
import java.util.Vector;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JPanel;

class PlayerCanvas extends JPanel implements KeyListener, MouseListener, MouseMotionListener {
  static final String versionString = "v24";
  
  static final int width = 482;
  
  static final int height = 387;
  
  static final int topBarHeight = 26;
  
  static final int goButtonX = 418;
  
  static final int stopButtonX = 451;
  
  static final int buttonY = 4;
  
  static final Color BLACK = new Color(0, 0, 0);
  
  static final Color WHITE = new Color(255, 255, 255);
  
  static final Font font = new Font("SansSerif", 1, 24);
  
  static final int soundInputBufSize = 50000;
  
  static TargetDataLine soundInputLine;
  
  byte[] soundInputBuf = new byte[50000];
  
  int soundLevel = 0;
  
  boolean overGoButton = false;
  
  boolean overStopButton = false;
  
  String message = "";
  
  boolean isLoading = true;
  
  double loadingFraction = 0.2D;
  
  LContext lc;
  
  Sprite stage;
  
  Object[] sprites = new Object[0];
  
  BufferedImage offscreen;
  
  BufferedImage penTrails;
  
  Rectangle invalrect = new Rectangle();
  
  int mouseX;
  
  int mouseY;
  
  boolean mouseIsDown = false;
  
  int mouseDownX;
  
  int mouseDownY;
  
  Drawable mouseDragTarget;
  
  int mouseDragXOffset;
  
  int mouseDragYOffset;
  
  boolean reportClickOnMouseUp;
  
  Vector mouseclicks = new Vector();
  
  boolean[] keydown = new boolean[256];
  
  Vector keystrokes = new Vector();
  
  PlayerCanvas() {
    setLayout((LayoutManager)null);
    addKeyListener(this);
    addMouseListener(this);
    addMouseMotionListener(this);
  }
  
  public void addNotify() {
    super.addNotify();
    this.offscreen = (BufferedImage)createImage(482, 387);
    this.offscreen.getRaster();
    Graphics graphics = this.offscreen.getGraphics();
    graphics.setColor(WHITE);
    graphics.fillRect(0, 0, 482, 387);
    graphics.dispose();
    repaint();
  }
  
  public Dimension getMinimumSize() {
    return new Dimension(482, 387);
  }
  
  public Dimension getPreferredSize() {
    return new Dimension(482, 387);
  }
  
  public synchronized void paintComponent(Graphics paramGraphics) {
    paramGraphics.drawImage(this.offscreen, 0, 0, 482, 387, this);
  }
  
  void clearall(LContext paramLContext) {
    this.stage = null;
    this.sprites = new Object[0];
    this.penTrails = null;
    SoundPlayer.stopSoundsForApplet(paramLContext);
    this.soundLevel = 0;
    paramLContext.props = new Hashtable();
    Runtime.getRuntime().gc();
    clearkeys();
    this.mouseclicks = new Vector();
    this.mouseIsDown = false;
    this.mouseDragTarget = null;
  }
  
  void setMessage(String paramString) {
    this.message = paramString;
    redraw_all();
  }
  
  synchronized void inval(Rectangle paramRectangle) {
    if (this.invalrect.isEmpty()) {
      this.invalrect = new Rectangle(paramRectangle);
    } else {
      this.invalrect = this.invalrect.union(paramRectangle);
    } 
  }
  
  void redraw_all() {
    redraw(new Rectangle(0, 0, 482, 387), false);
  }
  
  void redraw_invalid() {
    redraw(this.invalrect, false);
  }
  
  synchronized void redraw(Rectangle paramRectangle, boolean paramBoolean) {
    Graphics graphics = this.offscreen.getGraphics();
    graphics.setClip(paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height);
    graphics.setColor(WHITE);
    graphics.fillRect(0, 0, 482, 387);
    if (this.isLoading) {
      drawProgressBar(graphics);
    } else {
      if (this.stage != null) {
        this.stage.setStageOffset();
        this.stage.paint(graphics);
      } 
      if (this.penTrails != null)
        graphics.drawImage(this.penTrails, 0, 0, 482, 387, null); 
      int i;
      for (i = this.sprites.length - 1; i >= 0; i--) {
        Drawable drawable = (Drawable)this.sprites[i];
        if (drawable.isShowing() && paramRectangle.intersects(drawable.fullRect()))
          drawable.paint(graphics); 
      } 
      for (i = this.sprites.length - 1; i >= 0; i--) {
        Drawable drawable = (Drawable)this.sprites[i];
        if (drawable.isShowing() && paramRectangle.intersects(drawable.fullRect()))
          drawable.paintBubble(graphics); 
      } 
    } 
    drawBorder(graphics);
    if (paramBoolean) {
      graphics.setColor(new Color(200, 0, 0));
      graphics.fillRect(paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height);
    } 
    graphics.dispose();
    repaint(paramRectangle);
    this.invalrect = new Rectangle();
  }
  
  void drawBorder(Graphics paramGraphics) {
    paramGraphics.setColor(new Color(130, 130, 130));
    paramGraphics.fillRect(0, 0, 482, 25);
    paramGraphics.setColor(BLACK);
    paramGraphics.fillRect(0, 0, 482, 1);
    paramGraphics.fillRect(0, 0, 1, 387);
    paramGraphics.fillRect(0, 386, 482, 1);
    paramGraphics.fillRect(481, 0, 1, 387);
    paramGraphics.fillRect(0, 25, 482, 1);
    paramGraphics.drawImage(this.overGoButton ? Skin.goButtonOver : Skin.goButton, 418, 4, null);
    paramGraphics.drawImage(this.overStopButton ? Skin.stopButtonOver : Skin.stopButton, 451, 4, null);
    paramGraphics.setColor(WHITE);
    paramGraphics.setFont(new Font("Verdana", 0, 8));
    paramGraphics.drawString("v24", 5, 11);
    paramGraphics.setFont(new Font("Verdana", 1, 13));
    paramGraphics.setColor(new Color(250, 250, 0));
    paramGraphics.drawString(this.message, 70, 17);
  }
  
  void drawProgressBar(Graphics paramGraphics) {
    paramGraphics.setColor(BLACK);
    paramGraphics.setFont(font);
    paramGraphics.drawString("Loading...", 188, 173);
    byte b = 91;
    char c = 'Á';
    paramGraphics.fillRect(b, c, 300, 1);
    paramGraphics.fillRect(b, c, 1, 29);
    paramGraphics.fillRect(b, c + 28, 300, 1);
    paramGraphics.fillRect(b + 299, c, 1, 29);
    paramGraphics.setColor(new Color(10, 10, 200));
    paramGraphics.fillRect(b + 2, c + 2, (int)(296.0D * Math.min(this.loadingFraction, 1.0D)), 25);
    drawBorder(paramGraphics);
  }
  
  BufferedImage drawAreaWithoutSprite(Rectangle paramRectangle, Sprite paramSprite) {
    BufferedImage bufferedImage = new BufferedImage(paramRectangle.width, paramRectangle.height, 2);
    Graphics graphics = bufferedImage.getGraphics();
    graphics.setColor(WHITE);
    graphics.fillRect(0, 0, paramRectangle.width, paramRectangle.height);
    graphics = graphics.create(-paramRectangle.x, -paramRectangle.y, paramRectangle.width, paramRectangle.height);
    graphics.setClip(paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height);
    if (this.stage != null) {
      this.stage.setStageOffset();
      this.stage.paint(graphics);
    } 
    if (this.penTrails != null)
      graphics.drawImage(this.penTrails, 0, 0, 482, 387, null); 
    for (int i = this.sprites.length - 1; i >= 0; i--) {
      Drawable drawable = (Drawable)this.sprites[i];
      if (drawable != paramSprite && drawable.isShowing() && paramRectangle.intersects(drawable.rect()))
        drawable.paint(graphics); 
    } 
    graphics.dispose();
    return bufferedImage;
  }
  
  void drawRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    Graphics graphics = this.offscreen.getGraphics();
    graphics.setColor(BLACK);
    graphics.fillRect(paramInt1, paramInt2, paramInt3, paramInt4);
    graphics.dispose();
    repaint();
  }
  
  void startLoading() {
    this.isLoading = true;
    this.loadingFraction = 0.0D;
    redraw_all();
  }
  
  void stopLoading() {
    this.loadingFraction = 1.0D;
    redraw_all();
    this.loadingFraction = 0.0D;
    this.isLoading = false;
  }
  
  void loadingProgress(double paramDouble) {
    this.loadingFraction = paramDouble;
    redraw_all();
  }
  
  void updatePenTrails() {
    for (int i = this.sprites.length - 1; i >= 0; i--) {
      if (this.sprites[i] instanceof Sprite) {
        Sprite sprite = (Sprite)this.sprites[i];
        if (sprite.penDown)
          updatePenTrailForSprite(sprite); 
      } 
    } 
  }
  
  void updatePenTrailForSprite(Sprite paramSprite) {
    if (this.penTrails == null)
      createPenTrails(); 
    int i = 241 + (int)paramSprite.x;
    int j = 206 - (int)paramSprite.y;
    if (paramSprite.lastPenX == -1000000.0D) {
      paramSprite.lastPenX = i;
      paramSprite.lastPenY = j;
    } else if (paramSprite.lastPenX == i && paramSprite.lastPenY == j) {
      return;
    } 
    Graphics2D graphics2D = this.penTrails.createGraphics();
    graphics2D.setColor(paramSprite.penColor);
    graphics2D.setStroke(new BasicStroke(paramSprite.penSize, 1, 1));
    graphics2D.drawLine(paramSprite.lastPenX, paramSprite.lastPenY, i, j);
    graphics2D.dispose();
    Rectangle rectangle = new Rectangle(paramSprite.lastPenX, paramSprite.lastPenY, 0, 0);
    rectangle.add(i, j);
    rectangle.grow(paramSprite.penSize, paramSprite.penSize);
    inval(rectangle);
    paramSprite.lastPenX = i;
    paramSprite.lastPenY = j;
  }
  
  void stampCostume(Sprite paramSprite) {
    if (this.penTrails == null)
      createPenTrails(); 
    Graphics2D graphics2D = this.penTrails.createGraphics();
    if (paramSprite.filterChanged)
      paramSprite.applyFilters(); 
    graphics2D.drawImage(paramSprite.outImage(), paramSprite.screenX(), paramSprite.screenY(), (ImageObserver)null);
    graphics2D.dispose();
    paramSprite.inval();
  }
  
  void createPenTrails() {
    this.penTrails = new BufferedImage(482, 387, 2);
    this.penTrails.getRaster();
  }
  
  void clearPenTrails() {
    if (this.penTrails != null)
      this.penTrails.flush(); 
    this.penTrails = null;
    inval(new Rectangle(0, 0, 482, 387));
  }
  
  public void mouseEntered(MouseEvent paramMouseEvent) {
    requestFocus();
    mouseDragOrMove(paramMouseEvent);
  }
  
  public void mouseExited(MouseEvent paramMouseEvent) {
    updateMouseXY(paramMouseEvent);
  }
  
  public void mousePressed(MouseEvent paramMouseEvent) {
    mouseDowm(paramMouseEvent);
  }
  
  public void mouseReleased(MouseEvent paramMouseEvent) {
    mouseUp(paramMouseEvent);
  }
  
  public void mouseClicked(MouseEvent paramMouseEvent) {
    updateMouseXY(paramMouseEvent);
  }
  
  public void mouseDragged(MouseEvent paramMouseEvent) {
    mouseDragOrMove(paramMouseEvent);
  }
  
  public void mouseMoved(MouseEvent paramMouseEvent) {
    mouseDragOrMove(paramMouseEvent);
  }
  
  void mouseDowm(MouseEvent paramMouseEvent) {
    updateMouseXY(paramMouseEvent);
    requestFocus();
    if (inGoButton(paramMouseEvent)) {
      doStopButton();
      LogoCommandRunner.startLogoThread("greenflag", this.lc);
      return;
    } 
    if (inStopButton(paramMouseEvent)) {
      doStopButton();
      LogoCommandRunner.startLogoThread("interact", this.lc);
      return;
    } 
    this.mouseIsDown = true;
    this.mouseDownX = paramMouseEvent.getX();
    this.mouseDownY = paramMouseEvent.getY();
    this.mouseDragTarget = findDragTarget(paramMouseEvent.getX(), paramMouseEvent.getY());
    this.mouseDragXOffset = this.mouseDragYOffset = 0;
    this.reportClickOnMouseUp = true;
    if (this.mouseDragTarget instanceof Sprite) {
      Sprite sprite = (Sprite)this.mouseDragTarget;
      if (sprite.isDraggable) {
        this.mouseDragXOffset = sprite.screenX() - paramMouseEvent.getX();
        this.mouseDragYOffset = sprite.screenY() - paramMouseEvent.getY();
        moveSpriteToFront(sprite);
      } else {
        this.mouseDragTarget = null;
      } 
    } 
    if (this.mouseDragTarget == null)
      reportClick(); 
  }
  
  void mouseDragOrMove(MouseEvent paramMouseEvent) {
    updateMouseXY(paramMouseEvent);
    if (paramMouseEvent.getX() != this.mouseDownX || paramMouseEvent.getY() != this.mouseDownY)
      this.reportClickOnMouseUp = false; 
    if (this.mouseDragTarget != null) {
      this.mouseDragTarget.dragTo(paramMouseEvent.getX() + this.mouseDragXOffset, paramMouseEvent.getY() + this.mouseDragYOffset);
    } else {
      boolean bool1 = this.overGoButton;
      boolean bool2 = this.overStopButton;
      this.overGoButton = inGoButton(paramMouseEvent);
      this.overStopButton = inStopButton(paramMouseEvent);
      if (bool1 != this.overGoButton || bool2 != this.overStopButton) {
        inval(new Rectangle(0, 0, 482, 26));
        redraw_invalid();
      } 
    } 
  }
  
  void mouseUp(MouseEvent paramMouseEvent) {
    updateMouseXY(paramMouseEvent);
    if (this.reportClickOnMouseUp)
      if (this.mouseDragTarget instanceof Watcher) {
        ((Watcher)this.mouseDragTarget).click(paramMouseEvent.getX(), paramMouseEvent.getY());
      } else {
        reportClick();
      }  
    this.mouseDragTarget = null;
    this.mouseIsDown = false;
  }
  
  void reportClick() {
    Object[] arrayOfObject = new Object[2];
    arrayOfObject[0] = new Double((this.mouseDownX - 241));
    arrayOfObject[1] = new Double((206 - this.mouseDownY));
    this.mouseclicks.addElement(arrayOfObject);
    this.reportClickOnMouseUp = false;
  }
  
  void updateMouseXY(MouseEvent paramMouseEvent) {
    this.mouseX = paramMouseEvent.getX() - 241;
    this.mouseY = 206 - paramMouseEvent.getY();
  }
  
  boolean inGoButton(MouseEvent paramMouseEvent) {
    if (paramMouseEvent.getY() >= 26)
      return false; 
    int i = paramMouseEvent.getX();
    return (i >= 418 && i <= 418 + Skin.goButton.getWidth((ImageObserver)null));
  }
  
  boolean inStopButton(MouseEvent paramMouseEvent) {
    if (paramMouseEvent.getY() >= 26)
      return false; 
    int i = paramMouseEvent.getX();
    return (i >= 451 && i <= 451 + Skin.stopButton.getWidth((ImageObserver)null));
  }
  
  void doStopButton() {
    SoundPlayer.stopSoundsForApplet(this.lc);
    LogoCommandRunner.stopLogoThread(this.lc);
    (new LogoCommandRunner("stopAll", this.lc, true)).run();
    clearkeys();
    this.mouseclicks = new Vector();
    this.mouseIsDown = false;
    this.mouseDragTarget = null;
    redraw_all();
  }
  
  Drawable findDragTarget(int paramInt1, int paramInt2) {
    for (byte b = 0; b < this.sprites.length; b++) {
      if (this.sprites[b] instanceof Watcher) {
        Watcher watcher = (Watcher)this.sprites[b];
        if (watcher.inSlider(paramInt1, paramInt2))
          return watcher; 
      } 
      if (this.sprites[b] instanceof Sprite) {
        Sprite sprite = (Sprite)this.sprites[b];
        if (sprite.containsPoint(paramInt1, paramInt2))
          return sprite; 
      } 
    } 
    return null;
  }
  
  void moveSpriteToFront(Sprite paramSprite) {
    byte b1 = -1;
    for (byte b = 0; b < this.sprites.length; b++) {
      if (this.sprites[b] == paramSprite)
        b1 = b; 
    } 
    if (b1 < 0)
      return; 
    Object object = this.sprites[b1];
    for (byte b2 = b1; b2 > 0; ) {
      this.sprites[b2] = this.sprites[b2 - 1];
      b2--;
    } 
    this.sprites[0] = object;
    paramSprite.inval();
  }
  
  public void keyPressed(KeyEvent paramKeyEvent) {
    int i = keyCodeFor(paramKeyEvent);
    this.keydown[i] = true;
    if (i == 10 || (i >= 28 && i <= 31))
      this.keystrokes.addElement(new Double(i)); 
  }
  
  public void keyReleased(KeyEvent paramKeyEvent) {
    int i = keyCodeFor(paramKeyEvent);
    this.keydown[i] = false;
  }
  
  public void keyTyped(KeyEvent paramKeyEvent) {
    int i = paramKeyEvent.getKeyChar();
    if (i >= 65 && i <= 90)
      i = i + 32; 
    this.keystrokes.addElement(new Double(i));
  }
  
  int keyCodeFor(KeyEvent paramKeyEvent) {
    int i = paramKeyEvent.getKeyCode();
    if (i == 10)
      return 10; 
    if (i == 37)
      return 28; 
    if (i == 38)
      return 30; 
    if (i == 39)
      return 29; 
    if (i == 40)
      return 31; 
    if (i >= 65 && i <= 90)
      return i + 32; 
    return Math.min(i, 255);
  }
  
  void clearkeys() {
    for (byte b = 0; b < this.keydown.length; ) {
      this.keydown[b] = false;
      b++;
    } 
    this.keystrokes = new Vector();
  }
  
  int soundLevel() {
    if (soundInputLine == null)
      return 0; 
    int i = soundInputLine.available();
    if (i == 0)
      return this.soundLevel; 
    i = soundInputLine.read(this.soundInputBuf, 0, i);
    int j = 0;
    for (byte b = 0; b < i / 2; b++) {
      int k = (this.soundInputBuf[2 * b] << 8) + this.soundInputBuf[2 * b + 1];
      if (k >= 32768)
        k = 65536 - k; 
      if (k > j)
        j = k; 
    } 
    this.soundLevel = j / 327;
    return this.soundLevel;
  }
  
  void openSoundInput() {
    if (soundInputLine != null)
      soundInputLine.close(); 
    AudioFormat audioFormat = new AudioFormat(44100.0F, 16, 1, true, true);
    DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
    try {
      soundInputLine = (TargetDataLine)AudioSystem.getLine(info);
      soundInputLine.open(audioFormat, 50000);
      soundInputLine.start();
    } catch (LineUnavailableException lineUnavailableException) {
      soundInputLine = null;
    } 
  }
}
