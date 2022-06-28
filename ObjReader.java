import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.Vector;

class ObjReader {
  DataInputStream s;
  
  Object[][] objTable;
  
  static final int ObjectRefID = 99;
  
  static final Object[] empty = new Object[0];
  
  static final Canvas canvas = new Canvas();
  
  ObjReader(InputStream paramInputStream) {
    this.s = new DataInputStream(paramInputStream);
  }
  
  Object[][] readObjects(LContext paramLContext) throws IOException {
    readInfo();
    readObjTable(paramLContext);
    return this.objTable;
  }
  
  Hashtable readInfo() throws IOException {
    byte[] arrayOfByte = new byte[10];
    this.s.read(arrayOfByte);
    if (!"ScratchV01".equals(new String(arrayOfByte)))
      throw new IOException(); 
    int i = this.s.readInt();
    readObjTable(null);
    Object[] arrayOfObject = (Object[])this.objTable[0][0];
    Hashtable hashtable = new Hashtable(arrayOfObject.length);
    for (byte b = 0; b < arrayOfObject.length - 1; b += 2)
      hashtable.put(arrayOfObject[b], arrayOfObject[b + 1]); 
    return hashtable;
  }
  
  void readObjTable(LContext paramLContext) throws IOException {
    byte[] arrayOfByte = new byte[4];
    this.s.read(arrayOfByte);
    if (!"ObjS".equals(new String(arrayOfByte)) || this.s.readByte() != 1)
      throw new IOException(); 
    this.s.read(arrayOfByte);
    if (!"Stch".equals(new String(arrayOfByte)) || this.s.readByte() != 1)
      throw new IOException(); 
    int i = this.s.readInt();
    this.objTable = new Object[i][];
    for (byte b = 0; b < i; b++)
      this.objTable[b] = readObj(); 
    createSpritesAndWatchers(paramLContext);
    buildImagesAndSounds();
    fixSounds();
    resolveReferences();
    uncompressMedia();
  }
  
  Object[] readObj() throws IOException {
    Object[] arrayOfObject;
    int i = this.s.readUnsignedByte();
    if (i < 99) {
      arrayOfObject = new Object[2];
      arrayOfObject[0] = readFixedFormat(i);
      arrayOfObject[1] = new Integer(i);
    } else {
      int j = this.s.readUnsignedByte();
      int k = this.s.readUnsignedByte();
      arrayOfObject = new Object[3 + k];
      arrayOfObject[0] = empty;
      arrayOfObject[1] = new Integer(i);
      arrayOfObject[2] = new Integer(j);
      for (byte b = 3; b < arrayOfObject.length; ) {
        arrayOfObject[b] = readField();
        b++;
      } 
    } 
    return arrayOfObject;
  }
  
  Object readField() throws IOException {
    int i = this.s.readUnsignedByte();
    if (i == 99) {
      int j = this.s.readUnsignedByte() << 16;
      j += this.s.readUnsignedByte() << 8;
      j += this.s.readUnsignedByte();
      return new Ref(j);
    } 
    return readFixedFormat(i);
  }
  
  static final byte[] macRomanToISOLatin = new byte[] { 
      -60, -59, -57, -55, -47, -42, -36, -31, -32, -30, 
      -28, -29, -27, -25, -23, -24, -22, -21, -19, -20, 
      -18, -17, -15, -13, -14, -12, -10, -11, -6, -7, 
      -5, -4, -122, -80, -94, -93, -89, -107, -74, -33, 
      -82, -87, -103, -76, -88, Byte.MIN_VALUE, -58, -40, -127, -79, 
      -118, -115, -91, -75, -114, -113, -112, -102, -99, -86, 
      -70, -98, -26, -8, -65, -95, -84, -90, -125, -83, 
      -78, -85, -69, -123, -96, -64, -61, -43, -116, -100, 
      -106, -105, -109, -108, -111, -110, -9, -77, -1, -97, 
      -71, -92, -117, -101, -68, -67, -121, -73, -126, -124, 
      -119, -62, -54, -63, -53, -56, -51, -50, -49, -52, 
      -45, -44, -66, -46, -38, -37, -39, -48, -120, -104, 
      -81, -41, -35, -34, -72, -16, -3, -2 };
  
