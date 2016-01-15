package grammar;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;

import machine.Machine;
import values.Value;
import context.TerminalSet;
import env.Env;

public class TermChoice extends PTerm {

  String[] terms;

  public TermChoice(String[] terms) {
	super();
	this.terms = terms;
  }

  public PTerm close(Env<String, Value> env) {
	return this;
  }

  public TerminalSet predictors(Env<String, Value> env, HashSet<String> NTs) {
	TerminalSet Ts = new TerminalSet();
	for (String s : terms)
	  Ts.add(s);
	return Ts;
  }

  public String pprint(int opPrec) {
	return Arrays.toString(terms) + "?";
  }

  public void exec(Machine machine) {
	machine.skipWhiteSpace();
	boolean found = false;
	for (int i = 0; i < terms.length && !found; i++) {
	  String term = terms[i];
	  if (machine.hasPrefix(term)) {
		found = true;
		for (int j = 0; j < term.length(); j++)
		  machine.consume();
		machine.pushValue(new values.Str(term));
	  }
	}
	if (!found) {
	  machine.error(machine, "expecting " + Arrays.toString(terms));
	  machine.fail(false);
	}
  }

}
