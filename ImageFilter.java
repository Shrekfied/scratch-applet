import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

class ImageFilter {
  BufferedImage filteredImage;
  
  BufferedImage tempImage;
  
  void setSourceImage(BufferedImage paramBufferedImage) {
    if (this.filteredImage == null || this.filteredImage.getWidth((ImageObserver)null) != paramBufferedImage.getWidth((ImageObserver)null) || this.filteredImage.getHeight((ImageObserver)null) != paramBufferedImage.getHeight((ImageObserver)null)) {
      if (this.filteredImage != null)
        this.filteredImage.flush(); 
      this.filteredImage = new BufferedImage(paramBufferedImage.getWidth((ImageObserver)null), paramBufferedImage.getHeight((ImageObserver)null), 2);
    } 
    this.filteredImage.getRaster().setDataElements(0, 0, paramBufferedImage.getData());
  }
  
  BufferedImage makeOutputImage(BufferedImage paramBufferedImage) {
    if (this.tempImage == null || this.tempImage.getWidth((ImageObserver)null) != paramBufferedImage.getWidth((ImageObserver)null) || this.tempImage.getHeight((ImageObserver)null) != paramBufferedImage.getHeight((ImageObserver)null)) {
      if (this.tempImage != null)
        this.tempImage.flush(); 
      this.tempImage = new BufferedImage(paramBufferedImage.getWidth((ImageObserver)null), paramBufferedImage.getHeight((ImageObserver)null), 2);
    } 
    return this.tempImage;
  }
  
  void applyHueShift(int paramInt) {
    BufferedImage bufferedImage1 = this.filteredImage;
    BufferedImage bufferedImage2 = makeOutputImage(bufferedImage1);
    int i = bufferedImage1.getWidth();
    int j = bufferedImage1.getHeight();
    for (byte b = 0; b < i; b++) {
      for (byte b1 = 0; b1 < j; b1++) {
        int m = bufferedImage1.getRGB(b, b1), k = m;
        int n = k & 0xFF000000;
        if (n != 0) {
          int i1 = k >> 16 & 0xFF;
          int i2 = k >> 8 & 0xFF;
          int i3 = k & 0xFF;
          int i4 = (i1 < i2) ? i1 : i2;
          if (i3 < i4)
            i4 = i3; 
          int i5 = (i1 > i2) ? i1 : i2;
          if (i3 > i5)
            i5 = i3; 
          int i7 = i5 * 1000 / 255;
          if (i7 < 150)
            i7 = 150; 
          char c = (i5 == 0) ? Character.MIN_VALUE : ((i5 - i4) * 1000 / i5);
          if (c < '')
            c = ''; 
          int i6 = rgb2hue(i1, i2, i3, i4, i5) + paramInt;
          m = n | hsv2rgb(i6, c, i7);
        } 
        bufferedImage2.setRGB(b, b1, m);
      } 
    } 
    this.tempImage = this.filteredImage;
    this.filteredImage = bufferedImage2;
  }
  
  void applyBrightnessShift(int paramInt) {
    BufferedImage bufferedImage1 = this.filteredImage;
    BufferedImage bufferedImage2 = makeOutputImage(bufferedImage1);
    int i = bufferedImage1.getWidth();
    int j = bufferedImage1.getHeight();
    for (byte b = 0; b < i; b++) {
      for (byte b1 = 0; b1 < j; b1++) {
        int m = bufferedImage1.getRGB(b, b1), k = m;
        int n = k & 0xFF000000;
        if (n != 0) {
          int i1 = k >> 16 & 0xFF;
          int i2 = k >> 8 & 0xFF;
          int i3 = k & 0xFF;
          int i4 = (i1 < i2) ? i1 : i2;
          if (i3 < i4)
            i4 = i3; 
          int i5 = (i1 > i2) ? i1 : i2;
          if (i3 > i5)
            i5 = i3; 
          int i6 = rgb2hue(i1, i2, i3, i4, i5);
          boolean bool = (i5 == 0) ? false : ((i5 - i4) * 1000 / i5);
          int i7 = i5 * 1000 / 255 + 10 * paramInt;
          if (i7 > 1000)
            i7 = 1000; 
          if (i7 < 0)
            i7 = 0; 
          m = n | hsv2rgb(i6, bool, i7);
        } 
        bufferedImage2.setRGB(b, b1, m);
      } 
    } 
    this.tempImage = this.filteredImage;
    this.filteredImage = bufferedImage2;
  }
  
