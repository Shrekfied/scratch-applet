import java.util.Vector;

class TokenStream {
  String str;
  
  int offset = 0;
  
  TokenStream(String paramString) {
    this.str = paramString;
    skipSpace();
  }
  
  Object[] readList(LContext paramLContext) {
    Vector vector = new Vector();
    Object object;
    for (; !eof() && (object = readToken(paramLContext)) != null; vector.addElement(object));
    Object[] arrayOfObject = new Object[vector.size()];
    vector.copyInto(arrayOfObject);
    return arrayOfObject;
  }
  
  Object readToken(LContext paramLContext) {
    String str = next();
    try {
      if (str.length() > 2 && str.charAt(0) == '0' && str.charAt(1) == 'x')
        return new Double(Long.parseLong(str.substring(2), 16)); 
    } catch (NumberFormatException numberFormatException) {}
    try {
      if (str.length() > 1 && str.charAt(0) == '$')
        return new Double(Long.parseLong(str.substring(1), 16)); 
    } catch (NumberFormatException numberFormatException) {}
    try {
      if (str.length() > 1 && str.charAt(0) == '0')
        return new Double(Long.parseLong(str.substring(1), 8)); 
    } catch (NumberFormatException numberFormatException) {}
    if (str.equals("]"))
      return null; 
    if (Logo.aValidNumber(str))
      try {
        return Double.valueOf(str);
      } catch (NumberFormatException numberFormatException) {} 
    if (str.charAt(0) == '"')
      return new QuotedSymbol(Logo.intern(str.substring(1), paramLContext)); 
    if (str.charAt(0) == ':')
      return new DottedSymbol(Logo.intern(str.substring(1), paramLContext)); 
    if (str.equals("["))
      return readList(paramLContext); 
    if (str.charAt(0) == '|')
      return str.substring(1); 
    return Logo.intern(str, paramLContext);
  }
  
  boolean startsWith(String paramString) {
    return this.str.startsWith(paramString, this.offset);
  }
  
  void skipToNextLine() {
    for (; !eof() && "\n\r".indexOf(this.str.charAt(this.offset)) == -1; this.offset++);
    skipSpace();
  }
  
  void skipSpace() {
    while (!eof() && " ;,\t\r\n".indexOf(this.str.charAt(this.offset)) != -1) {
      if (peekChar().equals(";")) {
        while (!eof() && "\n\r".indexOf(this.str.charAt(this.offset)) == -1)
          this.offset++; 
        continue;
      } 
      this.offset++;
    } 
  }
  
  String nextLine() {
    String str = "";
    for (; !eof() && ";\n\r".indexOf(peekChar()) == -1; str = str + nextChar());
    skipSpace();
    return str;
  }
  
  String next() {
    String str = "";
    if (!delim(peekChar())) {
      while (!eof() && 
        !delim(peekChar())) {
        if (peekChar().equals("|")) {
          str = str + "|" + getVbarString();
          skipSpace();
          return str;
        } 
        str = str + nextChar();
      } 
    } else {
      str = nextChar();
    } 
    skipSpace();
    return str;
  }
  
  String getVbarString() {
    StringBuffer stringBuffer = new StringBuffer();
    nextChar();
    while (!eof()) {
      if (peekChar().equals("|")) {
        nextChar();
        break;
      } 
      stringBuffer.append(nextChar());
    } 
    return stringBuffer.toString();
  }
  
  boolean delim(String paramString) {
    char c = paramString.charAt(0);
    return ("()[] ,\t\r\n".indexOf(c) != -1);
  }
  
  String peekChar() {
    return String.valueOf(this.str.charAt(this.offset));
  }
  
  String nextChar() {
    return String.valueOf(this.str.charAt(this.offset++));
  }
  
  boolean eof() {
    return (this.str.length() == this.offset);
  }
}
