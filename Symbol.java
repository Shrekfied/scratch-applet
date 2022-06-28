class Symbol {
  String pname;
  
  Function fcn;
  
  Object value;
  
  Symbol(String paramString) {
    this.pname = paramString;
  }
  
  public String toString() {
    return this.pname;
  }
}
