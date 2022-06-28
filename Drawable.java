import java.awt.Graphics;
import java.awt.Rectangle;

interface Drawable {
  boolean isShowing();
  
  Rectangle rect();
  
  Rectangle fullRect();
  
  void paint(Graphics paramGraphics);
  
  void paintBubble(Graphics paramGraphics);
  
  void dragTo(int paramInt1, int paramInt2);
}
