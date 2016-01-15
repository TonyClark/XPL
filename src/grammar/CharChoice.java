package grammar;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;

import machine.Machine;
import values.Value;
import context.TerminalSet;
import env.Env;

public class CharChoice extends PTerm {

  int[] chars;

  public CharChoice(int[] chars) {
	super();
	this.chars = chars;
  }

  public PTerm close(Env<String, Value> env) {
	return this;
  }

  public TerminalSet predictors(Env<String, Value> env, HashSet<String> NTs) {
	TerminalSet Ts = new TerminalSet();
	for (int c : chars)
	  Ts.add("" + (char) c);
	return Ts;
  }

  public String pprint(int opPrec) {
	return Arrays.toString(chars);
  }

  public void exec(Machine machine) {
	if (!machine.EOF()) {
	  int c = machine.peek();
	  if (isLegal(c)) {
		machine.consume();
		machine.pushValue(new values.Int(c));
	  } else {
		machine.error(machine, "expecting char '" + Arrays.toString(chars) + "' but got '" + machine.peek() + "'");
		machine.fail(false);
	  }
	} else {
	  machine.error(machine, "expecting char '" + Arrays.toString(chars) + "' but encountered EOF");
	  machine.fail(false);
	}
  }

  public boolean isLegal(int c) {
	for (int cc : chars)
	  if (c == cc) return true;
	return false;
  }

}
