package grammar.simple;

public class Call extends SimplifiedPTerm {
  
  String name;

  public Call(String name) {
    super();
    this.name = name;
  }

  public String getName() {
    return name;
  }

}
