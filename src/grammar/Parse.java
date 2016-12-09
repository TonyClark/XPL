package grammar;

import java.util.HashSet;

import context.TerminalSet;
import env.Env;
import exp.BoaConstructor;
import machine.Machine;
import values.Value;

@BoaConstructor(fields = { "path" })
public class Parse extends PTerm {

	public String path;

	public Parse() {
	}

	public PTerm subst(PTerm term, String extensionPoint) {
		return this;
	}

	public PTerm close(Env<String, Value> env) {
		return this;
	}

	public TerminalSet predictors(Env<String, Value> env, HashSet<String> NTs) {
		TerminalSet Ts = new TerminalSet();
		return Ts;
	}

	public void exec(Machine machine) {
		try {
			Class<?> c = Class.forName(path);
			Object o = c.newInstance();
			if (Parser.class.isInstance(o)) {
				Parser parser = (Parser) o;
				machine.pushValue(parser.parse(machine));
			} else throw new Error("Illegal parser " + path);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String pprint(int opPrec) {
		return "'PARSE'";
	}

	private static boolean needsProtection(char c) {
		return false;
	}

}
