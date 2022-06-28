class Function {
  Primitives instance;
  
  int dispatchOffset;
  
  int nargs;
  
  boolean ipm;
  
  Function(Primitives paramPrimitives, int paramInt1, int paramInt2) {
    this(paramPrimitives, paramInt1, paramInt2, false);
  }
  
  Function(Primitives paramPrimitives, int paramInt1, int paramInt2, boolean paramBoolean) {
    this.instance = paramPrimitives;
    this.nargs = paramInt1;
    this.dispatchOffset = paramInt2;
    this.ipm = paramBoolean;
  }
}
