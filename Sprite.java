import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

class Sprite implements Drawable {
  static final int originX = 241;
  
  static final int originY = 206;
  
  PlayerCanvas canvas;
  
  BufferedImage costume;
  
  BufferedImage rotatedCostume;
  
  BufferedImage filteredCostume;
  
  BufferedImage tempImage;
  
  double x;
  
  double y;
  
  boolean isShowing = true;
  
  boolean isDraggable = false;
  
  double alpha = 1.0D;
  
  double scale = 1.0D;
  
  double rotationDegrees = 90.0D;
  
  int rotationstyle;
  
  int rotationX;
  
  int rotationY;
  
  int offsetX;
  
  int offsetY;
  
  Bubble bubble;
  
  boolean penDown;
  
  int lastPenX;
  
  int lastPenY;
  
  Color penColor = new Color(0, 0, 255);
  
  int penSize = 1;
  
  double penHue;
  
  double penShade;
  
  boolean filterChanged = false;
  
  double color;
  
  double brightness;
  
  double fisheye;
  
  double whirl;
  
  double mosaic;
  
  double pixelate;
  
  ImageFilter imageFilter = new ImageFilter();
  
  Sprite(LContext paramLContext) {
    setPenColor(this.penColor);
    if (paramLContext != null)
      this.canvas = paramLContext.canvas; 
  }
  
  int screenX() {
    return 241 + (int)(this.x - this.offsetX);
  }
  
  int screenY() {
    return 206 + (int)(-this.y - this.offsetY);
  }
  
  void setscreenX(double paramDouble) {
    this.x = paramDouble + this.offsetX - 241.0D;
  }
  
  void setscreenY(double paramDouble) {
    this.y = -(paramDouble + this.offsetY - 206.0D);
  }
  
  void setStageOffset() {
    this.x = this.y = 0.0D;
    this.offsetX = this.costume.getWidth((ImageObserver)null) / 2;
    this.offsetY = this.costume.getHeight((ImageObserver)null) / 2;
  }
  
  public void dragTo(int paramInt1, int paramInt2) {
    inval();
    setscreenX(paramInt1);
    setscreenY(paramInt2);
    inval();
  }
  
  boolean containsPoint(int paramInt1, int paramInt2) {
    BufferedImage bufferedImage = outImage();
    int i = screenX();
    int j = screenY();
    int k = bufferedImage.getWidth((ImageObserver)null);
    int m = bufferedImage.getHeight((ImageObserver)null);
    if (paramInt1 < i || paramInt1 >= i + k || paramInt2 < j || paramInt2 >= j + m)
      return false; 
    int n = bufferedImage.getRGB(paramInt1 - i, paramInt2 - j);
    return ((n & 0xFF000000) != 0);
  }
  
  boolean touchingSprite(Object paramObject, LContext paramLContext) {
    if (!(paramObject instanceof Sprite)) {
      Logo.error("argument must be a Sprite", paramLContext);
      return false;
    } 
    Sprite sprite = (Sprite)paramObject;
    Rectangle rectangle = rect().intersection(sprite.rect());
    if (rectangle.width <= 0 || rectangle.height <= 0)
      return false; 
    BufferedImage bufferedImage1 = outImage();
    BufferedImage bufferedImage2 = sprite.outImage();
    int i = rectangle.x - screenX();
    int j = rectangle.y - screenY();
    int k = rectangle.x - sprite.screenX();
    int m = rectangle.y - sprite.screenY();
    for (int n = j; n < j + rectangle.height; n++) {
      int i1 = k;
      for (int i2 = i; i2 < i + rectangle.width; i2++) {
        int i3 = bufferedImage1.getRGB(i2, n);
        int i4 = bufferedImage2.getRGB(i1, m);
        if ((i3 & 0xFF000000) != 0 && (i4 & 0xFF000000) != 0)
          return true; 
        i1++;
      } 
      m++;
    } 
    return false;
  }
  
  boolean touchingColor(Object paramObject, LContext paramLContext) {
    if (!(paramObject instanceof Color)) {
      Logo.error("argument of touchingColor? must be a Color", paramLContext);
      return false;
    } 
    int i = ((Color)paramObject).getRGB() | 0xFF000000;
    Rectangle rectangle = rect();
    BufferedImage bufferedImage1 = outImage();
    BufferedImage bufferedImage2 = paramLContext.canvas.drawAreaWithoutSprite(rectangle, this);
    for (byte b = 0; b < rectangle.height; b++) {
      for (byte b1 = 0; b1 < rectangle.width; b1++) {
        if ((bufferedImage1.getRGB(b1, b) & 0xFF000000) != 0 && 
          colorsMatch(bufferedImage2.getRGB(b1, b), i)) {
          bufferedImage2.flush();
          return true;
        } 
      } 
    } 
    bufferedImage2.flush();
    return false;
  }
  
