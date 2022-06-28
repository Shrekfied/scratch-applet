import java.util.HashSet;

public class Logo {
  static long starttime = System.currentTimeMillis();
  
  static String runToplevel(Object[] paramArrayOfObject, LContext paramLContext) {
    paramLContext.iline = new MapList(paramArrayOfObject);
    paramLContext.timeToStop = false;
    try {
      evLine(paramLContext);
    } catch (LogoError logoError) {
      if (logoError.getMessage() != null)
        return logoError.getMessage(); 
    } catch (Exception exception) {
      exception.printStackTrace();
      return exception.toString();
    } catch (Error error) {
      return error.toString();
    } 
    return null;
  }
  
  static void evLine(LContext paramLContext) {
    while (!paramLContext.iline.eof() && paramLContext.ufunresult == null) {
      Object object;
      if ((object = eval(paramLContext)) != null)
        error("You don't say what to do with " + prs(object), paramLContext); 
    } 
  }
  
  static Object eval(LContext paramLContext) {
    Object object = evalToken(paramLContext);
    while (infixNext(paramLContext.iline, paramLContext)) {
      if (object instanceof Nothing)
        error(paramLContext.iline.peek() + " needs more inputs", paramLContext); 
      object = evalInfix(object, paramLContext);
    } 
    return object;
  }
  
  static Object evalToken(LContext paramLContext) {
    Object object = paramLContext.iline.next();
    if (object instanceof QuotedSymbol)
      return ((QuotedSymbol)object).sym; 
    if (object instanceof DottedSymbol)
      return getValue(((DottedSymbol)object).sym, paramLContext); 
    if (object instanceof Symbol)
      return evalSym((Symbol)object, null, paramLContext); 
    if (object instanceof String)
      return evalSym(intern((String)object, paramLContext), null, paramLContext); 
    return object;
  }
  
  static Object evalSym(Symbol paramSymbol, Object[] paramArrayOfObject, LContext paramLContext) {
    if (paramLContext.timeToStop)
      error("Stopped!!!", paramLContext); 
    if (paramSymbol.fcn == null)
      error("I don't know how to " + paramSymbol, paramLContext); 
    Symbol symbol = paramLContext.cfun;
    paramLContext.cfun = paramSymbol;
    int i = paramLContext.priority;
    paramLContext.priority = 0;
    Object object = null;
    try {
      Function function = paramSymbol.fcn;
      int j = function.nargs;
      if (paramArrayOfObject == null)
        paramArrayOfObject = evalArgs(j, paramLContext); 
      object = function.instance.dispatch(function.dispatchOffset, paramArrayOfObject, paramLContext);
    } catch (RuntimeException runtimeException) {
      errorHandler(paramSymbol, paramArrayOfObject, runtimeException, paramLContext);
    } finally {
      paramLContext.cfun = symbol;
      paramLContext.priority = i;
    } 
    if (paramLContext.mustOutput && object == null)
      error(paramSymbol + " didn't output to " + paramLContext.cfun, paramLContext); 
    return object;
  }
  
  static Object[] evalArgs(int paramInt, LContext paramLContext) {
    boolean bool = paramLContext.mustOutput;
    paramLContext.mustOutput = true;
    Object[] arrayOfObject = new Object[paramInt];
    try {
      for (byte b = 0; b < paramInt; b++) {
        if (paramLContext.iline.eof())
          error(paramLContext.cfun + " needs more inputs", paramLContext); 
        arrayOfObject[b] = eval(paramLContext);
        if (arrayOfObject[b] instanceof Nothing)
          error(paramLContext.cfun + " needs more inputs", paramLContext); 
      } 
    } finally {
      paramLContext.mustOutput = bool;
    } 
    return arrayOfObject;
  }
  
  static void runCommand(Object[] paramArrayOfObject, LContext paramLContext) {
    boolean bool = paramLContext.mustOutput;
    paramLContext.mustOutput = false;
    try {
      runList(paramArrayOfObject, paramLContext);
    } finally {
      paramLContext.mustOutput = bool;
    } 
  }
  
  static Object runList(Object[] paramArrayOfObject, LContext paramLContext) {
    MapList mapList = paramLContext.iline;
    paramLContext.iline = new MapList(paramArrayOfObject);
    Object object = null;
    try {
      if (paramLContext.mustOutput) {
        object = eval(paramLContext);
      } else {
        evLine(paramLContext);
      } 
      checkListEmpty(paramLContext.iline, paramLContext);
    } finally {
      paramLContext.iline = mapList;
    } 
    return object;
  }
  
