package grammar;

import java.util.HashSet;
import java.util.Hashtable;

import machine.Machine;

import env.Env;
import exp.BoaConstructor;
import exp.Exp;
import exp.PClosure;
import values.Value;
import context.Context;
import context.TerminalSet;

@BoaConstructor(fields = { "start", "end", "grammar" })
public class PDelay extends PTerm {

  public String start;
  public String end;
  public Exp    grammar;

  public PDelay() {
  }

  public PDelay(String start, String end, Exp grammar) {
	super();
	this.start = start;
	this.end = end;
	this.grammar = grammar;
  }

  public PTerm close(Env<String, Value> env) {
	return this;
  }

  public String getText(Context context) {
	for (int i = 0; i < start.length(); i++)
	  context.consume();
	String s = "";
	int endCount = 1;
	while (endCount > 0 && !context.EOF()) {
	  if (context.hasPrefix(end))
		endCount--;
	  else if (context.hasPrefix(start)) endCount++;
	  if (endCount != 0) {
		s = s + context.peek();
		context.consume();
	  }
	}
	for (int i = 0; i < end.length(); i++)
	  context.consume();
	return s;
  }

  public TerminalSet predictors(Env<String, Value> env, HashSet<String> NTs) {
	TerminalSet Ts = new TerminalSet();
	Ts.add(start);
	return Ts;
  }

  public void exec(Machine machine) {
	machine.skipWhiteSpace();
	if (machine.hasPrefix(start)) {
	  String s = getText(machine);
	  Exp g = (Exp) grammar.eval(machine);
	  machine.pushValue(new PClosure(s, g));
	} else {
	  machine.error(machine, "expecting " + start);
	  machine.fail(false);
	}

  }

  public String pprint(int opPrec) {
	return "PDELAY('" + start + "','" + end + "'," + grammar.pprint(Exp.MAXOP) + ")";
  }

}