  boolean colorTouchingColor(Object paramObject1, Object paramObject2, LContext paramLContext) {
    if (!(paramObject1 instanceof Color) || !(paramObject2 instanceof Color)) {
      Logo.error("the arguments of colorTouchingColor? must be Colors", paramLContext);
      return false;
    } 
    int i = ((Color)paramObject1).getRGB() | 0xFF000000;
    int j = ((Color)paramObject2).getRGB() | 0xFF000000;
    Rectangle rectangle = rect();
    BufferedImage bufferedImage1 = outImage();
    BufferedImage bufferedImage2 = paramLContext.canvas.drawAreaWithoutSprite(rectangle, this);
    for (byte b = 0; b < rectangle.height; b++) {
      for (byte b1 = 0; b1 < rectangle.width; b1++) {
        if (colorsMatch(bufferedImage1.getRGB(b1, b), i) && colorsMatch(bufferedImage2.getRGB(b1, b), j)) {
          bufferedImage2.flush();
          return true;
        } 
      } 
    } 
    bufferedImage2.flush();
    return false;
  }
  
  boolean colorsMatch(int paramInt1, int paramInt2) {
    if ((paramInt1 & 0xFF000000) != (paramInt2 & 0xFF000000))
      return false; 
    if ((paramInt1 >> 16 & 0xF8) != (paramInt2 >> 16 & 0xF8))
      return false; 
    if ((paramInt1 >> 8 & 0xF8) != (paramInt2 >> 8 & 0xF8))
      return false; 
    if ((paramInt1 & 0xFFFF00) == 0 && (paramInt2 & 0xFFFF00) == 0 && (
      paramInt1 & 0xFF) <= 8 && (paramInt2 & 0xFF) <= 8)
      return true; 
    if ((paramInt1 & 0xF8) != (paramInt2 & 0xF8))
      return false; 
    return true;
  }
  
  void setalpha(Object paramObject, LContext paramLContext) {
    double d = Logo.aDouble(paramObject, paramLContext);
    if (d < 0.0D)
      d = -d; 
    if (d > 1.0D)
      d = 1.0D; 
    this.alpha = d;
    inval();
  }
  
  void setcostume(Object paramObject1, Object paramObject2, Object paramObject3, LContext paramLContext) {
    if (!(paramObject1 instanceof BufferedImage))
      return; 
    this.rotationX = Logo.anInt(paramObject2, paramLContext);
    this.rotationY = Logo.anInt(paramObject3, paramLContext);
    if (this.costume != null)
      inval(); 
    this.costume = (BufferedImage)paramObject1;
    rotateAndScale();
    inval();
  }
  
  void costumeChanged() {
    inval();
    rotateAndScale();
    inval();
  }
  
  void setscale(Object paramObject, LContext paramLContext) {
    double d1 = Logo.aDouble(paramObject, paramLContext);
    double d2 = Math.min(this.costume.getWidth((ImageObserver)null), 10);
    double d3 = Math.min(this.costume.getHeight((ImageObserver)null), 10);
    double d4 = Math.max(d2 / 480.0D, d3 / 360.0D);
    double d5 = Math.min(480.0D / this.costume.getWidth((ImageObserver)null), 360.0D / this.costume.getHeight((ImageObserver)null));
    this.scale = Math.min(Math.max(d1, d4), d5);
    costumeChanged();
  }
  
