package grammar;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Vector;

import machine.Machine;
import values.List;
import values.Value;
import context.TerminalSet;
import env.Env;
import grammar.simple.SimplifiedPTerm;

public class StarChars extends PTerm {

  int[] chars;

  public StarChars(int[] chars) {
    super();
    this.chars = chars;
  }

  public PTerm close(Env<String, Value> env) {
    return this;
  }

  public TerminalSet predictors(Env<String, Value> env, HashSet<String> NTs) {
    TerminalSet Ts = new TerminalSet();
    for (int c : chars)
      Ts.add("" + (char) c);
    return Ts;
  }

  public String pprint(int opPrec) {
    return Arrays.toString(chars) + "*";
  }

  public void exec(Machine machine) {
    machine.pushValue(getCharList(machine));
  }

  public List getCharList(Machine machine) {
    if (machine.EOF())
      return List.NIL;
    else {
      int c = machine.peek();
      if (!isLegal(c))
        return List.NIL;
      else {
        machine.consume();
        return List.cons(new values.Int(c), getCharList(machine));
      }
    }
  }

  public boolean isLegal(int c) {
    for (int cc : chars)
      if (c == cc) return true;
    return false;
  }

  public Vector<Vector<SimplifiedPTerm>> simplify() {
    throw new Error("simplification error: StarChars should be translated.");
  }

}
