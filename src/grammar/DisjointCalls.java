package grammar;

import java.util.HashSet;
import java.util.Hashtable;

import machine.Machine;
import values.Value;
import context.TerminalSet;
import env.Env;
import exp.BoaConstructor;
import exp.Var;

@BoaConstructor(fields = { "names" })
public class DisjointCalls extends PTerm {

  public String[] names;

  public DisjointCalls(String... names) {
	if (names.length < 1) throw new Error("Must call at least 1 rule");
	this.names = names;
  }

  public PTerm close(Env<String, Value> env) {
	return this;
  }

  public TerminalSet predictors(Env<String, Value> env, HashSet<String> NTs) {
	return desugar().predictors(env, NTs);
  }

  public void exec(Machine machine) {
	desugar().exec(machine);
  }

  public PTerm desugar() {
	return new Or(call(names[0]), calls(1));
  }

  public Seq call(String name) {
	return new Seq(new Bind(new patterns.Var("x"), new Call(name)), new Cut(), new Action(new Var("x")));
  }

  public PTerm calls(int i) {
	if (i == names.length - 1)
	  return call(names[i]);
	else return new Or(call(names[i]), calls(i + 1));
  }

  public String pprint(int opPrec) {
	return desugar().pprint(opPrec);
  }
}
