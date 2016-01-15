package grammar;

import java.util.HashSet;
import java.util.Hashtable;

import machine.Machine;
import values.Value;
import context.TerminalSet;
import env.Env;
import exp.BoaConstructor;

@BoaConstructor(fields = { "term" })
public class Plus extends PTerm {

  public PTerm term;

  public Plus() {
  }

  public Plus(PTerm term) {
	super();
	this.term = term;
  }

  public PTerm close(Env<String, Value> env) {
	return new Plus(term.close(env));
  }

  public TerminalSet predictors(Env<String, Value> env, HashSet<String> NTs) {
	return term.predictors(env, NTs);
  }

  public void exec(Machine machine) {
	machine.pushInstr(new machine.instrs.Cons());
	machine.pushInstr(new Star(term));
	machine.pushInstr(term);
  }

  public String pprint(int opPrec) {
	if (opPrec < PLUSOP)
	  return "(" + term.pprint(PLUSOP) + "+)";
	else return term.pprint(PLUSOP) + "+";
  }
}
