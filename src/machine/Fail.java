package machine;

import java.util.Stack;

import machine.instrs.Instr;

import values.Printable;
import values.Value;
import context.CharSource;
import env.Env;
import exp.BoaConstructor;
import grammar.Grammar;

@BoaConstructor(fields = { "succ", "grammar", "code", "env", "text", "textPtr", "indent" })
public class Fail extends Printable {

	public Succ					succ;
	public Grammar				grammar;
	public Stack<Instr>			code;
	public Env<String, Value>	env;
	public Stack<Value>			values;
	public CharSource			text;
	public int					textPtr;
	public int					indent;

	public Fail(Succ succ, Grammar grammar, Stack<Instr> code, Env<String, Value> env, Stack<Value> values, CharSource text, int textPtr, int indent) {
		super();
		this.succ = succ;
		this.grammar = grammar;
		this.code = code;
		this.env = env;
		this.values = values;
		this.text = text;
		this.textPtr = textPtr;
		this.indent = indent;
	}

	public Stack<Value> getValues() {
		return values;
	}

	public void setValues(Stack<Value> values) {
		this.values = values;
	}

	public Stack<Instr> getCode() {
		return code;
	}

	public void setCode(Stack<Instr> code) {
		this.code = code;
	}

	public Succ getSucc() {
		return succ;
	}

	public void setSucc(Succ succ) {
		this.succ = succ;
	}

	public Grammar getGrammar() {
		return grammar;
	}

	public void setGrammar(Grammar grammar) {
		this.grammar = grammar;
	}

	public Env<String, Value> getEnv() {
		return env;
	}

	public void setEnv(Env<String, Value> env) {
		this.env = env;
	}

	public CharSource getText() {
		return text;
	}

	public void setText(CharSource text) {
		this.text = text;
	}

	public int getTextPtr() {
		return textPtr;
	}

	public void setTextPtr(int textPtr) {
		this.textPtr = textPtr;
	}

	public String toString() {
		return "Fail(" + code + ")";
	}

	public int getIndent() {
		return indent;
	}

	public void setIndent(int indent) {
		this.indent = indent;
	}

	public void action(Machine machine) {
	}

}
