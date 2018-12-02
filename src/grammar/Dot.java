package grammar;

import env.Env;
import grammar.simple.SimplifiedPTerm;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Vector;

import machine.Machine;
import values.Value;
import context.TerminalSet;

public class Dot extends PTerm {

  public Dot() {
  }

  public PTerm close(Env<String, Value> env) {
    return this;
  }

  public TerminalSet predictors(Env<String, Value> env, HashSet<String> NTs) {
    TerminalSet Ts = new TerminalSet();
    Ts.add("ANYCHAR");
    return Ts;
  }

  public void exec(Machine machine) {
    if (!machine.EOF()) {
      machine.pushValue(new values.Int(machine.next()));
    } else {
      machine.error(machine, "expecting . but encountered EOF");
      machine.fail(false);
    }
  }

  public String pprint(int opPrec) {
    return ".";
  }

  public Vector<Vector<SimplifiedPTerm>> simplify() {
    return SimplifiedPTerm.dot();
  }

}
