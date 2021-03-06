package grammar;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Vector;

import machine.Machine;
import values.Value;
import context.TerminalSet;
import env.Env;
import exp.BoaConstructor;
import grammar.simple.SimplifiedPTerm;

@BoaConstructor(fields = { "lower", "upper" })
public class Range extends PTerm {

  public int lower;
  public int upper;

  public Range() {
  }

  public Range(int lower, int upper) {
    super();
    this.lower = lower;
    this.upper = upper;
  }

  public PTerm subst(PTerm term, String extensionPoint) {
    return this;
  }

  public PTerm close(Env<String, Value> env) {
    return this;
  }

  public TerminalSet predictors(Env<String, Value> env, HashSet<String> NTs) {
    TerminalSet Ts = new TerminalSet();
    Ts.add("[" + (char) lower + "," + (char) upper + "]");
    return Ts;
  }

  public void exec(Machine machine) {
    if (!machine.EOF())
      if (machine.peek() >= lower && machine.peek() <= upper) {
        machine.pushValue(new values.Int(machine.peek()));
        machine.consume();
      } else {
        machine.error(machine, "expecting char in range [" + lower + "," + upper + "] but encountered " + machine.peek());
        machine.fail(false);
      }
    else machine.fail(false);
  }

  public String pprint(int opPrec) {
    return "[" + lower + "," + upper + "]";
  }

  public Vector<Vector<SimplifiedPTerm>> simplify() {
    return desugar().simplify();
  }

  public PTerm desugar() {
    PTerm term = new Char(lower);
    for (int i = lower + 1; i < upper + 1; i++) {
      term = new Or(term, new Char(i));
    }
    return term;
  }

}