  int rgb2hue(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    int i = paramInt5 - paramInt4;
    if (i == 0)
      return 0; 
    if (paramInt1 == paramInt5)
      return 60 * (paramInt2 - paramInt3) / i; 
    if (paramInt2 == paramInt5)
      return 120 + 60 * (paramInt3 - paramInt1) / i; 
    return 240 + 60 * (paramInt1 - paramInt2) / i;
  }
  
  int hsv2rgb(int paramInt1, int paramInt2, int paramInt3) {
    int i = paramInt1 % 360;
    if (i < 0)
      i += 360; 
    int j = i / 60;
    int k = i % 60;
    int m = (1000 - paramInt2) * paramInt3 / 3922;
    int n = (1000 - paramInt2 * k / 60) * paramInt3 / 3922;
    int i1 = (1000 - paramInt2 * (60 - k) / 60) * paramInt3 / 3922;
    int i2 = paramInt3 * 1000 / 3922;
    switch (j) {
      case 0:
        return i2 << 16 | i1 << 8 | m;
      case 1:
        return n << 16 | i2 << 8 | m;
      case 2:
        return m << 16 | i2 << 8 | i1;
      case 3:
        return m << 16 | n << 8 | i2;
      case 4:
        return i1 << 16 | m << 8 | i2;
      case 5:
        return i2 << 16 | m << 8 | n;
    } 
    return 0;
  }
  
  void applyFisheye(double paramDouble) {
    BufferedImage bufferedImage1 = this.filteredImage;
    BufferedImage bufferedImage2 = makeOutputImage(bufferedImage1);
    int i = bufferedImage1.getWidth();
    int j = bufferedImage1.getHeight();
    double d1 = (i / 2);
    double d2 = (j / 2);
    double d3 = (paramDouble + 100.0D) / 100.0D;
    for (byte b = 0; b < i; b++) {
      for (byte b1 = 0; b1 < j; b1++) {
        double d7, d8, d4 = (b - d1) / d1;
        double d5 = (b1 - d2) / d2;
        double d6 = Math.pow(Math.sqrt(d4 * d4 + d5 * d5), d3);
        if (d6 <= 1.0D) {
          double d = Math.atan2(d5, d4);
          d7 = d1 + d6 * Math.cos(d) * d1;
          d8 = d2 + d6 * Math.sin(d) * d2;
        } else {
          d7 = b;
          d8 = b1;
        } 
        bufferedImage2.setRGB(b, b1, interpolate(bufferedImage1, d7, d8));
      } 
    } 
    this.tempImage = this.filteredImage;
    this.filteredImage = bufferedImage2;
  }
  
  int interpolate(BufferedImage paramBufferedImage, double paramDouble1, double paramDouble2) {
    int i = (int)Math.round(paramDouble1);
    if (i < 0)
      i = 0; 
    if (i >= paramBufferedImage.getWidth((ImageObserver)null))
      i = paramBufferedImage.getWidth((ImageObserver)null) - 1; 
    int j = (int)Math.round(paramDouble2);
    if (j < 0)
      j = 0; 
    if (j >= paramBufferedImage.getHeight((ImageObserver)null))
      j = paramBufferedImage.getHeight((ImageObserver)null) - 1; 
    return paramBufferedImage.getRGB(i, j);
  }
  
