import java.awt.Color;
import java.awt.Toolkit;
import java.text.DecimalFormat;

class SpritePrims extends Primitives {
  static final String[] primlist = new String[] { 
      "who", "0", "talkto", "1", "xpos", "0", "ypos", "0", "setx", "1", 
      "sety", "1", "%left", "0", "%top", "0", "%right", "0", "%bottom", "0", 
      "%setleft", "1", "%settop", "1", "%w", "0", "%h", "0", "costume", "0", 
      "setcostume", "3", "%scale", "0", "setscale", "1", "heading", "0", "setheading", "1", 
      "rotationstyle", "0", "setrotationstyle", "1", "show", "0", "hide", "0", "changed", "0", 
      "containsPoint?", "2", "alpha", "0", "setalpha", "1", "color", "0", "setcolor", "1", 
      "brightness", "0", "setbrightness", "1", "fisheye", "0", "setfisheye", "1", "whirl", "0", 
      "setwhirl", "1", "mosaic", "0", "setmosaic", "1", "pixelate", "0", "setpixelate", "1", 
      "beep", "0", "startSound", "1", "isSoundPlaying?", "1", "stopSound", "1", "stopAllSounds", "0", 
      "setPenDown", "1", "setPenColor", "1", "penSize", "0", "setPenSize", "1", "penHue", "0", 
      "setPenHue", "1", "penShade", "0", "setPenShade", "1", "clearPenTrails", "0", "stampCostume", "0", 
      "newcolor", "3", "touchingSprite?", "1", "touchingColor?", "1", "colorTouchingColor?", "2", "isShowing", "1", 
      "talkbubble", "1", "thinkbubble", "1", "updateBubble", "0", "watcher?", "1", "setWatcherXY", "3", 
      "setWatcherColorAndLabel", "3", "setWatcherSliderMinMax", "3", "setWatcherMode", "2", "setWatcherText", "2", "isDraggable", "0", 
      "setDraggable", "1" };
  
  public String[] primlist() {
    return primlist;
  }
  
