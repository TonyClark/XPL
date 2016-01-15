package grammar;

import java.util.HashSet;
import java.util.Hashtable;

import machine.Machine;
import values.Value;
import context.TerminalSet;
import env.Env;
import exp.BoaConstructor;

@BoaConstructor(fields = { "value" })
public class Const extends PTerm {

  public Value value;

  public Const() {
	super();
  }

  public Const(Value value) {
	super();
	this.value = value;
  }

  public PTerm close(Env<String, Value> env) {
	return this;
  }

  public TerminalSet predictors(Env<String, Value> env, HashSet<String> NTs) {
	return new TerminalSet();
  }

  public String pprint(int opPrec) {
	return value.toString();
  }

  public void exec(Machine machine) {
	machine.pushValue(value);
  }
}
