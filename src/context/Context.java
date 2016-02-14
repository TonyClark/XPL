package context;

import env.Env;
import grammar.Grammar;
import grammar.Rule;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Vector;

import values.Value;

public class Context extends Value {

	private static int				ERRBACKUP	= 160;

	private static int				errPtr		= -1;
	private static Vector<String>	errMsgs		= new Vector<String>();

	Grammar							grammar;
	Env<String, Value>				env;
	CharSource						text;
	int								textPtr;
	Value							value;
	int								indent		= 0;

	public Context(Grammar grammar, Env<String, Value> env, CharSource text, int textPtr, Value value, int indent) {
		super();
		this.grammar = grammar;
		this.env = env;
		this.text = text;
		this.textPtr = textPtr;
		this.value = value;
		this.indent = indent;
	}

	public void indent() {
		indent = indent + 2;
	}

	public void dedent() {
		indent = indent - 2;
	}

	public void tab() {
		for (int i = 0; i < indent; i++)
			System.out.print(" ");
	}

	public void newline() {
		System.out.println();
		tab();
	}

	public void debug(String message) {
		System.out.print(message);
	}

	public int getIndent() {
		return indent;
	}

	public void setIndent(int indent) {
		this.indent = indent;
	}

	public static void reset() {
		errPtr = -1;
		errMsgs = new Vector<String>();
	}

	public String getError() {
		String str = text.getText();
		int linePos = 0;
		int start = Math.max(0, errPtr - ERRBACKUP);
		String error = "ERROR: ";
		for (String msg : errMsgs)
			error = error + msg + "\n       ";
		error = error + '\n';
		for (int i = start; i < Math.min(errPtr, str.length()); i++) {
			char c = str.charAt(i);
			if (c == '\n')
				linePos = 0;
			else
				linePos++;
			error = error + c;
		}
		error = error + '\n';
		for (int i = 0; i < linePos; i++)
			error = error + ' ';
		error = error + "^\n";
		for (int i = 0; i < linePos; i++)
			error = error + ' ';
		error = error + "|\n";
		return error;
	}

	public Value getValue() {
		return value;
	}

	public void setValue(Value value) {
		this.value = value;
	}

	public Env<String, Value> getEnv() {
		return env;
	}

	public void setEnv(Env<String, Value> env) {
		this.env = env;
	}

	public Grammar getGrammar() {
		return grammar;
	}

	public void setGrammar(Grammar grammar) {
		this.grammar = grammar;
	}

	public void setText(CharSource text) {
		this.text = text;
	}

	public CharSource getText() {
		return text;
	}

	public int getTextPtr() {
		return textPtr;
	}

	public void setTextPtr(int textPtr) {
		this.textPtr = textPtr;
	}

	public Context bind(String name, Value value) {
		env = env.bind(name, value);
		return this;
	}

	public Context copy() {
		return new Context(grammar, env, text, textPtr, value, indent);
	}

	public Value lookup(String name) {
		return env.lookup(name);
	}

	public void receive(Context other) {
		env = other.env;
		text = other.text;
		textPtr = other.textPtr;
		value = other.value;
	}

	public char peek() {
		return text.charAt(textPtr);
	}

	public void consume() {
		textPtr++;
	}

	public boolean EOF() {
		return text.EOF(textPtr);
	}

	public int next() {
		return text.charAt(textPtr++);
	}

	public boolean hasRule(String name) {
		return grammar.hasRule(name) || env.binds(name);
	}

	public Rule getRule(String name) {
		if (grammar.hasRule(name))
			return grammar.getRule(name);
		else if (binds(name)) {
			Value value = lookup(name);
			if (value instanceof values.Thunk)
				value = ((values.Thunk) value).force();
			if (value instanceof Rule)
				return (Rule) value;
			else if (value instanceof Grammar) {
				Grammar g = (Grammar) value;
				return g.getRule(g.start);
			} else
				throw new Error("Value " + value + " is not a rule named " + name);
		}
		throw new Error("Cannot find rule named " + name);
	}

	public String fieldValue(Field field) {
		try {
			Object value = field.get(this);
			return valueString(value);
		} catch (IllegalArgumentException e) {
			return "<ACCESS VIOLATION>";
		} catch (IllegalAccessException e) {
			return "<ACCESS VIOLATION>";
		}

	}

	public boolean hasPrefix(String prefix) {
		return text.hasPrefix(textPtr, prefix);
	}

	public void skipWhiteSpace() {
		while (!EOF() && isWhiteSpace(peek()))
			consume();
	}

	private boolean isWhiteSpace(char c) {
		return c == ' ' || c == '\n' || c == '\r' || c == '\t';
	}

	public static CharSource readFile(String name) {
		String s = "";
		try {
			FileInputStream sin = new FileInputStream(name);
			int i = 0;
			while ((i = sin.read()) != -1) {
				s = s + (char) i;
			}
			sin.close();
		} catch (FileNotFoundException e) {
			System.out.println("read file: " + name);
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("read file: " + name);
			e.printStackTrace();
		}
		return new StringSource(s);
	}

	public void update(String name, Value value) {
		env.set(name, value);
	}

	public String toString() {
		return "<context>";
	}

	public boolean error(Context context, String msg) {
		if (context.nextNonWhitePos(textPtr) == errPtr) {
			errPtr = context.textPtr;
			boolean found = false;
			for (String m : errMsgs)
				if (msg.equals(m))
					found = true;
			if (!found)
				errMsgs.add(msg);
		}
		if (context.nextNonWhitePos(textPtr) > errPtr) {
			errPtr = context.textPtr;
			errMsgs = new Vector<String>();
			errMsgs.add(msg);
		}
		return false;
	}

	private int nextNonWhitePos(int ptr) {
		while (ptr < text.length() && isWhiteSpace(text.charAt(ptr)))
			ptr++;
		return ptr;
	}

	public boolean callFails(Context copy, Rule rule) {
		HashSet<String> Ts = rule.predictors(new HashSet<String>());
		return error(copy, " expecting " + Ts);
	}

	public static int getErrPtr() {
		return errPtr;
	}

	public static void setErrPtr(int errPtr) {
		Context.errPtr = errPtr;
	}

	public boolean binds(String name) {
		return env.binds(name);
	}

}
