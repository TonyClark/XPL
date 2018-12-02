package grammar.simple;

public class CharCode extends SimplifiedPTerm {

  int c;

  public CharCode(int c) {
    super();
    this.c = c;
  }

  public int getChar() {
    return c;
  }

}
