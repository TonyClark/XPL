package machine;

import grammar.Grammar;

import java.util.Stack;

import machine.instrs.Instr;

import values.Printable;
import values.Value;
import env.Env;
import exp.BoaConstructor;

@BoaConstructor(fields = { "code", "succ", "grammar", "env" })
public class Succ extends Printable {

	public Stack<Instr>			code	= new Stack<Instr>();
	private Stack<Fail>			cut		= new Stack<Fail>();
	public Succ					succ	= null;
	public Grammar				grammar;
	public Env<String, Value>	env;
	public Stack<Value>			values	= new Stack<Value>();
	private int					indent	= 0;

	public Succ(Stack<Instr> code, Stack<Fail> cut, Succ succ, Grammar grammar, Env<String, Value> env, Stack<Value> values,int indent) {
		super();
		this.code = code;
		this.cut = cut;
		this.succ = succ;
		this.grammar = grammar;
		this.env = env;
		this.values = values;
		this.indent = indent;
	}

	public Stack<Fail> getCut() {
		return cut;
	}

	public void setCut(Stack<Fail> cut) {
		this.cut = cut;
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

	public int getIndent() {
		return indent;
	}

	public void setIndent(int indent) {
		this.indent = indent;
	}

}
