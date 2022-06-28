import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

class DefiningPrims extends Primitives {
  static String[] primlist = new String[] { 
      "make", "2", "define", "3", "let", "1", "thing", "1", "put", "3", 
      "get", "2", "getp", "2", "plist", "1", "erplist", "1", "name?", "1", 
      "defined?", "1", "clearname", "1", "quote", "1", "intern", "1" };
  
  public String[] primlist() {
    return primlist;
  }
  
  public Object dispatch(int paramInt, Object[] paramArrayOfObject, LContext paramLContext) {
    switch (paramInt) {
      case 0:
        return prim_make(paramArrayOfObject[0], paramArrayOfObject[1], paramLContext);
      case 1:
        return prim_define(paramArrayOfObject[0], paramArrayOfObject[1], paramArrayOfObject[2], paramLContext);
      case 2:
        return prim_let(paramArrayOfObject[0], paramLContext);
      case 3:
        return prim_thing(paramArrayOfObject[0], paramLContext);
      case 4:
        return prim_put(paramArrayOfObject[0], paramArrayOfObject[1], paramArrayOfObject[2], paramLContext);
      case 5:
        return prim_get(paramArrayOfObject[0], paramArrayOfObject[1], paramLContext);
      case 6:
        return prim_get(paramArrayOfObject[0], paramArrayOfObject[1], paramLContext);
      case 7:
        return prim_plist(paramArrayOfObject[0], paramLContext);
      case 8:
        return prim_erplist(paramArrayOfObject[0], paramLContext);
      case 9:
        return prim_namep(paramArrayOfObject[0], paramLContext);
      case 10:
        return prim_definedp(paramArrayOfObject[0], paramLContext);
      case 11:
        return prim_clearname(paramArrayOfObject[0], paramLContext);
      case 12:
        return prim_quote(paramArrayOfObject[0], paramLContext);
      case 13:
        return prim_intern(paramArrayOfObject[0], paramLContext);
    } 
    return null;
  }
  
  Object prim_make(Object paramObject1, Object paramObject2, LContext paramLContext) {
    Logo.setValue(Logo.aSymbol(paramObject1, paramLContext), paramObject2, paramLContext);
    return null;
  }
  
  Object prim_clearname(Object paramObject, LContext paramLContext) {
    Logo.setValue(Logo.aSymbol(paramObject, paramLContext), null, paramLContext);
    return null;
  }
  
  Object prim_define(Object paramObject1, Object paramObject2, Object paramObject3, LContext paramLContext) {
    Symbol symbol = Logo.aSymbol(paramObject1, paramLContext);
    Object[] arrayOfObject1 = Logo.aList(paramObject2, paramLContext);
    Object[] arrayOfObject2 = Logo.aList(paramObject3, paramLContext);
    Ufun ufun = new Ufun(arrayOfObject1, arrayOfObject2);
    symbol.fcn = new Function(ufun, arrayOfObject1.length, 0);
    return null;
  }
  
  Object prim_let(Object paramObject, LContext paramLContext) {
    Vector vector = new Vector();
    if (paramLContext.locals != null)
      for (byte b = 0; b < paramLContext.locals.length; b++)
        vector.addElement(paramLContext.locals[b]);  
    MapList mapList = new MapList(Logo.aList(paramObject, paramLContext));
    while (!mapList.eof()) {
      Symbol symbol = Logo.aSymbol(mapList.next(), paramLContext);
      vector.addElement(symbol);
      vector.addElement(symbol.value);
      Logo.setValue(symbol, Logo.evalOneArg(mapList, paramLContext), paramLContext);
    } 
    paramLContext.locals = new Object[vector.size()];
    vector.copyInto(paramLContext.locals);
    return null;
  }
  
  Object prim_thing(Object paramObject, LContext paramLContext) {
    return Logo.getValue(Logo.aSymbol(paramObject, paramLContext), paramLContext);
  }
  
  Object prim_put(Object paramObject1, Object paramObject2, Object paramObject3, LContext paramLContext) {
    Hashtable hashtable = (Hashtable)paramLContext.props.get(paramObject1);
    if (hashtable == null) {
      hashtable = new Hashtable();
      paramLContext.props.put(paramObject1, hashtable);
    } 
    hashtable.put(paramObject2, paramObject3);
    return null;
  }
  
  Object prim_get(Object paramObject1, Object paramObject2, LContext paramLContext) {
    Hashtable hashtable = (Hashtable)paramLContext.props.get(paramObject1);
    if (hashtable == null)
      return new Object[0]; 
    Object object = hashtable.get(paramObject2);
    if (object == null)
      return new Object[0]; 
    return object;
  }
  
  Object prim_plist(Object paramObject, LContext paramLContext) {
    Hashtable hashtable = (Hashtable)paramLContext.props.get(paramObject);
    if (hashtable == null)
      return new Object[0]; 
    Vector vector = new Vector();
    for (Enumeration enumeration = hashtable.keys(); enumeration.hasMoreElements(); ) {
      Object object = enumeration.nextElement();
      vector.add(object);
      vector.add(hashtable.get(object));
    } 
    Object[] arrayOfObject = new Object[vector.size()];
    vector.copyInto(arrayOfObject);
    return arrayOfObject;
  }
  
  Object prim_erplist(Object paramObject, LContext paramLContext) {
    paramLContext.props.remove(paramObject);
    return null;
  }
  
  Object prim_namep(Object paramObject, LContext paramLContext) {
    return new Boolean(((Logo.aSymbol(paramObject, paramLContext)).value != null));
  }
  
  Object prim_definedp(Object paramObject, LContext paramLContext) {
    return new Boolean(((Logo.aSymbol(paramObject, paramLContext)).fcn != null));
  }
  
  Object prim_quote(Object paramObject, LContext paramLContext) {
    if (paramObject instanceof Object[])
      return paramObject; 
    return new QuotedSymbol(Logo.aSymbol(paramObject, paramLContext));
  }
  
  Object prim_intern(Object paramObject, LContext paramLContext) {
    return Logo.aSymbol(paramObject, paramLContext);
  }
}