  public Object dispatch(int paramInt, Object[] paramArrayOfObject, LContext paramLContext) {
    Sprite sprite = paramLContext.who;
    switch (paramInt) {
      case 0:
        return (sprite == null) ? new Object[0] : sprite;
      case 1:
        paramLContext.who = (paramArrayOfObject[0] instanceof Sprite) ? (Sprite)paramArrayOfObject[0] : null;
        return null;
      case 2:
        return new Double((sprite == null) ? 0.0D : sprite.x);
      case 3:
        return new Double((sprite == null) ? 0.0D : sprite.y);
      case 4:
        sprite.x = Logo.aDouble(paramArrayOfObject[0], paramLContext);
        return null;
      case 5:
        sprite.y = Logo.aDouble(paramArrayOfObject[0], paramLContext);
        return null;
      case 6:
        return new Double(sprite.screenX());
      case 7:
        return new Double(sprite.screenY());
      case 8:
        return new Double((sprite.screenX() + sprite.outImage().getWidth(null)));
      case 9:
        return new Double((sprite.screenY() + sprite.outImage().getHeight(null)));
      case 10:
        sprite.setscreenX(Logo.aDouble(paramArrayOfObject[0], paramLContext));
        return null;
      case 11:
        sprite.setscreenY(Logo.aDouble(paramArrayOfObject[0], paramLContext));
        return null;
      case 12:
        return new Double(sprite.outImage().getWidth(null));
      case 13:
        return new Double(sprite.outImage().getHeight(null));
      case 14:
        return sprite.costume;
      case 15:
        sprite.setcostume(paramArrayOfObject[0], paramArrayOfObject[1], paramArrayOfObject[2], paramLContext);
        sprite.costumeChanged();
        return null;
      case 16:
        return new Double(sprite.scale);
      case 17:
        sprite.setscale(paramArrayOfObject[0], paramLContext);
        return null;
      case 18:
        return prim_heading(paramLContext);
      case 19:
        sprite.rotationDegrees = Logo.aDouble(paramArrayOfObject[0], paramLContext) % 360.0D;
        sprite.costumeChanged();
        return null;
      case 20:
        return new Double(sprite.rotationstyle);
      case 21:
        sprite.rotationstyle = Logo.anInt(paramArrayOfObject[0], paramLContext);
        sprite.costumeChanged();
        return null;
      case 22:
        sprite.show();
        return null;
      case 23:
        sprite.hide();
        return null;
      case 24:
        sprite.inval();
        return null;
      case 25:
        return prim_containsPoint(paramArrayOfObject[0], paramArrayOfObject[1], paramLContext);
      case 26:
        return new Double(sprite.alpha);
      case 27:
        sprite.setalpha(paramArrayOfObject[0], paramLContext);
        return null;
      case 28:
        return new Double(sprite.color);
      case 29:
        sprite.color = Logo.aDouble(paramArrayOfObject[0], paramLContext);
        sprite.filterChanged = true;
        return null;
      case 30:
        return new Double(sprite.brightness);
      case 31:
        sprite.brightness = Logo.aDouble(paramArrayOfObject[0], paramLContext);
        sprite.filterChanged = true;
        return null;
      case 32:
        return new Double(sprite.fisheye);
      case 33:
        sprite.fisheye = Logo.aDouble(paramArrayOfObject[0], paramLContext);
        sprite.filterChanged = true;
        return null;
      case 34:
        return new Double(sprite.whirl);
      case 35:
        sprite.whirl = Logo.aDouble(paramArrayOfObject[0], paramLContext);
        sprite.filterChanged = true;
        return null;
      case 36:
        return new Double(sprite.mosaic);
      case 37:
        sprite.mosaic = Logo.aDouble(paramArrayOfObject[0], paramLContext);
        sprite.filterChanged = true;
        return null;
      case 38:
        return new Double(sprite.pixelate);
      case 39:
        sprite.pixelate = Logo.aDouble(paramArrayOfObject[0], paramLContext);
        sprite.filterChanged = true;
        return null;
      case 40:
        Toolkit.getDefaultToolkit().beep();
        return null;
      case 41:
        return SoundPlayer.startSound(paramArrayOfObject[0], sprite, paramLContext);
      case 42:
        return new Boolean(SoundPlayer.isSoundPlaying(paramArrayOfObject[0]));
      case 43:
        SoundPlayer.stopSound(paramArrayOfObject[0]);
        return null;
      case 44:
        SoundPlayer.stopSoundsForApplet(paramLContext);
        return null;
      case 45:
        sprite.setPenDown(Logo.aBoolean(paramArrayOfObject[0], paramLContext));
        return null;
      case 46:
        if (paramArrayOfObject[0] instanceof Color)
          sprite.setPenColor((Color)paramArrayOfObject[0]); 
        return null;
      case 47:
        return new Double(sprite.penSize);
      case 48:
        sprite.penSize = Logo.anInt(paramArrayOfObject[0], paramLContext);
        return null;
      case 49:
        return new Double(sprite.penHue);
      case 50:
        sprite.setPenHue(Logo.aDouble(paramArrayOfObject[0], paramLContext));
        return null;
      case 51:
        return new Double(sprite.penShade);
      case 52:
        sprite.setPenShade(Logo.aDouble(paramArrayOfObject[0], paramLContext));
        return null;
      case 53:
        paramLContext.canvas.clearPenTrails();
        return null;
      case 54:
        paramLContext.canvas.stampCostume(sprite);
        return null;
      case 55:
        return prim_newcolor(paramArrayOfObject[0], paramArrayOfObject[1], paramArrayOfObject[2], paramLContext);
      case 56:
        return new Boolean(sprite.touchingSprite(paramArrayOfObject[0], paramLContext));
      case 57:
        return new Boolean(sprite.touchingColor(paramArrayOfObject[0], paramLContext));
      case 58:
        return new Boolean(sprite.colorTouchingColor(paramArrayOfObject[0], paramArrayOfObject[1], paramLContext));
      case 59:
        return new Boolean((paramArrayOfObject[0] instanceof Sprite && ((Sprite)paramArrayOfObject[0]).isShowing()));
      case 60:
        sprite.talkbubble(paramArrayOfObject[0], true, paramLContext);
        return null;
      case 61:
        sprite.talkbubble(paramArrayOfObject[0], false, paramLContext);
        return null;
      case 62:
        sprite.updateBubble();
        return null;
      case 63:
        return new Boolean(paramArrayOfObject[0] instanceof Watcher);
      case 64:
        prim_setWatcherXY(paramArrayOfObject[0], paramArrayOfObject[1], paramArrayOfObject[2], paramLContext);
        return null;
      case 65:
        prim_setWatcherColorAndLabel(paramArrayOfObject[0], paramArrayOfObject[1], paramArrayOfObject[2], paramLContext);
        return null;
      case 66:
        prim_setWatcherSliderMinMax(paramArrayOfObject[0], paramArrayOfObject[1], paramArrayOfObject[2], paramLContext);
        return null;
      case 67:
        prim_setWatcherMode(paramArrayOfObject[0], paramArrayOfObject[1], paramLContext);
        return null;
      case 68:
        prim_setWatcherText(paramArrayOfObject[0], paramArrayOfObject[1], paramLContext);
        return null;
      case 69:
        return new Boolean(sprite.isDraggable);
      case 70:
        sprite.isDraggable = Logo.aBoolean(paramArrayOfObject[0], paramLContext);
        return null;
    } 
    return null;
  }
  