  Object readFixedFormat(int paramInt) throws IOException {
    short s;
    int i;
    byte[] arrayOfByte;
    Object[] arrayOfObject1;
    double d1, d2;
    byte b1;
    int arrayOfInt[], j, k;
    Object[] arrayOfObject2;
    byte b2;
    switch (paramInt) {
      case 1:
        return empty;
      case 2:
        return Boolean.TRUE;
      case 3:
        return Boolean.FALSE;
      case 4:
        return new Integer(this.s.readInt());
      case 5:
        return new Integer(this.s.readShort());
      case 6:
      case 7:
        d1 = 0.0D;
        d2 = 1.0D;
        s = this.s.readShort();
        for (b1 = 0; b1 < s; b1++) {
          int m = this.s.readUnsignedByte();
          d1 += d2 * m;
          d2 *= 256.0D;
        } 
        if (paramInt == 7)
          d1 = -d1; 
        return new Double(d1);
      case 8:
        return new Double(this.s.readDouble());
      case 9:
      case 10:
        i = this.s.readInt();
        this.s.read(arrayOfByte = new byte[i]);
        for (b1 = 0; b1 < i; b1++) {
          if (arrayOfByte[b1] < 0)
            arrayOfByte[b1] = macRomanToISOLatin[arrayOfByte[b1] + 128]; 
        } 
        try {
          return new String(arrayOfByte, "ISO-8859-1");
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
          return new String(arrayOfByte);
        } 
      case 11:
        i = this.s.readInt();
        this.s.read(arrayOfByte = new byte[i]);
        return arrayOfByte;
      case 12:
        i = this.s.readInt();
        this.s.read(arrayOfByte = new byte[2 * i]);
        return arrayOfByte;
      case 13:
        i = this.s.readInt();
        arrayOfInt = new int[i];
        for (j = 0; j < arrayOfInt.length; ) {
          arrayOfInt[j] = this.s.readInt();
          j++;
        } 
        return arrayOfInt;
      case 14:
        i = this.s.readInt();
        this.s.read(arrayOfByte = new byte[i]);
        try {
          return new String(arrayOfByte, "UTF-8");
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
          return new String(arrayOfByte);
        } 
      case 20:
      case 21:
      case 22:
      case 23:
        i = this.s.readInt();
        arrayOfObject1 = new Object[i];
        for (j = 0; j < arrayOfObject1.length; ) {
          arrayOfObject1[j] = readField();
          j++;
        } 
        return arrayOfObject1;
      case 24:
      case 25:
        i = this.s.readInt();
        arrayOfObject1 = new Object[2 * i];
        for (j = 0; j < arrayOfObject1.length; ) {
          arrayOfObject1[j] = readField();
          j++;
        } 
        return arrayOfObject1;
      case 30:
      case 31:
        j = this.s.readInt();
        k = 255;
        if (paramInt == 31)
          k = this.s.readUnsignedByte(); 
        return new Color(j >> 22 & 0xFF, j >> 12 & 0xFF, j >> 2 & 0xFF, k);
      case 32:
        arrayOfObject1 = new Object[2];
        arrayOfObject1[0] = readField();
        arrayOfObject1[1] = readField();
        return arrayOfObject1;
      case 33:
        arrayOfObject1 = new Object[4];
        arrayOfObject1[0] = readField();
        arrayOfObject1[1] = readField();
        arrayOfObject1[2] = readField();
        arrayOfObject1[3] = readField();
        return arrayOfObject1;
      case 34:
      case 35:
        arrayOfObject2 = new Object[6];
        for (b2 = 0; b2 < 5; ) {
          arrayOfObject2[b2] = readField();
          b2++;
        } 
        if (paramInt == 35)
          arrayOfObject2[5] = readField(); 
        return arrayOfObject2;
    } 
    System.out.println("Unknown fixed-format class " + paramInt);
    throw new IOException();
  }
  