  static Object evalOneArg(MapList paramMapList, LContext paramLContext) {
    boolean bool = paramLContext.mustOutput;
    paramLContext.mustOutput = true;
    MapList mapList = paramLContext.iline;
    paramLContext.iline = paramMapList;
    try {
      return eval(paramLContext);
    } finally {
      paramLContext.iline = mapList;
      paramLContext.mustOutput = bool;
    } 
  }
  
  static boolean infixNext(MapList paramMapList, LContext paramLContext) {
    Object object = null;
    Function function = null;
    return (!paramMapList.eof() && object = paramMapList.peek() instanceof Symbol && (function = ((Symbol)object).fcn) != null && function.nargs < paramLContext.priority);
  }
  
  static Object evalInfix(Object paramObject, LContext paramLContext) {
    Symbol symbol1 = (Symbol)paramLContext.iline.next();
    Function function = symbol1.fcn;
    Symbol symbol2 = paramLContext.cfun;
    paramLContext.cfun = symbol1;
    int i = paramLContext.priority;
    paramLContext.priority = function.nargs;
    Object object = null;
    Object[] arrayOfObject = new Object[2];
    arrayOfObject[0] = paramObject;
    try {
      Object[] arrayOfObject1 = evalArgs(1, paramLContext);
      arrayOfObject[1] = arrayOfObject1[0];
      object = function.instance.dispatch(function.dispatchOffset, arrayOfObject, paramLContext);
    } catch (RuntimeException runtimeException) {
      errorHandler(symbol1, arrayOfObject, runtimeException, paramLContext);
    } finally {
      paramLContext.cfun = symbol2;
      paramLContext.priority = i;
    } 
    if (paramLContext.mustOutput && object == null)
      error(symbol1 + " didn't output to " + paramLContext.cfun, paramLContext); 
    return object;
  }
  
  static Symbol intern(String paramString, LContext paramLContext) {
    String str;
    if (paramString.length() == 0) {
      str = paramString;
    } else if (paramString.charAt(0) == '|') {
      str = paramString = paramString.substring(1);
    } else {
      str = paramString.toLowerCase();
    } 
    Symbol symbol = (Symbol)paramLContext.oblist.get(str);
    if (symbol == null)
      paramLContext.oblist.put(str, symbol = new Symbol(paramString)); 
    return symbol;
  }
  
  static Object[] parse(String paramString, LContext paramLContext) {
    TokenStream tokenStream = new TokenStream(paramString);
    return tokenStream.readList(paramLContext);
  }
  
  static String prs(Object paramObject) {
    return prs(paramObject, 10);
  }
  
  static String prs(Object paramObject, int paramInt) {
    return prs(paramObject, paramInt, new HashSet());
  }
  
  static String prs(Object paramObject, int paramInt, HashSet paramHashSet) {
    if (paramObject instanceof Number && paramInt == 16)
      return Long.toString(((Number)paramObject).longValue(), 16).toUpperCase(); 
    if (paramObject instanceof Number && paramInt == 8)
      return Long.toString(((Number)paramObject).longValue(), 8); 
    if (paramObject instanceof Number && isInt((Number)paramObject))
      return Long.toString(((Number)paramObject).longValue(), 10); 
    if (paramObject instanceof Object[]) {
      Object[] arrayOfObject = (Object[])paramObject;
      if (arrayOfObject.length > 0 && paramHashSet.contains(paramObject))
        return "..."; 
      if (arrayOfObject.length > 0)
        paramHashSet.add(paramObject); 
      String str = "";
      for (byte b = 0; b < arrayOfObject.length; b++) {
        if (arrayOfObject[b] instanceof Object[])
          str = str + "["; 
        str = str + prs(arrayOfObject[b], paramInt, paramHashSet);
        if (arrayOfObject[b] instanceof Object[])
          str = str + "]"; 
        if (b != arrayOfObject.length - 1)
          str = str + " "; 
      } 
      return str;
    } 
    return paramObject.toString();
  }
  
  static boolean isInt(Number paramNumber) {
    return (paramNumber.doubleValue() == (new Integer(paramNumber.intValue())).doubleValue());
  }
  
