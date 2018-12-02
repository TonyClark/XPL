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

@BoaConstructor(fields = { "c" })
public class Char extends PTerm {

  public int c;

  public Char() {
  }

  public Char(int c) {
    super();
    this.c = c;
  }

  public PTerm close(Env<String, Value> env) {
    return this;
  }

  public TerminalSet predictors(Env<String, Value> env, HashSet<String> NTs) {
    TerminalSet Ts = new TerminalSet();
    Ts.add("" + (char) c);
    return Ts;
  }

  public void exec(Machine machine) {
    if (!machine.EOF())
      if (machine.peek() == c) {
        machine.consume();
        machine.pushValue(new values.Int(c));
      } else {
        machine.error(machine, "expecting char '" + c + "' but got '" + machine.peek() + "'");
        machine.fail(false);
      }
    else {
      machine.error(machine, "expecting char '" + c + "' but encountered EOF");
      machine.fail(false);
    }
  }

  public String pprint(int opPrec) {
    return "" + c;
  }

  public Vector<Vector<SimplifiedPTerm>> simplify() {
    return SimplifiedPTerm.charCode(c);
  }

}
