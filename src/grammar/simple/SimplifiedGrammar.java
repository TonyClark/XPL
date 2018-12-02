package grammar.simple;

import java.util.Hashtable;
import java.util.Vector;

public class SimplifiedGrammar {

  Hashtable<String, Vector<Vector<SimplifiedPTerm>>> rules = new Hashtable<String, Vector<Vector<SimplifiedPTerm>>>();

  public void addRule(String name, Vector<SimplifiedPTerm> newBody) {
    if (!rules.containsKey(name)) {
      rules.put(name, new Vector<Vector<SimplifiedPTerm>>());
    }
    Vector<Vector<SimplifiedPTerm>> bodies = rules.get(name);
    bodies.add(newBody);
  }

  public Hashtable<String, Vector<Vector<SimplifiedPTerm>>> getRules() {
    return rules;
  }

}
