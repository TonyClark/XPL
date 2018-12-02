package grammar;

import java.util.HashSet;
import java.util.Vector;

import context.TerminalSet;
import env.Env;
import exp.BoaConstructor;
import grammar.simple.SimplifiedPTerm;
import machine.Machine;
import values.Value;

@BoaConstructor(fields = { "line", "start", "end" })
public class SkipWhite extends PTerm {

  public String line;
  public String start;
  public String end;

  public SkipWhite() {
  }

  public PTerm subst(PTerm term, String extensionPoint) {
    return this;
  }

  public PTerm close(Env<String, Value> env) {
    return this;
  }

  public TerminalSet predictors(Env<String, Value> env, HashSet<String> NTs) {
    TerminalSet Ts = new TerminalSet();
    return Ts;
  }

  public void exec(Machine machine) {
    machine.skipWhiteSpace();
    while (machine.hasPrefix(line) || machine.hasPrefix(start)) {
      if (machine.hasPrefix(line))
        machine.skipLine();
      else if (machine.hasPrefix(start)) {
        while (!machine.EOF() && !machine.hasPrefix(end))
          machine.consume();
        for (int i = 0; i < end.length(); i++)
          machine.consume();

      }
      machine.skipWhiteSpace();
    }
    machine.pushValue(new values.Str(""));
  }

  public String pprint(int opPrec) {
    return "'SKIPWHITE'";
  }

  public Vector<Vector<SimplifiedPTerm>> simplify() {
    return SimplifiedPTerm.empty();
  }

}
