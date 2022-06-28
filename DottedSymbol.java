class DottedSymbol {
  Symbol sym;
  
  DottedSymbol(Symbol paramSymbol) {
    this.sym = paramSymbol;
  }
  
  public String toString() {
    return ":" + this.sym.toString();
  }
}
