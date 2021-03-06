package grammar;

import java.util.HashSet;
import java.util.Stack;
import java.util.Vector;

import context.TerminalSet;
import env.Env;
import exp.BoaConstructor;
import grammar.simple.SimplifiedPTerm;
import machine.Fail;
import machine.Machine;
import values.Value;

@BoaConstructor(fields = { "term" })
public class Not extends PTerm {

  public PTerm term;

  public Not() {
  }

  public Not(PTerm term) {
    super();
    this.term = term;
  }

  public PTerm close(Env<String, Value> env) {
    if (term.close(env) instanceof RangeChoice) return new NotRanges(((RangeChoice) term.close(env)).ranges);
    return new Not(term.close(env));
  }

  public TerminalSet predictors(Env<String, Value> env, HashSet<String> NTs) {
    return new TerminalSet();
  }

  @SuppressWarnings("unchecked")
  public void exec(Machine machine) {
    machine.instrs.Not not = new machine.instrs.Not((Stack<Fail>) machine.getFails().clone(), term);
    machine.choice();
    machine.pushInstr(not);
    machine.pushInstr(term);
  }

  public String pprint(int opPrec) {
    return "not(" + term.pprint(10) + ")";
  }

  public Vector<Vector<SimplifiedPTerm>> simplify() {
    return SimplifiedPTerm.not(term);
  }

}