  Object prim_heading(LContext paramLContext) {
    double d = paramLContext.who.rotationDegrees % 360.0D;
    if (d > 180.0D)
      d -= 360.0D; 
    return new Double(d);
  }
  
  Object prim_containsPoint(Object paramObject1, Object paramObject2, LContext paramLContext) {
    if (paramLContext.who == null)
      return new Boolean(false); 
    int i = Logo.anInt(paramObject1, paramLContext) + 241;
    int j = 206 - Logo.anInt(paramObject2, paramLContext);
    return new Boolean(paramLContext.who.containsPoint(i, j));
  }
  
  Color prim_newcolor(Object paramObject1, Object paramObject2, Object paramObject3, LContext paramLContext) {
    int i = Logo.anInt(paramObject1, paramLContext);
    int j = Logo.anInt(paramObject2, paramLContext);
    int k = Logo.anInt(paramObject3, paramLContext);
    return new Color(i, j, k);
  }
  
  void prim_setWatcherXY(Object paramObject1, Object paramObject2, Object paramObject3, LContext paramLContext) {
    if (!(paramObject1 instanceof Watcher))
      return; 
    Watcher watcher = (Watcher)paramObject1;
    watcher.inval();
    watcher.box.x = Logo.anInt(paramObject2, paramLContext);
    watcher.box.y = Logo.anInt(paramObject3, paramLContext);
    watcher.inval();
  }
  
  void prim_setWatcherColorAndLabel(Object paramObject1, Object paramObject2, Object paramObject3, LContext paramLContext) {
    if (!(paramObject1 instanceof Watcher))
      return; 
    Watcher watcher = (Watcher)paramObject1;
    watcher.inval();
    watcher.readout.color = (Color)paramObject2;
    watcher.label = (String)paramObject3;
    watcher.inval();
  }
  
  void prim_setWatcherSliderMinMax(Object paramObject1, Object paramObject2, Object paramObject3, LContext paramLContext) {
    if (!(paramObject1 instanceof Watcher))
      return; 
    Watcher watcher = (Watcher)paramObject1;
    watcher.sliderMin = Logo.aDouble(paramObject2, paramLContext);
    watcher.sliderMax = Logo.aDouble(paramObject3, paramLContext);
    watcher.isDiscrete = (Math.round(watcher.sliderMin) == watcher.sliderMin && Math.round(watcher.sliderMax) == watcher.sliderMax);
  }
  
  void prim_setWatcherMode(Object paramObject1, Object paramObject2, LContext paramLContext) {
    if (!(paramObject1 instanceof Watcher))
      return; 
    Watcher watcher = (Watcher)paramObject1;
    int i = Logo.anInt(paramObject2, paramLContext);
    watcher.inval();
    watcher.mode = Math.max(0, Math.min(i, 3));
    watcher.inval();
  }
  
  void prim_setWatcherText(Object paramObject1, Object paramObject2, LContext paramLContext) {
    if (!(paramObject1 instanceof Watcher))
      return; 
    Watcher watcher = (Watcher)paramObject1;
    String str = Logo.prs(paramObject2);
    if (paramObject2 instanceof Double) {
      double d1 = ((Double)paramObject2).doubleValue();
      double d2 = Math.abs(d1);
      DecimalFormat decimalFormat = new DecimalFormat("0.#");
      if (d2 < 1.0D)
        decimalFormat = new DecimalFormat("0.######"); 
      if (d2 < 1.0E-5D || d2 >= 1000000.0D)
        decimalFormat = new DecimalFormat("0.###E0"); 
      if (d2 == 0.0D)
        decimalFormat = new DecimalFormat("0.#"); 
      str = decimalFormat.format(d1);
    } 
    if (str.equals(watcher.readout.contents))
      return; 
    watcher.inval();
    watcher.readout.contents = str;
    watcher.inval();
  }
}
