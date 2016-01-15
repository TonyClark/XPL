package grammar;

import java.util.HashSet;
import java.util.Hashtable;

import machine.Machine;
import values.Value;
import context.TerminalSet;
import env.Env;
import exp.BoaConstructor;
import exp.Exp;

@BoaConstructor(fields = { "exp" })
public class Action extends PTerm {

  public Exp exp;

  public Action() {
  }

  public Action(Exp exp) {
	super();
	this.exp = exp;
  }

  public PTerm close(Env<String, Value> env) {
	return new Closure(env, exp);
  }

  public TerminalSet predictors(Env<String, Value> env, HashSet<String> Nts) {
	return new TerminalSet();
  }

  public void exec(Machine machine) {
	machine.pushValue(exp.eval(machine));
  }

  public String pprint(int opPrec) {
	return "{" + exp.pprint(Exp.MAXOP) + "}";
  }

}
