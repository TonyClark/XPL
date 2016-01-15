package grammar;

import java.util.HashSet;
import java.util.Hashtable;

import machine.Machine;
import machine.instrs.Cons;
import machine.instrs.Nil;
import values.Value;
import context.TerminalSet;
import env.Env;
import exp.BoaConstructor;

@BoaConstructor(fields = { "term" })
public class Star extends PTerm {

  public PTerm term;

  public Star() {
  }

  public Star(PTerm term) {
	super();
	this.term = term;
  }

  public PTerm close(Env<String, Value> env) {
	term = term.close(env);
	if (isLiteral()) return getLiteral();
	return new Star(term);
  }

  public boolean isLiteral() {
	return term instanceof CharChoice;
  }

  public PTerm getLiteral() {
	CharChoice c = (CharChoice) term;
	return new StarChars(c.chars);
  }

  public TerminalSet predictors(Env<String, Value> env, HashSet<String> NTs) {
	return term.predictors(env, NTs);
  }

  public void exec(Machine machine) {
	machine.choice(new Nil());
	machine.pushInstr(Cons.CONS);
	machine.pushInstr(new Star(term));
	machine.pushInstr(term);
  }

  public String pprint(int opPrec) {
	if (opPrec < STAROP)
	  return "(" + term.pprint(STAROP) + "*)";
	else return term.pprint(STAROP) + "*";
  }

}
