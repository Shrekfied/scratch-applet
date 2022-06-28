class QuotedSymbol {
  Symbol sym;
  
  QuotedSymbol(Symbol paramSymbol) {
    this.sym = paramSymbol;
  }
  
  public String toString() {
    return "\"" + this.sym.toString();
  }
}
