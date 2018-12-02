package grammar;

import java.util.HashSet;
import java.util.Vector;

import context.TerminalSet;
import env.Env;
import exp.BoaConstructor;
import grammar.simple.SimplifiedPTerm;
import machine.Machine;
import machine.instrs.PopLine;
import values.Value;

@BoaConstructor(fields = { "term" })
public class PushLine extends PTerm {

  public PTerm term;

  public PushLine() {
  }

  public PushLine(PTerm term) {
    super();
    this.term = term;
  }

  public PTerm close(Env<String, Value> env) {
    return new PushLine(term.close(env));
  }

  public TerminalSet predictors(Env<String, Value> env, HashSet<String> NTs) {
    return term.predictors(env, NTs);
  }

  public void exec(Machine machine) {
    machine.skipWhiteSpace();
    machine.pushLinePos();
    machine.pushInstr(PopLine.POP);
    term.exec(machine);
  }

  public String pprint(int opPrec) {
    return "[[" + term.pprint(STAROP) + "]]";
  }

  public Vector<Vector<SimplifiedPTerm>> simplify() {
    return SimplifiedPTerm.empty();
  }

}