  void createSpritesAndWatchers(LContext paramLContext) {
    for (byte b = 0; b < this.objTable.length; b++) {
      Object[] arrayOfObject = this.objTable[b];
      int i = ((Number)arrayOfObject[1]).intValue();
      if (i == 124 || i == 125)
        arrayOfObject[0] = new Sprite(paramLContext); 
      if (i == 155) {
        arrayOfObject[0] = new Watcher(paramLContext);
        if (((Number)arrayOfObject[2]).intValue() > 3) {
          Number number1 = (Number)arrayOfObject[23];
          Number number2 = (Number)arrayOfObject[24];
          if (floatWithoutFraction(number1) || floatWithoutFraction(number2))
            arrayOfObject[24] = new Double(number2.doubleValue() + 1.0E-8D); 
        } 
      } 
    } 
  }
  
  boolean floatWithoutFraction(Object paramObject) {
    if (!(paramObject instanceof Double))
      return false; 
    double d = ((Double)paramObject).doubleValue();
    return (Math.round(d) == d);
  }
  
  void resolveReferences() throws IOException {
    for (byte b = 0; b < this.objTable.length; b++) {
      int i = ((Number)this.objTable[b][1]).intValue();
      if (i >= 20 && i <= 29) {
        Object[] arrayOfObject = (Object[])this.objTable[b][0];
        for (byte b1 = 0; b1 < arrayOfObject.length; b1++) {
          Object object = arrayOfObject[b1];
          if (object instanceof Ref)
            arrayOfObject[b1] = deRef((Ref)object); 
        } 
      } 
      if (i > 99)
        for (byte b1 = 3; b1 < (this.objTable[b]).length; b1++) {
          Object object = this.objTable[b][b1];
          if (object instanceof Ref)
            this.objTable[b][b1] = deRef((Ref)object); 
        }  
    } 
  }
  
  Object deRef(Ref paramRef) {
    Object[] arrayOfObject = this.objTable[paramRef.index];
    return (arrayOfObject[0] == null || arrayOfObject[0] == empty) ? arrayOfObject : arrayOfObject[0];
  }
  
  void buildImagesAndSounds() throws IOException {
    for (byte b = 0; b < this.objTable.length; b++) {
      int i = ((Number)this.objTable[b][1]).intValue();
      if (i == 34 || i == 35) {
        Object[] arrayOfObject = (Object[])this.objTable[b][0];
        int j = ((Number)arrayOfObject[0]).intValue();
        int k = ((Number)arrayOfObject[1]).intValue();
        int m = ((Number)arrayOfObject[2]).intValue();
        int[] arrayOfInt = decodePixels(this.objTable[((Ref)arrayOfObject[4]).index][0]);
        MemoryImageSource memoryImageSource = null;
        this.objTable[b][0] = empty;
        if (m <= 8) {
          IndexColorModel indexColorModel;
          if (arrayOfObject[5] != null) {
            Object[] arrayOfObject1 = (Object[])this.objTable[((Ref)arrayOfObject[5]).index][0];
            indexColorModel = customColorMap(m, arrayOfObject1);
          } else {
            indexColorModel = squeakColorMap(m);
          } 
          memoryImageSource = new MemoryImageSource(j, k, indexColorModel, rasterToByteRaster(arrayOfInt, j, k, m), 0, j);
        } 
        if (m == 16)
          memoryImageSource = new MemoryImageSource(j, k, raster16to32(arrayOfInt, j, k), 0, j); 
        if (m == 32)
          memoryImageSource = new MemoryImageSource(j, k, rasterAddAlphaTo32(arrayOfInt), 0, j); 
        if (memoryImageSource != null) {
          int[] arrayOfInt1 = new int[j * k];
          PixelGrabber pixelGrabber = new PixelGrabber(memoryImageSource, 0, 0, j, k, arrayOfInt1, 0, j);
          try {
            pixelGrabber.grabPixels();
          } catch (InterruptedException interruptedException) {}
          BufferedImage bufferedImage = new BufferedImage(j, k, 2);
          bufferedImage.getRaster().setDataElements(0, 0, j, k, arrayOfInt1);
          this.objTable[b][0] = bufferedImage;
        } 
      } 
      if (i == 109) {
        Object[] arrayOfObject = this.objTable[((Ref)this.objTable[b][6]).index];
        this.objTable[b][0] = new ScratchSound(((Number)this.objTable[b][7]).intValue(), (byte[])arrayOfObject[0]);
      } 
    } 
  }
  
