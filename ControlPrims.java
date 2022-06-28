class ControlPrims extends Primitives {
  static String[] primlist = new String[] { 
      "repeat", "2", "if", "2", "ifelse", "3", "stop", "0", "output", "1", 
      "dotimes", "2", "dolist", "2", "carefully", "2", "errormessage", "0", "unwind-protect", "2", 
      "error", "1", "dispatch", "2", "run", "1", "loop", "1", "forever", "1", 
      "selectq", "2", "stopme", "0" };
  
  public String[] primlist() {
    return primlist;
  }
  
  public Object dispatch(int paramInt, Object[] paramArrayOfObject, LContext paramLContext) {
    switch (paramInt) {
      case 0:
        return prim_repeat(paramArrayOfObject[0], paramArrayOfObject[1], paramLContext);
      case 1:
        return prim_if(paramArrayOfObject[0], paramArrayOfObject[1], paramLContext);
      case 2:
        return prim_ifelse(paramArrayOfObject[0], paramArrayOfObject[1], paramArrayOfObject[2], paramLContext);
      case 3:
        return prim_stop(paramLContext);
      case 4:
        return prim_output(paramArrayOfObject[0], paramLContext);
      case 5:
        return prim_dotimes(paramArrayOfObject[0], paramArrayOfObject[1], paramLContext);
      case 6:
        return prim_dolist(paramArrayOfObject[0], paramArrayOfObject[1], paramLContext);
      case 7:
        return prim_carefully(paramArrayOfObject[0], paramArrayOfObject[1], paramLContext);
      case 8:
        return paramLContext.errormessage;
      case 9:
        return prim_unwindprotect(paramArrayOfObject[0], paramArrayOfObject[1], paramLContext);
      case 10:
        return prim_error(paramArrayOfObject[0], paramLContext);
      case 11:
        return prim_dispatch(paramArrayOfObject[0], paramArrayOfObject[1], paramLContext);
      case 12:
        return prim_run(paramArrayOfObject[0], paramLContext);
      case 13:
        return prim_loop(paramArrayOfObject[0], paramLContext);
      case 14:
        return prim_loop(paramArrayOfObject[0], paramLContext);
      case 15:
        return prim_selectq(paramArrayOfObject[0], paramArrayOfObject[1], paramLContext);
      case 16:
        return prim_stopme(paramLContext);
    } 
    return null;
  }
  
  Object prim_repeat(Object paramObject1, Object paramObject2, LContext paramLContext) {
    int i = Logo.anInt(paramObject1, paramLContext);
    Object[] arrayOfObject = Logo.aList(paramObject2, paramLContext);
    for (byte b = 0; b < i; ) {
      Logo.runCommand(arrayOfObject, paramLContext);
      if (paramLContext.ufunresult != null)
        return null; 
      b++;
    } 
    return null;
  }
  
  Object prim_if(Object paramObject1, Object paramObject2, LContext paramLContext) {
    if (Logo.aBoolean(paramObject1, paramLContext))
      Logo.runCommand(Logo.aList(paramObject2, paramLContext), paramLContext); 
    return null;
  }
  
  Object prim_ifelse(Object paramObject1, Object paramObject2, Object paramObject3, LContext paramLContext) {
    boolean bool = Logo.aBoolean(paramObject1, paramLContext);
    Object[] arrayOfObject1 = Logo.aList(paramObject2, paramLContext);
    Object[] arrayOfObject2 = Logo.aList(paramObject3, paramLContext);
    return bool ? Logo.runList(arrayOfObject1, paramLContext) : Logo.runList(arrayOfObject2, paramLContext);
  }
  
  Object prim_stop(LContext paramLContext) {
    paramLContext.ufunresult = paramLContext.juststop;
    return null;
  }
  
  Object prim_output(Object paramObject, LContext paramLContext) {
    paramLContext.ufunresult = paramObject;
    return null;
  }
  
  Object prim_dotimes(Object paramObject1, Object paramObject2, LContext paramLContext) {
    MapList mapList = new MapList(Logo.aList(paramObject1, paramLContext));
    Object[] arrayOfObject = Logo.aList(paramObject2, paramLContext);
    Symbol symbol = Logo.aSymbol(mapList.next(), paramLContext);
    int i = Logo.anInt(Logo.evalOneArg(mapList, paramLContext), paramLContext);
    Logo.checkListEmpty(mapList, paramLContext);
    Object object = symbol.value;
    try {
      for (byte b = 0; b < i; b++) {
        symbol.value = new Double(b);
        Logo.runCommand(arrayOfObject, paramLContext);
      } 
      if (paramLContext.ufunresult != null)
        return null; 
    } finally {
      symbol.value = object;
    } 
    return null;
  }
  
  Object prim_dolist(Object paramObject1, Object paramObject2, LContext paramLContext) {
    MapList mapList = new MapList(Logo.aList(paramObject1, paramLContext));
    Object[] arrayOfObject1 = Logo.aList(paramObject2, paramLContext);
    Symbol symbol = Logo.aSymbol(mapList.next(), paramLContext);
    Object[] arrayOfObject2 = Logo.aList(Logo.evalOneArg(mapList, paramLContext), paramLContext);
    Logo.checkListEmpty(mapList, paramLContext);
    Object object = symbol.value;
    try {
      for (byte b = 0; b < arrayOfObject2.length; b++) {
        symbol.value = arrayOfObject2[b];
        Logo.runCommand(arrayOfObject1, paramLContext);
        if (paramLContext.ufunresult != null)
          return null; 
      } 
    } finally {
      symbol.value = object;
    } 
    return null;
  }
  
  Object prim_carefully(Object paramObject1, Object paramObject2, LContext paramLContext) {
    Object[] arrayOfObject1 = Logo.aList(paramObject1, paramLContext);
    Object[] arrayOfObject2 = Logo.aList(paramObject2, paramLContext);
    try {
      return Logo.runList(arrayOfObject1, paramLContext);
    } catch (Exception exception) {
      paramLContext.errormessage = exception.getMessage();
      return Logo.runList(arrayOfObject2, paramLContext);
    } 
  }
  
  Object prim_unwindprotect(Object paramObject1, Object paramObject2, LContext paramLContext) {
    Object[] arrayOfObject1 = Logo.aList(paramObject1, paramLContext);
    Object[] arrayOfObject2 = Logo.aList(paramObject2, paramLContext);
    try {
      Logo.runCommand(arrayOfObject1, paramLContext);
    } finally {
      Logo.runCommand(arrayOfObject2, paramLContext);
    } 
    return null;
  }
  
  Object prim_error(Object paramObject, LContext paramLContext) {
    Logo.error(Logo.prs(paramObject), paramLContext);
    return null;
  }
  
  Object prim_dispatch(Object paramObject1, Object paramObject2, LContext paramLContext) {
    int i = Logo.anInt(paramObject1, paramLContext);
    Object[] arrayOfObject1 = Logo.aList(paramObject2, paramLContext);
    Object[] arrayOfObject2 = Logo.aList(arrayOfObject1[i], paramLContext);
    return Logo.runList(arrayOfObject2, paramLContext);
  }
  
  Object prim_run(Object paramObject, LContext paramLContext) {
    return Logo.runList(Logo.aList(paramObject, paramLContext), paramLContext);
  }
  
  Object prim_loop(Object paramObject, LContext paramLContext) {
    Object[] arrayOfObject = Logo.aList(paramObject, paramLContext);
    while (true) {
      Logo.runCommand(arrayOfObject, paramLContext);
      if (paramLContext.ufunresult != null)
        return null; 
    } 
  }
  
  Object prim_selectq(Object paramObject1, Object paramObject2, LContext paramLContext) {
    Object[] arrayOfObject = Logo.aList(paramObject2, paramLContext);
    for (byte b = 0; b < arrayOfObject.length; b += 2) {
      if ((arrayOfObject[b] instanceof DottedSymbol) ? Logo.getValue(((DottedSymbol)arrayOfObject[b]).sym, paramLContext).equals(paramObject1) : arrayOfObject[b].equals(paramObject1))
        return Logo.runList((Object[])arrayOfObject[b + 1], paramLContext); 
    } 
    return null;
  }
  
  Object prim_stopme(LContext paramLContext) {
    Logo.error("", paramLContext);
    return null;
  }
}
