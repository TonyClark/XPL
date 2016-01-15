package grammar;

import java.util.HashSet;
import java.util.Hashtable;

import machine.Machine;

import env.Concat;
import env.Env;
import exp.BoaConstructor;
import exp.Exp;
import values.Value;
import context.Context;
import context.TerminalSet;

@BoaConstructor(fields = { "env", "exp" })
public class Closure extends PTerm {

  Env<String, Value> env;
  Exp                exp;

  public Closure(Env<String, Value> env, Exp exp) {
	super();
	this.env = env;
	this.exp = exp;
  }

  public PTerm close(Env<String, Value> env) {
	return this;
  }

  public String toString() {
	return "<pclosure>";
  }

  public TerminalSet predictors(Env<String, Value> env, HashSet<String> NTs) {
	return new TerminalSet();
  }

  public void exec(Machine machine) {
	Context copy = machine.copy();
	Env<String, Value> e = new Concat<String, Value>(copy.getEnv(), env);
	copy.setEnv(e);
	machine.pushValue(exp.eval(copy));
  }

  public String pprint(int opPrec) {
	return "{" + exp.pprint(Exp.MAXOP) + "}";
  }

}
