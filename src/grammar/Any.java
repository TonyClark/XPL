package grammar;

import java.util.HashSet;
import java.util.Hashtable;

import machine.Machine;
import values.Value;
import context.TerminalSet;
import env.Env;

public class Any extends PTerm {

  public PTerm close(Env<String, Value> env) {
	return this;
  }

  public TerminalSet predictors(Env<String, Value> env, HashSet<String> NTs) {
	return new TerminalSet();
  }

  public String pprint(int opPrec) {
	return "Any";
  }

  public void exec(Machine machine) {
	machine.pushValue(null);
  }

}
