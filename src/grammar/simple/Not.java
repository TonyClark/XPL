package grammar.simple;

import grammar.PTerm;

public class Not extends SimplifiedPTerm {

  PTerm term;
  
  public Not(PTerm term) {
    super();
    this.term = term;
  }

}