  int[] decodePixels(Object paramObject) throws IOException {
    if (paramObject instanceof int[])
      return (int[])paramObject; 
    DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream((byte[])paramObject));
    int i = decodeInt(dataInputStream);
    int[] arrayOfInt = new int[i];
    int j = 0;
    while (true) {
      if ((((dataInputStream.available() > 0) ? 1 : 0) & ((j < i) ? 1 : 0)) != 0) {
        int i1, i2;
        byte b;
        int k = decodeInt(dataInputStream);
        int m = k >> 2;
        int n = k & 0x3;
        switch (n) {
          case 0:
            j += m;
            continue;
          case 1:
            i1 = dataInputStream.readUnsignedByte();
            i2 = i1 << 24 | i1 << 16 | i1 << 8 | i1;
            for (b = 0; b < m; ) {
              arrayOfInt[j++] = i2;
              b++;
            } 
            continue;
          case 2:
            i2 = dataInputStream.readInt();
            for (b = 0; b < m; ) {
              arrayOfInt[j++] = i2;
              b++;
            } 
            continue;
          case 3:
            for (b = 0; b < m; b++) {
              i2 = dataInputStream.readUnsignedByte() << 24;
              i2 |= dataInputStream.readUnsignedByte() << 16;
              i2 |= dataInputStream.readUnsignedByte() << 8;
              i2 |= dataInputStream.readUnsignedByte();
              arrayOfInt[j++] = i2;
            } 
            continue;
        } 
        continue;
      } 
      break;
    } 
    return arrayOfInt;
  }
  
  int decodeInt(DataInputStream paramDataInputStream) throws IOException {
    int i = paramDataInputStream.readUnsignedByte();
    if (i <= 223)
      return i; 
    if (i <= 254)
      return (i - 224) * 256 + paramDataInputStream.readUnsignedByte(); 
    return paramDataInputStream.readInt();
  }
  
  int[] rasterAddAlphaTo32(int[] paramArrayOfint) {
    for (byte b = 0; b < paramArrayOfint.length; b++) {
      int i = paramArrayOfint[b];
      if (i != 0)
        paramArrayOfint[b] = 0xFF000000 | i; 
    } 
    return paramArrayOfint;
  }
  
  int[] raster16to32(int[] paramArrayOfint, int paramInt1, int paramInt2) {
    int[] arrayOfInt = new int[paramInt1 * paramInt2];
    int i = (paramInt1 + 1) / 2;
    for (byte b = 0; b < paramInt2; b++) {
      byte b1 = 16;
      for (byte b2 = 0; b2 < paramInt1; b2++) {
        int j = paramArrayOfint[b * i + b2 / 2] >> b1 & 0xFFFF;
        int k = (j >> 10 & 0x1F) << 3;
        int m = (j >> 5 & 0x1F) << 3;
        int n = (j & 0x1F) << 3;
        boolean bool = (k + m + n == 0) ? false : (0xFF000000 | k << 16 | m << 8 | n);
        arrayOfInt[b * paramInt1 + b2] = bool;
        b1 = (b1 == 16) ? 0 : 16;
      } 
    } 
    return arrayOfInt;
  }
  
  byte[] rasterToByteRaster(int[] paramArrayOfint, int paramInt1, int paramInt2, int paramInt3) {
    byte[] arrayOfByte = new byte[paramInt1 * paramInt2];
    int i = paramArrayOfint.length / paramInt2;
    int j = (1 << paramInt3) - 1;
    int k = 32 / paramInt3;
    for (byte b = 0; b < paramInt2; b++) {
      for (byte b1 = 0; b1 < paramInt1; b1++) {
        int m = paramArrayOfint[b * i + b1 / k];
        int n = paramInt3 * (k - b1 % k - 1);
        arrayOfByte[b * paramInt1 + b1] = (byte)(m >> n & j);
      } 
    } 
    return arrayOfByte;
  }
  
  static final byte[] squeakColors = new byte[] { 
      -1, -1, -1, 0, 0, 0, -1, -1, -1, Byte.MIN_VALUE, 
      Byte.MIN_VALUE, Byte.MIN_VALUE, -1, 0, 0, 0, -1, 0, 0, 0, 
      -1, 0, -1, -1, -1, -1, 0, -1, 0, -1, 
      32, 32, 32, 64, 64, 64, 96, 96, 96, -97, 
      -97, -97, -65, -65, -65, -33, -33, -33, 8, 8, 
      8, 16, 16, 16, 24, 24, 24, 40, 40, 40, 
      48, 48, 48, 56, 56, 56, 72, 72, 72, 80, 
      80, 80, 88, 88, 88, 104, 104, 104, 112, 112, 
      112, 120, 120, 120, -121, -121, -121, -113, -113, -113, 
      -105, -105, -105, -89, -89, -89, -81, -81, -81, -73, 
      -73, -73, -57, -57, -57, -49, -49, -49, -41, -41, 
      -41, -25, -25, -25, -17, -17, -17, -9, -9, -9, 
      0, 0, 0, 0, 51, 0, 0, 102, 0, 0, 
      -103, 0, 0, -52, 0, 0, -1, 0, 0, 0, 
      51, 0, 51, 51, 0, 102, 51, 0, -103, 51, 
      0, -52, 51, 0, -1, 51, 0, 0, 102, 0, 
      51, 102, 0, 102, 102, 0, -103, 102, 0, -52, 
      102, 0, -1, 102, 0, 0, -103, 0, 51, -103, 
      0, 102, -103, 0, -103, -103, 0, -52, -103, 0, 
      -1, -103, 0, 0, -52, 0, 51, -52, 0, 102, 
      -52, 0, -103, -52, 0, -52, -52, 0, -1, -52, 
      0, 0, -1, 0, 51, -1, 0, 102, -1, 0, 
      -103, -1, 0, -52, -1, 0, -1, -1, 51, 0, 
      0, 51, 51, 0, 51, 102, 0, 51, -103, 0, 
      51, -52, 0, 51, -1, 0, 51, 0, 51, 51, 
      51, 51, 51, 102, 51, 51, -103, 51, 51, -52, 
      51, 51, -1, 51, 51, 0, 102, 51, 51, 102, 
      51, 102, 102, 51, -103, 102, 51, -52, 102, 51, 
      -1, 102, 51, 0, -103, 51, 51, -103, 51, 102, 
      -103, 51, -103, -103, 51, -52, -103, 51, -1, -103, 
      51, 0, -52, 51, 51, -52, 51, 102, -52, 51, 
      -103, -52, 51, -52, -52, 51, -1, -52, 51, 0, 
      -1, 51, 51, -1, 51, 102, -1, 51, -103, -1, 
      51, -52, -1, 51, -1, -1, 102, 0, 0, 102, 
      51, 0, 102, 102, 0, 102, -103, 0, 102, -52, 
      0, 102, -1, 0, 102, 0, 51, 102, 51, 51, 
      102, 102, 51, 102, -103, 51, 102, -52, 51, 102, 
      -1, 51, 102, 0, 102, 102, 51, 102, 102, 102, 
      102, 102, -103, 102, 102, -52, 102, 102, -1, 102, 
      102, 0, -103, 102, 51, -103, 102, 102, -103, 102, 
      -103, -103, 102, -52, -103, 102, -1, -103, 102, 0, 
      -52, 102, 51, -52, 102, 102, -52, 102, -103, -52, 
      102, -52, -52, 102, -1, -52, 102, 0, -1, 102, 
      51, -1, 102, 102, -1, 102, -103, -1, 102, -52, 
      -1, 102, -1, -1, -103, 0, 0, -103, 51, 0, 
      -103, 102, 0, -103, -103, 0, -103, -52, 0, -103, 
      -1, 0, -103, 0, 51, -103, 51, 51, -103, 102, 
      51, -103, -103, 51, -103, -52, 51, -103, -1, 51, 
      -103, 0, 102, -103, 51, 102, -103, 102, 102, -103, 
      -103, 102, -103, -52, 102, -103, -1, 102, -103, 0, 
      -103, -103, 51, -103, -103, 102, -103, -103, -103, -103, 
      -103, -52, -103, -103, -1, -103, -103, 0, -52, -103, 
      51, -52, -103, 102, -52, -103, -103, -52, -103, -52, 
      -52, -103, -1, -52, -103, 0, -1, -103, 51, -1, 
      -103, 102, -1, -103, -103, -1, -103, -52, -1, -103, 
      -1, -1, -52, 0, 0, -52, 51, 0, -52, 102, 
      0, -52, -103, 0, -52, -52, 0, -52, -1, 0, 
      -52, 0, 51, -52, 51, 51, -52, 102, 51, -52, 
      -103, 51, -52, -52, 51, -52, -1, 51, -52, 0, 
      102, -52, 51, 102, -52, 102, 102, -52, -103, 102, 
      -52, -52, 102, -52, -1, 102, -52, 0, -103, -52, 
      51, -103, -52, 102, -103, -52, -103, -103, -52, -52, 
      -103, -52, -1, -103, -52, 0, -52, -52, 51, -52, 
      -52, 102, -52, -52, -103, -52, -52, -52, -52, -52, 
      -1, -52, -52, 0, -1, -52, 51, -1, -52, 102, 
      -1, -52, -103, -1, -52, -52, -1, -52, -1, -1, 
      -1, 0, 0, -1, 51, 0, -1, 102, 0, -1, 
      -103, 0, -1, -52, 0, -1, -1, 0, -1, 0, 
      51, -1, 51, 51, -1, 102, 51, -1, -103, 51, 
      -1, -52, 51, -1, -1, 51, -1, 0, 102, -1, 
      51, 102, -1, 102, 102, -1, -103, 102, -1, -52, 
      102, -1, -1, 102, -1, 0, -103, -1, 51, -103, 
      -1, 102, -103, -1, -103, -103, -1, -52, -103, -1, 
      -1, -103, -1, 0, -52, -1, 51, -52, -1, 102, 
      -52, -1, -103, -52, -1, -52, -52, -1, -1, -52, 
      -1, 0, -1, -1, 51, -1, -1, 102, -1, -1, 
      -103, -1, -1, -52, -1, -1, -1, -1 };
  
  IndexColorModel squeakColorMap(int paramInt) {
    boolean bool = (paramInt == 1) ? true : false;
    return new IndexColorModel(paramInt, 256, squeakColors, 0, false, bool);
  }
  
  IndexColorModel customColorMap(int paramInt, Object[] paramArrayOfObject) {
    byte[] arrayOfByte = new byte[4 * paramArrayOfObject.length];
    byte b1 = 0;
    for (byte b2 = 0; b2 < paramArrayOfObject.length; b2++) {
      Color color = (Color)this.objTable[((Ref)paramArrayOfObject[b2]).index][0];
      arrayOfByte[b1++] = (byte)color.getRed();
      arrayOfByte[b1++] = (byte)color.getGreen();
      arrayOfByte[b1++] = (byte)color.getBlue();
      arrayOfByte[b1++] = (byte)color.getAlpha();
    } 
    return new IndexColorModel(paramInt, paramArrayOfObject.length, arrayOfByte, 0, true, 0);
  }
  
  void fixSounds() {
    boolean bool = false;
    byte b;
    for (b = 0; b < this.objTable.length; b++) {
      int i = ((Number)this.objTable[b][1]).intValue();
      if (i == 109 && (
        (ScratchSound)this.objTable[b][0]).isByteReversed())
        bool = true; 
    } 
    if (!bool)
      return; 
    for (b = 0; b < this.objTable.length; b++) {
      int i = ((Number)this.objTable[b][1]).intValue();
      if (i == 109)
        ((ScratchSound)this.objTable[b][0]).reverseBytes(); 
    } 
  }
  
  void uncompressMedia() {
    for (byte b = 0; b < this.objTable.length; b++) {
      Object[] arrayOfObject = this.objTable[b];
      int i = ((Number)arrayOfObject[1]).intValue();
      int j = -1;
      if (i >= 100)
        j = ((Number)arrayOfObject[2]).intValue(); 
      if (i == 162 && j >= 4) {
        if (arrayOfObject[7] instanceof byte[]) {
          BufferedImage bufferedImage = jpegDecode((byte[])arrayOfObject[7]);
          if (bufferedImage != null) {
            if (arrayOfObject[4] instanceof Image)
              ((Image)arrayOfObject[4]).flush(); 
            arrayOfObject[4] = bufferedImage;
            arrayOfObject[7] = empty;
          } 
        } 
        if (arrayOfObject[8] instanceof BufferedImage) {
          arrayOfObject[4] = arrayOfObject[8];
          arrayOfObject[8] = empty;
        } 
      } 
      if (i == 164 && j >= 2 && 
        arrayOfObject[9] instanceof byte[]) {
        int k = ((Number)arrayOfObject[7]).intValue();
        int m = ((Number)arrayOfObject[8]).intValue();
        byte[] arrayOfByte = (byte[])arrayOfObject[9];
        int n = (arrayOfByte.length * 8 + m - 1) / m;
        int[] arrayOfInt = (new ADPCMDecoder(arrayOfByte, m)).decode(n);
        arrayOfObject[4] = new ScratchSound(k, arrayOfInt);
        arrayOfObject[9] = empty;
        arrayOfObject[8] = empty;
        arrayOfObject[7] = empty;
      } 
    } 
  }
  
  void canonicalizeMedia() {
    Vector vector1 = new Vector(100);
    Vector vector2 = new Vector(100);
    for (byte b = 0; b < this.objTable.length; b++) {
      Object[] arrayOfObject = this.objTable[b];
      int i = ((Number)arrayOfObject[1]).intValue();
      if (i == 162)
        BufferedImage bufferedImage = (BufferedImage)arrayOfObject[4]; 
      if (i == 164)
        ScratchSound scratchSound = (ScratchSound)arrayOfObject[4]; 
    } 
  }
  
  BufferedImage jpegDecode(byte[] paramArrayOfbyte) {
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    Image image = toolkit.createImage(paramArrayOfbyte);
    MediaTracker mediaTracker = new MediaTracker(canvas);
    mediaTracker.addImage(image, 0);
    try {
      mediaTracker.waitForID(0);
    } catch (InterruptedException interruptedException) {}
    if (image == null)
      return null; 
    int i = image.getWidth(null);
    int j = image.getHeight(null);
    BufferedImage bufferedImage = new BufferedImage(i, j, 2);
    Graphics graphics = bufferedImage.getGraphics();
    graphics.drawImage(image, 0, 0, i, j, null);
    graphics.dispose();
    image.flush();
    return bufferedImage;
  }
  
  void printit(Object paramObject) {
    if (paramObject instanceof Object[] && ((Object[])paramObject).length == 0) {
      System.out.print(" []");
      return;
    } 
    if (paramObject instanceof BufferedImage) {
      BufferedImage bufferedImage = (BufferedImage)paramObject;
      System.out.print(" BufferedImage_" + paramObject.hashCode() + "(" + bufferedImage.getWidth(null) + "x" + bufferedImage.getHeight(null) + ")");
      return;
    } 
    System.out.print(" " + paramObject);
  }
}
