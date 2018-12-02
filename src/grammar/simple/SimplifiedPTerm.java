package grammar.simple;

import java.util.Vector;

import grammar.PTerm;

public class SimplifiedPTerm {

  public static Vector<Vector<SimplifiedPTerm>> empty() {
    return new Vector<Vector<SimplifiedPTerm>>();
  }

  public static Vector<Vector<SimplifiedPTerm>> call(String name) {
    Vector<Vector<SimplifiedPTerm>> result = new Vector<Vector<SimplifiedPTerm>>();
    Vector<SimplifiedPTerm> call = new Vector<SimplifiedPTerm>();
    call.add(new Call(name));
    result.add(call);
    return result;
  }

  public static Vector<Vector<SimplifiedPTerm>> calls(String[] names) {
    Vector<Vector<SimplifiedPTerm>> result = new Vector<Vector<SimplifiedPTerm>>();
    for (String name : names) {
      Vector<SimplifiedPTerm> call = new Vector<SimplifiedPTerm>();
      call.add(new Call(name));
      result.add(call);
    }
    return result;
  }

  public static Vector<Vector<SimplifiedPTerm>> charCode(int c) {
    Vector<Vector<SimplifiedPTerm>> result = new Vector<Vector<SimplifiedPTerm>>();
    Vector<SimplifiedPTerm> charCode = new Vector<SimplifiedPTerm>();
    charCode.add(new CharCode(c));
    result.add(charCode);
    return result;
  }

  public static Vector<Vector<SimplifiedPTerm>> dot() {
    Vector<Vector<SimplifiedPTerm>> result = new Vector<Vector<SimplifiedPTerm>>();
    Vector<SimplifiedPTerm> dot = new Vector<SimplifiedPTerm>();
    dot.add(new Dot());
    result.add(dot);
    return result;
  }

  public static Vector<Vector<SimplifiedPTerm>> chars(int[] chars) {
    Vector<Vector<SimplifiedPTerm>> result = new Vector<Vector<SimplifiedPTerm>>();
    for (int c : chars) {
      Vector<SimplifiedPTerm> charCode = new Vector<SimplifiedPTerm>();
      charCode.add(new CharCode(c));
      result.add(charCode);
    }
    return result;
  }

  public static Vector<Vector<SimplifiedPTerm>> not(PTerm term) {
    Vector<Vector<SimplifiedPTerm>> result = new Vector<Vector<SimplifiedPTerm>>();
    Vector<SimplifiedPTerm> not = new Vector<SimplifiedPTerm>();
    not.add(new Not(term));
    result.add(not);
    return result;
  }

  public static Vector<Vector<SimplifiedPTerm>> appends(Vector<Vector<SimplifiedPTerm>> ts1, Vector<Vector<SimplifiedPTerm>> ts2) {
    Vector<Vector<SimplifiedPTerm>> all = new Vector<Vector<SimplifiedPTerm>>();
    all.addAll(ts1);
    all.addAll(ts2);
    return all;
  }

  public static Vector<Vector<SimplifiedPTerm>> parse(String path) {
    Vector<Vector<SimplifiedPTerm>> result = new Vector<Vector<SimplifiedPTerm>>();
    Vector<SimplifiedPTerm> parse = new Vector<SimplifiedPTerm>();
    parse.add(new Parse(path));
    result.add(parse);
    return result;
  }

  public static Vector<Vector<SimplifiedPTerm>> term(String t) {
    Vector<Vector<SimplifiedPTerm>> result = new Vector<Vector<SimplifiedPTerm>>();
    Vector<SimplifiedPTerm> term = new Vector<SimplifiedPTerm>();
    term.add(new Term(t));
    result.add(term);
    return result;
  }

  public static Vector<SimplifiedPTerm> append(Vector<SimplifiedPTerm> terms1, Vector<SimplifiedPTerm> terms2) {
    Vector<SimplifiedPTerm> terms = new Vector<SimplifiedPTerm>();
    for (SimplifiedPTerm term : terms1)
      terms.add(term);
    for (SimplifiedPTerm term : terms2)
      terms.addElement(term);
    return terms;
  }

}
