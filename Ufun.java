class Ufun extends Primitives {
  Object[] arglist;
  
  Object[] body;
  
  Ufun(Object[] paramArrayOfObject1, Object[] paramArrayOfObject2) {
    this.arglist = paramArrayOfObject1;
    this.body = paramArrayOfObject2;
  }
  
  public Object dispatch(int paramInt, Object[] paramArrayOfObject, LContext paramLContext) {
    Object object = null;
    Object[] arrayOfObject1 = new Object[this.arglist.length];
    Symbol symbol = paramLContext.ufun;
    paramLContext.ufun = paramLContext.cfun;
    Object[] arrayOfObject2 = paramLContext.locals;
    paramLContext.locals = null;
    for (byte b = 0; b < this.arglist.length; b++) {
      arrayOfObject1[b] = ((Symbol)this.arglist[b]).value;
      ((Symbol)this.arglist[b]).value = paramArrayOfObject[b];
    } 
    try {
      Logo.runCommand(this.body, paramLContext);
      if (paramLContext.ufunresult != null && paramLContext.ufunresult != paramLContext.juststop)
        object = paramLContext.ufunresult; 
    } finally {
      paramLContext.ufun = symbol;
      byte b1;
      for (b1 = 0; b1 < this.arglist.length; b1++)
        ((Symbol)this.arglist[b1]).value = arrayOfObject1[b1]; 
      if (paramLContext.locals != null)
        for (b1 = 0; b1 < paramLContext.locals.length; b1 += 2)
          ((Symbol)paramLContext.locals[b1]).value = paramLContext.locals[b1 + 1];  
      paramLContext.locals = arrayOfObject2;
      paramLContext.ufunresult = null;
    } 
    return object;
  }
}
