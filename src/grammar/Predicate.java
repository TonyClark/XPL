package grammar;

import java.util.HashSet;
import java.util.Hashtable;

import machine.Machine;
import values.Value;
import context.TerminalSet;
import env.Env;
import exp.BoaConstructor;
import exp.Exp;

@BoaConstructor(fields = { "exp", "exps" })
public class Predicate extends PTerm {

  public Exp   exp;
  public Exp[] exps;

  public Predicate() {
  }

  public Predicate(Exp exp, Exp[] exps) {
	this.exp = exp;
	this.exps = exps;
  }

  public PTerm close(Env<String, Value> env) {
	return this;
  }

  public TerminalSet predictors(Env<String, Value> env, HashSet<String> NTs) {
	return new TerminalSet();
  }

  public void exec(Machine machine) {
	Value value = exp.eval(machine);
	if (value instanceof values.Closure) {
	  values.Closure closure = (values.Closure) value;
	  Value[] values = new Value[exps.length];
	  for (int i = 0; i < values.length; i++)
		values[i] = exps[i].eval(machine);
	  Value isOk = closure.apply(values);
	  if (isOk instanceof values.Bool) {
		values.Bool bool = (values.Bool) isOk;
		if (bool.value)
		  machine.pushValue(bool);
		else machine.fail(false);
	  } else if (isOk instanceof values.Str) {
		values.Str b = (values.Str) isOk;
		if (b.value.length() == 0) {
		  machine.pushValue(null);
		} else {
		  machine.error(machine, b.value);
		  machine.fail(false);
		}
	  } else throw new Error("Expecting predicate to return a string: " + isOk);
	} else throw new Error("Not a predicate: " + value);
  }

  public String pprint(int opPrec) {
	if (opPrec < PREDOP)
	  return "(?" + exp.pprint(Exp.MAXOP) + exps + ")";
	else return "?" + exp.pprint(Exp.MAXOP) + exps;
  }

}
