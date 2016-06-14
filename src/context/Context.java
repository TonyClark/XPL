package context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Stack;
import java.util.Vector;

import env.Env;
import grammar.Grammar;
import grammar.Rule;
import values.Value;

public class Context extends Value {

	private static int						errPtr		= -1;
	private static Vector<String>	errMsgs		= new Vector<String>();

	public static int getErrPtr() {
		return errPtr;
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

	public static void reset() {
		errPtr = -1;
		errMsgs = new Vector<String>();
	}

	public static void setErrPtr(int errPtr) {
		Context.errPtr = errPtr;
	}

	private Stack<Integer>	lines;

	Grammar									grammar;
	Env<String, Value>			env;

	CharSource							text;

	int											textPtr;

	Value										value;

	int											indent	= 0;

	public Context(Grammar grammar, Env<String, Value> env, Stack<Integer> lines, CharSource text, int textPtr, Value value, int indent) {
		super();
		this.grammar = grammar;
		this.env = env;
		this.lines = lines;
		this.text = text;
		this.textPtr = textPtr;
		this.value = value;
		this.indent = indent;
	}

	public Context bind(String name, Value value) {
		env = env.bind(name, value);
		return this;
	}

	public boolean binds(String name) {
		return env.binds(name);
	}

	public boolean callFails(Context copy, Rule rule) {
		HashSet<String> Ts = rule.predictors(new HashSet<String>());
		return error(copy, " expecting " + Ts);
	}

	public void consume() {
		textPtr++;
	}

	public Context copy() {
		return new Context(grammar, env, (Stack<Integer>) lines.clone(), text, textPtr, value, indent);
	}

	public void debug(String message) {
		System.out.print(message);
	}

	public void dedent() {
		indent = indent - 2;
	}

	public boolean EOF() {
		return text.EOF(textPtr);
	}

	public boolean error(Context context, String msg) {
		if (context.nextNonWhitePos(textPtr) == errPtr) {
			errPtr = context.textPtr;
			boolean found = false;
			for (String m : errMsgs)
				if (msg.equals(m)) found = true;
			if (!found) errMsgs.add(msg);
		}
		if (context.nextNonWhitePos(textPtr) > errPtr) {
			errPtr = context.textPtr;
			errMsgs = new Vector<String>();
			errMsgs.add(msg);
		}
		return false;
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

	public Env<String, Value> getEnv() {
		return env;
	}

	public ParseError getError() {
		return new ParseError(errMsgs,Math.min(errPtr, getText().length()),getText().getText());
	}

	public Grammar getGrammar() {
		return grammar;
	}

	public int getIndent() {
		return indent;
	}

	public int getLine() {
		if (lines.isEmpty())
			return 1;
		else return lines.peek();
	}

	public Stack<Integer> getLines() {
		return lines;
	}

	public Rule getRule(String name) {
		if (grammar.hasRule(name))
			return grammar.getRule(name);
		else if (binds(name)) {
			Value value = lookup(name);
			if (value instanceof values.Thunk) value = ((values.Thunk) value).force();
			if (value instanceof Rule)
				return (Rule) value;
			else if (value instanceof Grammar) {
				Grammar g = (Grammar) value;
				return g.getRule(g.start);
			} else throw new Error("Value " + value + " is not a rule named " + name);
		}
		throw new Error("Cannot find rule named " + name);
	}

	public CharSource getText() {
		return text;
	}

	public int getTextPtr() {
		return textPtr;
	}

	public Value getValue() {
		return value;
	}

	public boolean hasPrefix(String prefix) {
		return text.hasPrefix(textPtr, prefix);
	}

	public boolean hasRule(String name) {
		return grammar.hasRule(name) || env.binds(name);
	}

	public void indent() {
		indent = indent + 2;
	}

	private boolean isNewLine(char c) {
		return c == '\n';
	}

	private boolean isWhiteSpace(char c) {
		return c == ' ' || c == '\n' || c == '\r' || c == '\t';
	}

	public int linePos() {
		int linePos = 1;
		for (int i = 0; i < textPtr; i++)
			if (isNewLine(text.charAt(i))) linePos++;
		return linePos;

	}

	public Value lookup(String name) {
		return env.lookup(name);
	}

	public void newline() {
		System.out.println();
		tab();
	}

	public int next() {
		return text.charAt(textPtr++);
	}

	private int nextNonWhitePos(int ptr) {
		while (ptr < text.length() && isWhiteSpace(text.charAt(ptr)))
			ptr++;
		return ptr;
	}

	public char peek() {
		return text.charAt(textPtr);
	}

	public void popLinePos() {
		lines.pop();
	}

	public void pushLinePos() {
		lines.push(linePos());
	}

	public void receive(Context other) {
		env = other.env;
		text = other.text;
		textPtr = other.textPtr;
		value = other.value;
	}

	public void setEnv(Env<String, Value> env) {
		this.env = env;
	}

	public void setGrammar(Grammar grammar) {
		this.grammar = grammar;
	}

	public void setIndent(int indent) {
		this.indent = indent;
	}

	public void setLines(Stack<Integer> lines) {
		this.lines = lines;
	}

	public void setText(CharSource text) {
		this.text = text;
	}

	public void setTextPtr(int textPtr) {
		this.textPtr = textPtr;
	}

	public void setValue(Value value) {
		this.value = value;
	}

	public void skipWhiteSpace() {
		while (!EOF() && isWhiteSpace(peek()))
			consume();
	}

	public void tab() {
		for (int i = 0; i < indent; i++)
			System.out.print(" ");
	}

	public String toString() {
		return "<context>";
	}

	public void update(String name, Value value) {
		env.set(name, value);
	}

}