  static boolean aValidNumber(String paramString) {
    if (paramString.length() == 1 && "0123456789".indexOf(paramString.charAt(0)) == -1)
      return false; 
    if ("eE.+-0123456789".indexOf(paramString.charAt(0)) == -1)
      return false; 
    for (byte b = 1; b < paramString.length(); b++) {
      if ("eE.0123456789".indexOf(paramString.charAt(b)) == -1)
        return false; 
    } 
    return true;
  }
  
  static Object getValue(Symbol paramSymbol, LContext paramLContext) {
    Object object;
    if ((object = paramSymbol.value) != null)
      return object; 
    error(paramSymbol + " has no value", paramLContext);
    return null;
  }
  
  static void setValue(Symbol paramSymbol, Object paramObject, LContext paramLContext) {
    paramSymbol.value = paramObject;
  }
  
  static double aDouble(Object paramObject, LContext paramLContext) {
    if (paramObject instanceof Double)
      return ((Double)paramObject).doubleValue(); 
    String str = prs(paramObject);
    if (str.length() > 0 && aValidNumber(str))
      return Double.valueOf(str).doubleValue(); 
    error(paramLContext.cfun + " doesn't like " + prs(paramObject) + " as input", paramLContext);
    return 0.0D;
  }
  
  static int anInt(Object paramObject, LContext paramLContext) {
    if (paramObject instanceof Double)
      return ((Double)paramObject).intValue(); 
    String str = prs(paramObject);
    if (aValidNumber(str))
      return Double.valueOf(str).intValue(); 
    error(paramLContext.cfun + " doesn't like " + str + " as input", paramLContext);
    return 0;
  }
  
  static long aLong(Object paramObject, LContext paramLContext) {
    if (paramObject instanceof Double)
      return ((Double)paramObject).longValue(); 
    String str = prs(paramObject);
    if (aValidNumber(str))
      return Double.valueOf(str).longValue(); 
    error(paramLContext.cfun + " doesn't like " + str + " as input", paramLContext);
    return 0L;
  }
  
  static boolean aBoolean(Object paramObject, LContext paramLContext) {
    if (paramObject instanceof Boolean)
      return ((Boolean)paramObject).booleanValue(); 
    if (paramObject instanceof Symbol)
      return ((Symbol)paramObject).pname.equals("true"); 
    error(paramLContext.cfun + " doesn't like " + prs(paramObject) + " as input", paramLContext);
    return false;
  }
  
  static Object[] aList2Double(Object paramObject, LContext paramLContext) {
    if (paramObject instanceof Object[]) {
      if (((Object[])paramObject).length == 2 && (
        (Object[])paramObject)[0] instanceof Double && ((Object[])paramObject)[1] instanceof Double)
        return (Object[])paramObject; 
      error(paramLContext.cfun + " doesn't like " + prs(paramObject) + " as input", paramLContext);
    } 
    return null;
  }
  
  static Object[] aList(Object paramObject, LContext paramLContext) {
    if (paramObject instanceof Object[])
      return (Object[])paramObject; 
    error(paramLContext.cfun + " doesn't like " + prs(paramObject) + " as input", paramLContext);
    return null;
  }
  
  static Symbol aSymbol(Object paramObject, LContext paramLContext) {
    if (paramObject instanceof Symbol)
      return (Symbol)paramObject; 
    if (paramObject instanceof String)
      return intern((String)paramObject, paramLContext); 
    if (paramObject instanceof Number) {
      String str = String.valueOf(((Number)paramObject).longValue());
      return intern(str, paramLContext);
    } 
    error(paramLContext.cfun + " doesn't like " + prs(paramObject) + " as input", paramLContext);
    return null;
  }
  
  static String aString(Object paramObject, LContext paramLContext) {
    if (paramObject instanceof String)
      return (String)paramObject; 
    if (paramObject instanceof Symbol)
      return ((Symbol)paramObject).toString(); 
    error(paramLContext.cfun + " doesn't like " + prs(paramObject) + " as input", paramLContext);
    return null;
  }
  
  static void setupPrims(String[] paramArrayOfString, LContext paramLContext) {
    for (byte b = 0; b < paramArrayOfString.length; ) {
      setupPrims(paramArrayOfString[b], paramLContext);
      b++;
    } 
  }
  