  void rotateAndScale() {
    this.filterChanged = true;
    double d1 = (this.rotationstyle == 0) ? this.rotationDegrees : 90.0D;
    if (this.rotatedCostume != null && this.rotatedCostume != this.costume)
      this.rotatedCostume.flush(); 
    if (this.scale == 1.0D && this.rotationDegrees == 90.0D) {
      this.rotatedCostume = this.costume;
      this.offsetX = this.rotationX;
      this.offsetY = this.rotationY;
      return;
    } 
    int i = this.costume.getWidth((ImageObserver)null);
    int j = this.costume.getHeight((ImageObserver)null);
    double d2 = Math.toRadians(d1 - 90.0D);
    AffineTransform affineTransform = AffineTransform.getRotateInstance(d2, (i / 2), (j / 2));
    affineTransform.scale(this.scale, this.scale);
    AffineTransformOp affineTransformOp = new AffineTransformOp(affineTransform, 2);
    Rectangle2D.Float float_ = (Rectangle2D.Float)affineTransformOp.getBounds2D(this.costume);
    float f1 = -float_.x;
    float f2 = -float_.y;
    affineTransform = AffineTransform.getRotateInstance(d2, ((i / 2) + f1), ((j / 2) + f2));
    affineTransform.translate(f1, f2);
    affineTransform.scale(this.scale, this.scale);
    affineTransformOp = new AffineTransformOp(affineTransform, 2);
    this.rotatedCostume = affineTransformOp.filter(this.costume, (BufferedImage)null);
    affineTransform = AffineTransform.getRotateInstance(d2, 0.0D, 0.0D);
    affineTransform.scale(this.scale, this.scale);
    Point2D point2D = affineTransform.transform(new Point2D.Double((this.rotationX - i / 2), (this.rotationY - j / 2)), null);
    this.offsetX = (int)(point2D.getX() + (this.rotatedCostume.getWidth((ImageObserver)null) / 2));
    this.offsetY = (int)(point2D.getY() + (this.rotatedCostume.getHeight((ImageObserver)null) / 2));
    if (this.rotationstyle == 1) {
      double d = (this.rotationDegrees < 0.0D) ? (this.rotationDegrees + 360.0D) : this.rotationDegrees;
      if (d <= 180.0D)
        return; 
      int k = this.rotatedCostume.getWidth((ImageObserver)null);
      affineTransform = AffineTransform.getScaleInstance(-1.0D, 1.0D);
      affineTransform.translate(-k, 0.0D);
      affineTransformOp = new AffineTransformOp(affineTransform, 2);
      this.rotatedCostume = affineTransformOp.filter(this.rotatedCostume, (BufferedImage)null);
      this.offsetX = (int)((k / 2) - this.scale * (this.rotationX - i / 2));
      this.offsetY = (int)(this.scale * this.rotationY);
    } 
  }
  
  void show() {
    this.isShowing = true;
    inval();
  }
  
  void hide() {
    this.isShowing = false;
    inval();
  }
  
  public boolean isShowing() {
    return this.isShowing;
  }
  
  public Rectangle rect() {
    BufferedImage bufferedImage = outImage();
    if (bufferedImage == null)
      return new Rectangle(screenX(), screenY(), 600, 600); 
    return new Rectangle(screenX(), screenY(), bufferedImage.getWidth((ImageObserver)null), bufferedImage.getHeight((ImageObserver)null));
  }
  
  public Rectangle fullRect() {
    Rectangle rectangle = rect();
    if (this.bubble != null)
      rectangle = rectangle.union(this.bubble.rect()); 
    return rectangle;
  }
  
  void inval() {
    this.canvas.inval(fullRect());
  }
  
  public void paint(Graphics paramGraphics) {
    Graphics2D graphics2D = (Graphics2D)paramGraphics;
    if (this.filterChanged)
      applyFilters(); 
    if (this.alpha != 1.0D) {
      Composite composite = graphics2D.getComposite();
      graphics2D.setComposite(AlphaComposite.getInstance(3, (float)this.alpha));
      graphics2D.drawImage(outImage(), screenX(), screenY(), (ImageObserver)null);
      graphics2D.setComposite(composite);
    } else {
      graphics2D.drawImage(outImage(), screenX(), screenY(), (ImageObserver)null);
    } 
  }
  
  public void paintBubble(Graphics paramGraphics) {
    if (this.bubble != null)
      this.bubble.paint(paramGraphics); 
  }
  
  void talkbubble(Object paramObject, boolean paramBoolean, LContext paramLContext) {
    String str = Logo.prs(paramObject);
    inval();
    this.bubble = null;
    if (str.length() == 0)
      return; 
    this.bubble = new Bubble();
    if (!paramBoolean)
      this.bubble.beThinkBubble(true); 
    this.bubble.setContents(str);
    if (this.rotationDegrees >= 0.0D && this.rotationDegrees <= 180.0D)
      this.bubble.pointLeft = true; 
    updateBubble();
  }
  
  void updateBubble() {
    byte b = 5;
    if (this.bubble == null)
      return; 
    inval();
    Rectangle rectangle = rect();
    boolean bool = this.bubble.pointLeft;
    int[] arrayOfInt = bubbleInsets();
    if (bool && rectangle.x + rectangle.width - arrayOfInt[1] + this.bubble.w + b > 482)
      bool = false; 
    if (!bool && rectangle.x + arrayOfInt[0] - this.bubble.w - b < 0)
      bool = true; 
    if (bool) {
      this.bubble.pointLeft = true;
      this.bubble.x = rectangle.x + rectangle.width - arrayOfInt[1] + b;
    } else {
      this.bubble.pointLeft = false;
      this.bubble.x = rectangle.x + arrayOfInt[0] - this.bubble.w - b;
    } 
    this.bubble.y = Math.max(rectangle.y - this.bubble.h - 12, 25);
    inval();
  }
  
