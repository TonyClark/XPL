package grammar;

import java.util.HashSet;
import java.util.Vector;

import context.TerminalSet;
import env.Env;
import grammar.simple.SimplifiedPTerm;
import machine.Machine;
import values.Value;

public class EOF extends PTerm {

  public PTerm close(Env<String, Value> env) {
    return this;
  }

  public TerminalSet predictors(Env<String, Value> env, HashSet<String> NTs) {
    TerminalSet Ts = new TerminalSet();
    Ts.add("EOF");
    return Ts;
  }

  public void exec(Machine machine) {
    machine.skipWhiteSpace();
    if (machine.EOF()) {
      machine.pushValue(null);
    } else {
      machine.error(machine, "expecting EOF");
      machine.fail(false);
    }

  }

  public String pprint(int opPrec) {
    return "EOF";
  }

  public Vector<Vector<SimplifiedPTerm>> simplify() {
    return SimplifiedPTerm.empty();
  }

}
