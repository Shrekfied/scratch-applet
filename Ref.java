class Ref {
  int index;
  
  Ref(int paramInt) {
    this.index = paramInt - 1;
  }
  
  public String toString() {
    return "Ref(" + this.index + ")";
  }
}
