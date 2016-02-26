package grammar;

import java.util.HashSet;
import java.util.Hashtable;

import machine.Machine;
import values.Value;
import context.TerminalSet;
import env.Env;
import exp.BoaConstructor;

@BoaConstructor(fields = { "term" })
public class Term extends PTerm {

	public String term;

	public Term() {
	}

	public Term(String term) {
		super();
		this.term = term;
	}

	public PTerm subst(PTerm term, String extensionPoint) {
		return this;
	}

	public PTerm close(Env<String, Value> env) {
		return this;
	}

	public TerminalSet predictors(Env<String, Value> env, HashSet<String> NTs) {
		TerminalSet Ts = new TerminalSet();
		Ts.add(term);
		return Ts;
	}

	public void exec(Machine machine) {
		machine.skipWhiteSpace();
		if (machine.hasPrefix(term)) {
			for (int i = 0; i < term.length(); i++)
				machine.consume();
			machine.pushValue(new values.Str(term));
		} else {
			machine.error(machine, "expecting " + term);
			machine.fail(false);
		}
	}

	public String pprint(int opPrec) {
		return "'" + protectChars(term) + "'";
	}

	public static String protectChars(String str) {
		String s = "";
		for (int i = 0; i < str.length(); i++) {
			if (needsProtection(str.charAt(i)))
				s = s + '\\' + str.charAt(i);
			else s = s + str.charAt(i);
		}
		return s;
	}

	private static boolean needsProtection(char c) {
		switch (c) {
			case '\\':
				return true;
			case '\'':
				return true;
			default:
				return false;
		}
	}

}
