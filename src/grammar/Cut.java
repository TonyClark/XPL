package grammar;

import env.Env;
import grammar.simple.SimplifiedPTerm;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Vector;

import machine.Machine;
import values.Value;
import context.TerminalSet;

public class Cut extends PTerm {

  public PTerm close(Env<String, Value> env) {
    return this;
  }

  public TerminalSet predictors(Env<String, Value> env, HashSet<String> NTs) {
    return new TerminalSet();
  }

  public void exec(Machine machine) {
    machine.cut();
    machine.pushValue(null);
  }

  public String pprint(int opPrec) {
    return "!";
  }

  public Vector<Vector<SimplifiedPTerm>> simplify() {
    return SimplifiedPTerm.empty();
  }

}
