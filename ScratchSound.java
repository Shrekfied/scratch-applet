class ScratchSound {
  int rate;
  
  byte[] samples;
  
  ScratchSound(int paramInt, byte[] paramArrayOfbyte) {
    this.rate = paramInt;
    this.samples = paramArrayOfbyte;
  }
  
  ScratchSound(int paramInt, int[] paramArrayOfint) {
    this.rate = paramInt;
    this.samples = new byte[2 * paramArrayOfint.length];
    byte b1 = 0;
    for (byte b2 = 0; b2 < paramArrayOfint.length; b2++) {
      this.samples[b1++] = (byte)(paramArrayOfint[b2] >> 8 & 0xFF);
      this.samples[b1++] = (byte)(paramArrayOfint[b2] & 0xFF);
    } 
  }
  
  public String toString() {
    double d = (100 * this.samples.length / 2 * this.rate) / 100.0D;
    return "ScratchSound(" + d + ", " + this.rate + ")";
  }
  
  boolean isByteReversed() {
    if (this.samples.length < 100)
      return false; 
    return (matches(reversedMeow) || matches(reversedPop));
  }
  
  boolean matches(byte[] paramArrayOfbyte) {
    for (byte b = 0; b < 100; b++) {
      if (this.samples[b] != paramArrayOfbyte[b])
        return false; 
    } 
    return true;
  }
  
  static final byte[] reversedMeow = new byte[] { 
      33, 0, 58, 0, 21, 0, -32, -1, -34, -1, 
      5, 0, -34, -1, -11, -1, -59, -1, -94, -1, 
      -87, -1, -108, -1, 109, -1, 120, -1, 91, -1, 
      76, -1, 66, -1, 20, -1, -19, -2, -28, -2, 
      -38, -2, -126, -2, 57, -2, 5, -2, 41, -2, 
      -115, -2, 16, -1, 70, -1, -47, -1, 109, 0, 
      84, 1, 47, 2, -56, 2, -100, 2, -92, 2, 
      -66, 2, -13, 2, 33, 3, 109, 3, -2, 2, 
      65, 2, 75, 2, 105, 2, 49, 2, 13, 2, 
      78, 1, 17, 1, -86, 0, 112, 0, -82, -1 };
  
  static final byte[] reversedPop = new byte[] { 
      -43, 0, 3, 3, -67, 7, 114, 13, -17, 21, 
      83, 29, 60, 35, -101, 36, -30, 32, -85, 22, 
      115, 6, 85, -15, 95, -38, 96, -60, -115, -77, 
      105, -87, -110, -88, -27, -79, 71, -59, 65, -31, 
      -13, 2, -19, 38, -47, 71, 74, 97, 125, 111, 
      35, 112, 10, 97, -50, 68, -22, 29, 42, -13, 
      15, -55, 106, -89, -3, -111, 37, -115, -18, -104, 
      57, -75, -24, -35, -30, 12, -99, 58, 68, 96, 
      112, 118, -75, 121, -94, 104, -40, 69, 103, 23, 
      74, -28, 108, -75, 122, -110, -34, -127, 107, -122 };
  
  void reverseBytes() {
    for (byte b = 0; b < this.samples.length - 1; b += 2) {
      byte b1 = this.samples[b];
      this.samples[b] = this.samples[b + 1];
      this.samples[b + 1] = b1;
    } 
  }
}
