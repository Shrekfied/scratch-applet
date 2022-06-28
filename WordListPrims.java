class WordListPrims extends Primitives {
  static String[] primlist = new String[] { 
      "first", "1", "last", "1", "word", "i2", "butfirst", "1", "bf", "1", 
      "butlast", "1", "bl", "1", "fput", "2", "lput", "2", "item", "2", 
      "nth", "2", "empty?", "1", "count", "1", "word?", "1", "list?", "1", 
      "member?", "2", "itempos", "2", "setitem", "3", "setnth", "3", "removeitem", "2", 
      "removeitempos", "2", "sentence", "2", "se", "i2", "list", "i2", "makelist", "1", 
      "copylist", "1", "parse", "1", "char", "1", "ascii", "1", "reverse", "1", 
      "substring", "3", "ucase", "1" };
  
  public String[] primlist() {
    return primlist;
  }
  
  public Object dispatch(int paramInt, Object[] paramArrayOfObject, LContext paramLContext) {
    switch (paramInt) {
      case 0:
        return prim_first(paramArrayOfObject[0], paramLContext);
      case 1:
        return prim_last(paramArrayOfObject[0], paramLContext);
      case 2:
        return prim_word(paramArrayOfObject, paramLContext);
      case 3:
      case 4:
        return prim_butfirst(paramArrayOfObject[0], paramLContext);
      case 5:
      case 6:
        return prim_butlast(paramArrayOfObject[0], paramLContext);
      case 7:
        return prim_fput(paramArrayOfObject[0], paramArrayOfObject[1], paramLContext);
      case 8:
        return prim_lput(paramArrayOfObject[0], paramArrayOfObject[1], paramLContext);
      case 9:
        return prim_item(paramArrayOfObject[0], paramArrayOfObject[1], paramLContext);
      case 10:
        return prim_nth(paramArrayOfObject[0], paramArrayOfObject[1], paramLContext);
      case 11:
        return prim_emptyp(paramArrayOfObject[0], paramLContext);
      case 12:
        return prim_count(paramArrayOfObject[0], paramLContext);
      case 13:
        return prim_wordp(paramArrayOfObject[0], paramLContext);
      case 14:
        return prim_listp(paramArrayOfObject[0], paramLContext);
      case 15:
        return prim_memberp(paramArrayOfObject[0], paramArrayOfObject[1], paramLContext);
      case 16:
        return prim_itempos(paramArrayOfObject[0], paramArrayOfObject[1], paramLContext);
      case 17:
        return prim_setitem(paramArrayOfObject[0], paramArrayOfObject[1], paramArrayOfObject[2], paramLContext);
      case 18:
        return prim_setnth(paramArrayOfObject[0], paramArrayOfObject[1], paramArrayOfObject[2], paramLContext);
      case 19:
        return prim_removeitem(paramArrayOfObject[0], paramArrayOfObject[1], paramLContext);
      case 20:
        return prim_removeitempos(paramArrayOfObject[0], paramArrayOfObject[1], paramLContext);
      case 21:
      case 22:
        return prim_sentence(paramArrayOfObject, paramLContext);
      case 23:
        return prim_list(paramArrayOfObject, paramLContext);
      case 24:
        return prim_makelist(paramArrayOfObject[0], paramLContext);
      case 25:
        return prim_copylist(paramArrayOfObject[0], paramLContext);
      case 26:
        return prim_parse(paramArrayOfObject[0], paramLContext);
      case 27:
        return prim_char(paramArrayOfObject[0], paramLContext);
      case 28:
        return prim_ascii(paramArrayOfObject[0], paramLContext);
      case 29:
        return prim_reverse(paramArrayOfObject[0], paramLContext);
      case 30:
        return prim_substring(paramArrayOfObject[0], paramArrayOfObject[1], paramArrayOfObject[2], paramLContext);
      case 31:
        return prim_ucase(paramArrayOfObject[0], paramLContext);
    } 
    return null;
  }
  
  Object copyList(Object[] paramArrayOfObject, int paramInt1, int paramInt2) {
    Object[] arrayOfObject = new Object[paramInt2];
    for (byte b = 0; b < paramInt2; ) {
      arrayOfObject[b] = paramArrayOfObject[paramInt1++];
      b++;
    } 
    return arrayOfObject;
  }
  
  Object addToList(Object[] paramArrayOfObject, Object paramObject) {
    if (!(paramObject instanceof Object[]))
      return lput(paramObject, paramArrayOfObject); 
    Object[] arrayOfObject1 = (Object[])paramObject, arrayOfObject2 = new Object[paramArrayOfObject.length + arrayOfObject1.length];
    byte b;
    for (b = 0; b < paramArrayOfObject.length; ) {
      arrayOfObject2[b] = paramArrayOfObject[b];
      b++;
    } 
    for (b = 0; b < arrayOfObject1.length; ) {
      arrayOfObject2[b + paramArrayOfObject.length] = arrayOfObject1[b];
      b++;
    } 
    return arrayOfObject2;
  }
  
  Object removeItem(Object[] paramArrayOfObject, int paramInt) {
    Object[] arrayOfObject = new Object[paramArrayOfObject.length - 1];
    byte b1 = 0;
    for (byte b2 = 0; b2 < paramArrayOfObject.length; ) {
      if (b2 != paramInt - 1)
        arrayOfObject[b1++] = paramArrayOfObject[b2]; 
      b2++;
    } 
    return arrayOfObject;
  }
  
  static Object lput(Object paramObject, Object[] paramArrayOfObject) {
    Object[] arrayOfObject = new Object[paramArrayOfObject.length + 1];
    for (byte b = 0; b < paramArrayOfObject.length; ) {
      arrayOfObject[b] = paramArrayOfObject[b];
      b++;
    } 
    arrayOfObject[paramArrayOfObject.length] = paramObject;
    return arrayOfObject;
  }
  
  static int memberp(Object paramObject1, Object paramObject2) {
    if (paramObject2 instanceof Object[]) {
      Object[] arrayOfObject = (Object[])paramObject2;
      for (byte b1 = 0; b1 < arrayOfObject.length; b1++) {
        if (Logo.prs(paramObject1).equals(Logo.prs(arrayOfObject[b1])))
          return b1 + 1; 
      } 
      return 0;
    } 
    if (paramObject1 instanceof Object[])
      return 0; 
    String str1 = Logo.prs(paramObject1), str2 = Logo.prs(paramObject2);
    for (byte b = 0; b < str2.length(); b++) {
      if (str1.regionMatches(true, 0, str2, b, str1.length()))
        return b + 1; 
    } 
    return 0;
  }
  
  Object prim_first(Object paramObject, LContext paramLContext) {
    if (paramObject instanceof Object[])
      return ((Object[])paramObject)[0]; 
    return Logo.prs(paramObject).substring(0, 1);
  }
  
  Object prim_last(Object paramObject, LContext paramLContext) {
    if (paramObject instanceof Object[]) {
      Object[] arrayOfObject = (Object[])paramObject;
      return arrayOfObject[arrayOfObject.length - 1];
    } 
    String str = Logo.prs(paramObject);
    return str.substring(str.length() - 1, str.length());
  }
  
  Object prim_word(Object[] paramArrayOfObject, LContext paramLContext) {
    String str = "";
    for (byte b = 0; b < paramArrayOfObject.length; ) {
      str = str + Logo.prs(paramArrayOfObject[b]);
      b++;
    } 
    return str;
  }
  
  Object prim_butfirst(Object paramObject, LContext paramLContext) {
    if (paramObject instanceof Object[]) {
      Object[] arrayOfObject = (Object[])paramObject;
      return copyList(arrayOfObject, 1, arrayOfObject.length - 1);
    } 
    String str = Logo.prs(paramObject);
    return str.substring(1, str.length());
  }
  
  Object prim_butlast(Object paramObject, LContext paramLContext) {
    if (paramObject instanceof Object[]) {
      Object[] arrayOfObject = (Object[])paramObject;
      return copyList(arrayOfObject, 0, arrayOfObject.length - 1);
    } 
    String str = Logo.prs(paramObject);
    return str.substring(0, str.length() - 1);
  }
  
  Object prim_fput(Object paramObject1, Object paramObject2, LContext paramLContext) {
    Object[] arrayOfObject1 = Logo.aList(paramObject2, paramLContext);
    Object[] arrayOfObject2 = new Object[arrayOfObject1.length + 1];
    arrayOfObject2[0] = paramObject1;
    for (byte b = 0; b < arrayOfObject1.length; ) {
      arrayOfObject2[b + 1] = arrayOfObject1[b];
      b++;
    } 
    return arrayOfObject2;
  }
  
  Object prim_lput(Object paramObject1, Object paramObject2, LContext paramLContext) {
    return lput(paramObject1, Logo.aList(paramObject2, paramLContext));
  }
  
  Object prim_item(Object paramObject1, Object paramObject2, LContext paramLContext) {
    int i = Logo.anInt(paramObject1, paramLContext) - 1;
    return (paramObject2 instanceof Object[]) ? ((Object[])paramObject2)[i] : Logo.prs(paramObject2).substring(i, i + 1);
  }
  
  Object prim_nth(Object paramObject1, Object paramObject2, LContext paramLContext) {
    int i = Logo.anInt(paramObject1, paramLContext);
    return (paramObject2 instanceof Object[]) ? ((Object[])paramObject2)[i] : Logo.prs(paramObject2).substring(i, i + 1);
  }
  
  Object prim_emptyp(Object paramObject, LContext paramLContext) {
    return new Boolean((paramObject instanceof Object[]) ? ((((Object[])paramObject).length == 0)) : ((Logo.prs(paramObject).length() == 0)));
  }
  
  Object prim_count(Object paramObject, LContext paramLContext) {
    return new Long((paramObject instanceof Object[]) ? ((Object[])paramObject).length : Logo.prs(paramObject).length());
  }
  
  Object prim_wordp(Object paramObject, LContext paramLContext) {
    return new Boolean(!(paramObject instanceof Object[]));
  }
  
  Object prim_listp(Object paramObject, LContext paramLContext) {
    return new Boolean(paramObject instanceof Object[]);
  }
  
  Object prim_memberp(Object paramObject1, Object paramObject2, LContext paramLContext) {
    return new Boolean((memberp(paramObject1, paramObject2) != 0));
  }
  
  Object prim_itempos(Object paramObject1, Object paramObject2, LContext paramLContext) {
    int i = memberp(paramObject1, paramObject2);
    if (i != 0)
      return new Long(i); 
    Logo.error(paramLContext.cfun + " doesn't like " + Logo.prs(paramObject1) + " as input", paramLContext);
    return null;
  }
  
  Object prim_setitem(Object paramObject1, Object paramObject2, Object paramObject3, LContext paramLContext) {
    Logo.aList(paramObject2, paramLContext)[Logo.anInt(paramObject1, paramLContext) - 1] = paramObject3;
    return null;
  }
  
  Object prim_setnth(Object paramObject1, Object paramObject2, Object paramObject3, LContext paramLContext) {
    Logo.aList(paramObject2, paramLContext)[Logo.anInt(paramObject1, paramLContext)] = paramObject3;
    return null;
  }
  
  Object prim_removeitem(Object paramObject1, Object paramObject2, LContext paramLContext) {
    Object[] arrayOfObject = Logo.aList(paramObject2, paramLContext);
    return removeItem(arrayOfObject, memberp(paramObject1, arrayOfObject));
  }
  
  Object prim_removeitempos(Object paramObject1, Object paramObject2, LContext paramLContext) {
    return removeItem(Logo.aList(paramObject2, paramLContext), Logo.anInt(paramObject1, paramLContext));
  }
  
  Object prim_sentence(Object[] paramArrayOfObject, LContext paramLContext) {
    Object[] arrayOfObject = new Object[0];
    for (byte b = 0; b < paramArrayOfObject.length; ) {
      arrayOfObject = (Object[])addToList(arrayOfObject, paramArrayOfObject[b]);
      b++;
    } 
    return arrayOfObject;
  }
  
  Object prim_list(Object[] paramArrayOfObject, LContext paramLContext) {
    Object[] arrayOfObject = new Object[paramArrayOfObject.length];
    for (byte b = 0; b < paramArrayOfObject.length; ) {
      arrayOfObject[b] = paramArrayOfObject[b];
      b++;
    } 
    return arrayOfObject;
  }
  
  Object prim_makelist(Object paramObject, LContext paramLContext) {
    int i = Logo.anInt(paramObject, paramLContext);
    Object[] arrayOfObject = new Object[i];
    for (byte b = 0; b < i; ) {
      arrayOfObject[b] = new Object[0];
      b++;
    } 
    return arrayOfObject;
  }
  
  Object prim_copylist(Object paramObject, LContext paramLContext) {
    Object[] arrayOfObject = Logo.aList(paramObject, paramLContext);
    return copyList(arrayOfObject, 0, arrayOfObject.length);
  }
  
  Object prim_parse(Object paramObject, LContext paramLContext) {
    return Logo.parse(Logo.aString(paramObject, paramLContext), paramLContext);
  }
  
  Object prim_char(Object paramObject, LContext paramLContext) {
    char[] arrayOfChar = new char[1];
    arrayOfChar[0] = (char)Logo.anInt(paramObject, paramLContext);
    return new String(arrayOfChar);
  }
  
  Object prim_ascii(Object paramObject, LContext paramLContext) {
    return new Long(Logo.aString(paramObject, paramLContext).charAt(0));
  }
  
  Object prim_reverse(Object paramObject, LContext paramLContext) {
    Object[] arrayOfObject1 = Logo.aList(paramObject, paramLContext);
    Object[] arrayOfObject2 = new Object[arrayOfObject1.length];
    for (byte b = 0; b < arrayOfObject1.length; ) {
      arrayOfObject2[b] = arrayOfObject1[arrayOfObject1.length - b - 1];
      b++;
    } 
    return arrayOfObject2;
  }
  
  Object prim_substring(Object paramObject1, Object paramObject2, Object paramObject3, LContext paramLContext) {
    String str = Logo.prs(paramObject1);
    int i = Logo.anInt(paramObject2, paramLContext), j = Logo.anInt(paramObject3, paramLContext);
    if (i == -1)
      return str.substring(str.length() - j, str.length()); 
    if (j == -1)
      return str.substring(i, str.length()); 
    return str.substring(i, i + j);
  }
  
  Object prim_ucase(Object paramObject, LContext paramLContext) {
    return Logo.prs(paramObject).toUpperCase();
  }
}