  static void setupPrims(String paramString, LContext paramLContext) {
    try {
      Class clazz = Class.forName(paramString);
      Primitives primitives = (Primitives)clazz.newInstance();
      String[] arrayOfString = primitives.primlist();
      for (byte b = 0; b < arrayOfString.length; b += 2) {
        String str = arrayOfString[b + 1];
        boolean bool = str.startsWith("i");
        if (bool)
          str = str.substring(1); 
        Symbol symbol = intern(arrayOfString[b], paramLContext);
        symbol.fcn = new Function(primitives, Integer.parseInt(str), b / 2, bool);
      } 
    } catch (Exception exception) {
      System.out.println(exception.toString());
    } 
  }
  
  static void checkListEmpty(MapList paramMapList, LContext paramLContext) {
    if (!paramMapList.eof() && paramLContext.ufunresult == null)
      error("You don't say what to do with " + prs(paramMapList.next()), paramLContext); 
  }
  
  static void errorHandler(Symbol paramSymbol, Object[] paramArrayOfObject, RuntimeException paramRuntimeException, LContext paramLContext) {
    if (paramRuntimeException instanceof ArrayIndexOutOfBoundsException || paramRuntimeException instanceof StringIndexOutOfBoundsException || paramRuntimeException instanceof NegativeArraySizeException) {
      error(paramSymbol + " doesn't like " + prs(paramArrayOfObject[0]) + " as input", paramLContext);
    } else {
      throw paramRuntimeException;
    } 
  }
  
  static void error(String paramString, LContext paramLContext) {
    if (paramString.equals(""))
      throw new LogoError(null); 
    paramString = paramString + ((paramLContext.ufun == null) ? "" : (" in " + paramLContext.ufun));
    throw new LogoError(paramString);
  }
  
  static void readAllFunctions(String paramString, LContext paramLContext) {
    TokenStream tokenStream = new TokenStream(paramString);
    while (true) {
      switch (findKeyWord(tokenStream)) {
        case 0:
          return;
        case 1:
          doDefine(tokenStream, paramLContext);
        case 2:
          doTo(tokenStream, paramLContext);
      } 
    } 
  }
  
  static int findKeyWord(TokenStream paramTokenStream) {
    while (true) {
      if (paramTokenStream.eof())
        return 0; 
      if (paramTokenStream.startsWith("define "))
        return 1; 
      if (paramTokenStream.startsWith("to "))
        return 2; 
      paramTokenStream.skipToNextLine();
    } 
  }
  
  static void doDefine(TokenStream paramTokenStream, LContext paramLContext) {
    paramTokenStream.readToken(paramLContext);
    Symbol symbol = aSymbol(paramTokenStream.readToken(paramLContext), paramLContext);
    Object[] arrayOfObject1 = aList(paramTokenStream.readToken(paramLContext), paramLContext);
    Object[] arrayOfObject2 = aList(paramTokenStream.readToken(paramLContext), paramLContext);
    Ufun ufun = new Ufun(arrayOfObject1, arrayOfObject2);
    symbol.fcn = new Function(ufun, arrayOfObject1.length, 0);
  }
  
  static void doTo(TokenStream paramTokenStream, LContext paramLContext) {
    Object[] arrayOfObject1 = parse(paramTokenStream.nextLine(), paramLContext);
    Object[] arrayOfObject2 = parse(readBody(paramTokenStream, paramLContext), paramLContext);
    Object[] arrayOfObject3 = getArglistFromTitle(arrayOfObject1);
    Symbol symbol = aSymbol(arrayOfObject1[1], paramLContext);
    Ufun ufun = new Ufun(arrayOfObject3, arrayOfObject2);
    symbol.fcn = new Function(ufun, arrayOfObject3.length, 0);
  }
  
  static String readBody(TokenStream paramTokenStream, LContext paramLContext) {
    String str = "";
    while (true) {
      if (paramTokenStream.eof())
        return str; 
      String str1 = paramTokenStream.nextLine();
      if (str1.startsWith("end") && "end".equals(((Symbol)parse(str1, paramLContext)[0]).pname))
        return str; 
      str = str + " " + str1;
    } 
  }
  
  static Object[] getArglistFromTitle(Object[] paramArrayOfObject) {
    Object[] arrayOfObject = new Object[paramArrayOfObject.length - 2];
    for (byte b = 0; b < arrayOfObject.length; b++)
      arrayOfObject[b] = ((DottedSymbol)paramArrayOfObject[b + 2]).sym; 
    return arrayOfObject;
  }
}
