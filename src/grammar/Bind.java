package grammar;

import java.util.HashSet;
import java.util.Hashtable;

import machine.Machine;
import patterns.Pattern;
import values.Value;
import context.TerminalSet;
import env.Env;
import exp.BoaConstructor;

@BoaConstructor(fields = { "pattern", "term" })
public class Bind extends PTerm {

  public Pattern pattern;
  public PTerm   term;

  public Bind() {
  }

  public Bind(Pattern pattern, PTerm term) {
	super();
	this.pattern = pattern;
	this.term = term;
  }

  public Bind(String name, PTerm term) {
	this(new patterns.Var(name), term);
  }

  public PTerm close(Env<String, Value> env) {
	return new Bind(pattern, term.close(env));
  }

  public TerminalSet predictors(Env<String, Value> env, HashSet<String> NTs) {
	return term.predictors(env, NTs);
  }

  public void exec(Machine machine) {
	machine.pushInstr(new machine.instrs.Bind(pattern));
	machine.pushInstr(term);
  }

  public String pprint(int opPrec) {
	if (opPrec <= BINDOP)
	  return "(" + pattern.pprint() + "=" + term.pprint(BINDOP) + ")";
	else return pattern.pprint() + "=" + term.pprint(BINDOP);
  }
}