  int[] bubbleInsets() {
    BufferedImage bufferedImage = outImage();
    int i = bufferedImage.getWidth();
    int j = bufferedImage.getHeight();
    int k = i;
    int m = i;
    byte b = -1;
    for (byte b1 = 0; b1 < j; b1++) {
      boolean bool = false;
      for (byte b2 = 0; b2 < Math.max(k, m); b2++) {
        int n = bufferedImage.getRGB(b2, b1) & 0xFF000000;
        if (n != 0 && b2 < k) {
          k = b2;
          bool = true;
        } 
        n = bufferedImage.getRGB(i - b2 - 1, b1) & 0xFF000000;
        if (n != 0 && b2 < m) {
          m = b2;
          bool = true;
        } 
      } 
      if (b < 0) {
        if (bool)
          b = b1; 
      } else if (b1 >= b + 10) {
        break;
      } 
    } 
    int[] arrayOfInt = new int[2];
    arrayOfInt[0] = k;
    arrayOfInt[1] = m;
    return arrayOfInt;
  }
  
  void setPenDown(boolean paramBoolean) {
    if (paramBoolean == this.penDown)
      return; 
    if (paramBoolean)
      this.lastPenX = this.lastPenY = -1000000; 
    this.canvas.updatePenTrailForSprite(this);
    this.penDown = paramBoolean;
  }
  
  void setPenColor(Color paramColor) {
    float[] arrayOfFloat = Color.RGBtoHSB(paramColor.getRed(), paramColor.getGreen(), paramColor.getBlue(), null);
    this.penColor = paramColor;
    this.penHue = 200.0D * arrayOfFloat[0];
    float f1 = arrayOfFloat[1];
    float f2 = arrayOfFloat[2];
    if (f2 == 1.0D) {
      this.penShade = 50.0D + 50.0D * (1.0D - f1);
    } else {
      this.penShade = 50.0D * f2;
    } 
  }
  
  void setPenHue(double paramDouble) {
    this.penHue = paramDouble % 200.0D;
    if (this.penHue < 0.0D)
      this.penHue = 200.0D + this.penHue; 
    setPenShade(this.penShade);
  }
  
  void setPenShade(double paramDouble) {
    this.penShade = paramDouble % 200.0D;
    if (this.penShade < 0.0D)
      this.penShade = 200.0D + this.penShade; 
    float f = (float)((this.penShade > 100.0D) ? (200.0D - this.penShade) : this.penShade);
    if (f <= 50.0D) {
      float f1 = (f + 10.0F) / 60.0F;
      this.penColor = new Color(Color.HSBtoRGB((float)(this.penHue / 200.0D), 1.0F, f1));
    } else {
      float f1 = (100.0F - f + 10.0F) / 60.0F;
      this.penColor = new Color(Color.HSBtoRGB((float)(this.penHue / 200.0D), f1, 1.0F));
    } 
  }
  
  BufferedImage outImage() {
    if (this.filteredCostume != null)
      return this.filteredCostume; 
    if (this.rotatedCostume != null)
      return this.rotatedCostume; 
    return this.costume;
  }
  
  void applyFilters() {
    if (!filtersActive()) {
      this.filteredCostume = null;
      this.filterChanged = false;
      return;
    } 
    this.imageFilter.setSourceImage((this.rotatedCostume != null) ? this.rotatedCostume : this.costume);
    if (this.color != 0.0D)
      this.imageFilter.applyHueShift((int)this.color); 
    if (this.brightness != 0.0D)
      this.imageFilter.applyBrightnessShift((int)this.brightness); 
    if (this.whirl != 0.0D)
      this.imageFilter.applyWhirl(this.whirl); 
    if (this.fisheye != 0.0D)
      this.imageFilter.applyFisheye(this.fisheye); 
    if (Math.abs(this.pixelate) >= 5.0D)
      this.imageFilter.applyPixelate(this.pixelate); 
    if (Math.abs(this.mosaic) >= 5.0D)
      this.imageFilter.applyMosaic(this.mosaic); 
    this.filteredCostume = this.imageFilter.filteredImage;
    this.filterChanged = false;
  }
  
  boolean filtersActive() {
    if (this.color != 0.0D || this.brightness != 0.0D || this.fisheye != 0.0D || this.whirl != 0.0D || Math.abs(this.mosaic) >= 5.0D || Math.abs(this.pixelate) >= 5.0D)
      return true; 
    return false;
  }
}