  void applyWhirl(double paramDouble) {
    double d1, d3, d4;
    BufferedImage bufferedImage1 = this.filteredImage;
    BufferedImage bufferedImage2 = makeOutputImage(bufferedImage1);
    double d5 = Math.toRadians(-paramDouble);
    int i = bufferedImage1.getWidth();
    int j = bufferedImage1.getHeight();
    double d6 = (i / 2);
    double d7 = (j / 2);
    if (d6 < d7) {
      d1 = d6;
      d3 = d7 / d6;
      d4 = 1.0D;
    } else {
      d1 = d7;
      d3 = 1.0D;
      if (d7 < d6) {
        d4 = d6 / d7;
      } else {
        d4 = 1.0D;
      } 
    } 
    double d2 = d1 * d1;
    for (byte b = 0; b < i; b++) {
      for (byte b1 = 0; b1 < j; b1++) {
        double d8 = d3 * (b - d6);
        double d9 = d4 * (b1 - d7);
        double d10 = d8 * d8 + d9 * d9;
        if (d10 < d2) {
          double d11 = 1.0D - Math.sqrt(d10) / d1;
          double d12 = d5 * d11 * d11;
          double d13 = Math.sin(d12);
          double d14 = Math.cos(d12);
          double d15 = (d14 * d8 - d13 * d9) / d3 + d6;
          double d16 = (d13 * d8 + d14 * d9) / d4 + d7;
          bufferedImage2.setRGB(b, b1, bufferedImage1.getRGB((int)d15, (int)d16));
        } else {
          bufferedImage2.setRGB(b, b1, bufferedImage1.getRGB(b, b1));
        } 
      } 
    } 
    this.tempImage = this.filteredImage;
    this.filteredImage = bufferedImage2;
  }
  
  void applyMosaic(double paramDouble) {
    BufferedImage bufferedImage1 = this.filteredImage;
    int i = (int)(Math.abs(paramDouble) + 10.0D) / 10;
    i = Math.min(i, Math.min(bufferedImage1.getWidth((ImageObserver)null), bufferedImage1.getHeight((ImageObserver)null)));
    if (i <= 1)
      return; 
    AffineTransform affineTransform = AffineTransform.getScaleInstance(1.0D / i, 1.0D / i);
    AffineTransformOp affineTransformOp = new AffineTransformOp(affineTransform, 1);
    BufferedImage bufferedImage2 = affineTransformOp.filter(bufferedImage1, (BufferedImage)null);
    int j = (i + 1) * bufferedImage2.getWidth((ImageObserver)null) - i - 1;
    int k = (i + 1) * bufferedImage2.getHeight((ImageObserver)null) - i - 1;
    BufferedImage bufferedImage3 = new BufferedImage(j, k, 2);
    bufferedImage3.getRaster();
    Graphics graphics = bufferedImage3.getGraphics();
    int m = bufferedImage2.getWidth((ImageObserver)null) - 1;
    int n = bufferedImage2.getHeight((ImageObserver)null) - 1;
    int i1;
    for (i1 = 0; i1 < bufferedImage3.getHeight((ImageObserver)null); i1 += n) {
      int i2;
      for (i2 = 0; i2 < bufferedImage3.getWidth((ImageObserver)null); i2 += m)
        graphics.drawImage(bufferedImage2, i2, i1, null); 
    } 
    graphics.dispose();
    bufferedImage2.flush();
    if (this.filteredImage != null)
      this.filteredImage.flush(); 
    affineTransform = AffineTransform.getScaleInstance(bufferedImage1.getWidth((ImageObserver)null) / bufferedImage3.getWidth((ImageObserver)null), bufferedImage1.getHeight((ImageObserver)null) / bufferedImage3.getHeight((ImageObserver)null));
    affineTransformOp = new AffineTransformOp(affineTransform, 1);
    this.filteredImage = affineTransformOp.filter(bufferedImage3, (BufferedImage)null);
    bufferedImage3.flush();
  }
  
  void applyPixelate(double paramDouble) {
    BufferedImage bufferedImage1 = this.filteredImage;
    double d = (Math.abs(paramDouble) + 10.0D) / 10.0D;
    d = Math.min(d, Math.min(bufferedImage1.getWidth((ImageObserver)null), bufferedImage1.getHeight((ImageObserver)null)));
    if (d <= 1.0D)
      return; 
    AffineTransform affineTransform = AffineTransform.getScaleInstance(1.0D / d, 1.0D / d);
    AffineTransformOp affineTransformOp = new AffineTransformOp(affineTransform, 2);
    BufferedImage bufferedImage2 = affineTransformOp.filter(bufferedImage1, (BufferedImage)null);
    affineTransform = AffineTransform.getScaleInstance(d, d);
    affineTransformOp = new AffineTransformOp(affineTransform, 1);
    this.filteredImage = affineTransformOp.filter(bufferedImage2, this.filteredImage);
    bufferedImage2.flush();
  }
}
