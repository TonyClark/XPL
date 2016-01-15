package grammar;

import java.util.HashSet;
import java.util.Hashtable;

import machine.Machine;
import values.Thunk;
import values.Value;
import context.TerminalSet;
import env.Env;
import exp.BoaConstructor;
import exp.Exp;

@BoaConstructor(fields = { "ruleExp", "args" })
public class EvalCall extends PTerm {

  public Exp   ruleExp;
  public Exp[] args;

  public EvalCall() {
  }

  public EvalCall(Exp rule, Exp... args) {
	super();
	this.ruleExp = rule;
	this.args = args;
  }

  public PTerm close(Env<String, Value> env) {
	return this;
  }

  public TerminalSet predictors(Env<String, Value> env, HashSet<String> NTs) {
	return new TerminalSet();
  }

  public void exec(Machine machine) {
	Rule rule;
	Value value = ruleExp.eval(machine);
	if (value instanceof Thunk) value = ((Thunk) value).force();
	if (value instanceof Rule)
	  rule = (Rule) value;
	else if (value instanceof Grammar) {
	  Grammar grammar = (Grammar) value;
	  rule = grammar.getRule(grammar.start);
	} else if (value instanceof values.Record) {
	  machine.fail(false);
	  return;
	} else throw new Error("Cannot call " + value);
	Value[] values = new Value[args.length];
	for (int i = 0; i < args.length; i++)
	  values[i] = args[i].eval(machine);
	rule.call(machine, values);
  }

  public String pprint(int opPrec) {
	if (opPrec <= CALLOP)
	  return "(<" + ruleExp.pprint(Exp.MAXOP) + ">^(" + args + "))";
	else return "<" + ruleExp.pprint(Exp.MAXOP) + ">^(" + args + ")";
  }

}
