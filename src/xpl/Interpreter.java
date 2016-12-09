package xpl;

import java.util.Hashtable;
import java.util.Stack;

import commands.Command;
import context.CharSource;
import context.Context;
import context.TopLevel;
import env.Env;
import exp.Exp;
import exp.Import;
import grammar.Call;
import grammar.Grammar;
import machine.Machine;
import values.Record;
import values.Value;

public class Interpreter {

	// This class provides an entry point to a top-level interpreter for XPL and
	// some
	// useful language loading operations...

	// A cache of grammars for languages in the case that the same language is
	// used
	// multiple times...

	static Hashtable<String, Grammar> languages = new Hashtable<String, Grammar>();

	public static void main(String[] args) {
		Env<String, Value> env = Value.builtinEnv;
		Machine machine = new Machine(null, env, new Stack<Integer>(), new TopLevel(System.in), 0, null, 0);
		XPL.XPL = (Grammar) XPL.XPL.eval(machine);
		Record xpl = Import.getFile("../xpl/src/xpl/xpl.xpl");
		XPL.setXPL((Grammar) xpl.dot("XPL"));
		if (args.length == 2) XPL.setXPL(readLanguage(machine, args[0], args[1]));
		boolean quit = false;
		while (!quit) {
			try {
				Context.reset();
				machine = new Machine(XPL.XPL, env, new Stack<Integer>(), new TopLevel(System.in), 0, null, 0);
				machine.bind("XPL", XPL.XPL);
				System.out.print("> ");
				machine.pushInstr(new Call("topLevelCommand"));
				Command command = (Command) machine.run();
				if (machine.isOk())
					quit = command.perform(machine);
				else System.out.println(machine.getError());
				env = machine.getEnv();
				XPL.setXPL((Grammar) machine.lookup("XPL"));
			} catch (Error e) {
				e.printStackTrace();
				System.out.println("error: " + e.getCause());
			}
		}
		System.out.println("...terminated");
	}

	public static Grammar readLanguage(String languagePath, String languageName) {
		Grammar grammar = null;
		if (languages.containsKey(languageName))
			grammar = languages.get(languageName);
		else {
			Env<String, Value> env = Value.builtinEnv;
			Machine machine = new Machine(null, env, new Stack<Integer>(), new TopLevel(System.in), 0, null, 0);
			XPL.XPL = (Grammar) XPL.XPL.eval(machine);
			Record xpl = Import.getFile("../xpl/src/xpl/xpl.xpl");
			XPL.setXPL((Grammar) xpl.dot("XPL"));
			grammar = readLanguage(machine, languagePath, languageName);
			languages.put(languageName, grammar);
		}
		return grammar;
	}

	public static Object readFile(String languagePath, String languageName, String filePath, String startNT, Exp... args) {
		// Call this to read a file in a given language. It assumes that XPL needs
		// to be read first, then the grammar for the language and then the file...
		Grammar grammar = getGrammar(languagePath, languageName);
		// rammar.setDebug(true);
		return parseCharSource(filePath, grammar, startNT, Context.readFile(filePath), args, true);
	}

	public static Grammar getGrammar(String languagePath, String languageName) {
		if (languages.containsKey(languageName))
			return languages.get(languageName);
		else {
			Grammar grammar = null;
			Env<String, Value> env = Value.builtinEnv;
			Machine machine = new Machine(null, env, new Stack<Integer>(), new TopLevel(System.in), 0, null, 0);
			XPL.XPL = (Grammar) XPL.XPL.eval(machine);
			Record xpl = Import.getFile("../xpl/src/xpl/xpl.xpl");
			XPL.setXPL((Grammar) xpl.dot("XPL"));
			grammar = readLanguage(machine, languagePath, languageName);
			languages.put(languageName, grammar);
			return grammar;
		}
	}

	public static Object parseCharSource(String filePath, Grammar grammar, String startNT, CharSource chars, Exp[] args, boolean verbose) {
		Machine machine = new Machine(grammar, Value.builtinEnv, new Stack<Integer>(), chars, 0, null, 0);
		machine.pushInstr(new Call(startNT, args));
		Machine.reset();
		long start = System.currentTimeMillis();
		if (verbose) System.out.print("[" + filePath);
		Value value = machine.run();
		if (verbose) System.out.println(" " + (System.currentTimeMillis() - start) + " ms," + machine.getMaxFailDepth() + "]");
		if (machine.isOk()) {
			return value;
		} else throw machine.getError();
	}

	private static Grammar readLanguage(Machine machine, String file, String languageName) {
		values.Record record = Import.getFile(file);
		for (values.Field field : record.fields)
			machine.bind(field.name, field.value);
		return (Grammar) record.getField(languageName).value;
	}

}
