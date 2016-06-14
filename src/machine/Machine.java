package machine;

import env.Env;
import grammar.Grammar;

import java.util.Hashtable;
import java.util.Stack;

import machine.instrs.Instr;
import values.Value;
import context.CharSource;
import context.Context;

public class Machine extends Context {

	private Stack<Fail>		fails						= new Stack<Fail>();
	private Stack<Fail>		cut							= new Stack<Fail>();
	private Stack<Instr>	code						= new Stack<Instr>();
	private Succ					succ						= null;
	private boolean				ok							= true;
	private Stack<Value>	values					= new Stack<Value>();
	private int						maxFailDepth		= 0;
	private int						numberOfFails		= 0;
	private int						numberOfInstrs	= 0;
	private int						numberOfCalls		= 0;

	public Machine(Grammar grammar, Env<String, Value> env, Stack<Integer> stack, CharSource text, int textPtr, Value value, int indent) {
		super(grammar, env, stack, text, textPtr, value, indent);
	}

	@SuppressWarnings("unchecked")
	public void callChoice(Instr instr, Grammar grammar, Env<String, Value> env) {
		Succ succ = new Succ((Stack<Instr>) getCode().clone(), (Stack<Fail>) getCut().clone(), getSucc(), getGrammar(), getEnv(), (Stack<Value>) values.clone(),
				getIndent());
		Stack<Instr> code = new Stack<Instr>();
		code.push(instr);
		fails.push(new Fail(succ, grammar, code, env, (Stack<Value>) values.clone(), (Stack<Integer>) getLines().clone(), getText(), getTextPtr(), getIndent()));
	}

	@SuppressWarnings("unchecked")
	public void callTracedChoice(String name, String args, Instr instr, Grammar grammar, Env<String, Value> env) {
		Stack<Instr> succCode = (Stack<Instr>) getCode().clone();
		Succ succ = new Succ(succCode, (Stack<Fail>) getCut().clone(), getSucc(), getGrammar(), getEnv(), (Stack<Value>) values.clone(), getIndent() - 2);
		Stack<Instr> code = new Stack<Instr>();
		code.push(instr);
		fails.push(new Fail(succ, grammar, code, env, (Stack<Value>) values.clone(), (Stack<Integer>) getLines().clone(), getText(), getTextPtr(), getIndent()));
	}

	public boolean canFail() {
		return !fails.isEmpty();
	}

	@SuppressWarnings("unchecked")
	public void choice() {
		Stack<Value> values = (Stack<Value>) getValues().clone();
		values.push(null);
		fails.push(
				new Fail(succ, getGrammar(), (Stack<Instr>) code.clone(), getEnv(), values, (Stack<Integer>) getLines().clone(), getText(), getTextPtr(), getIndent()));
	}

	@SuppressWarnings("unchecked")
	public void choice(Instr instr) {
		Stack<Instr> code = (Stack<Instr>) getCode().clone();
		code.push(instr);
		fails.push(
				new Fail(succ, getGrammar(), code, getEnv(), (Stack<Value>) values.clone(), (Stack<Integer>) getLines().clone(), getText(), getTextPtr(), getIndent()));
	}

	public void cut() {
		setFails(getCut());
	}

	@SuppressWarnings("unchecked")
	public void enter() {
		setCut((Stack<Fail>) getFails().clone());
		numberOfCalls++;
		fail(true);
	}

	@SuppressWarnings("unchecked")
	public void fail(boolean isEnter) {
		if (fails.isEmpty())
			ok = false;
		else {
			numberOfFails++;
			Fail fail = fails.pop();
			fail.action(this);
			setSucc(fail.getSucc());
			setGrammar(fail.getGrammar());
			setCode((Stack<Instr>) fail.getCode().clone());
			setEnv(fail.getEnv());
			setValues(fail.getValues());
			setLines(fail.getLines());
			setText(fail.getText());
			setTextPtr(fail.getTextPtr());
			setIndent(fail.getIndent());
			if (!isEnter && getGrammar().isDebug()) {
				debug("fail");
				newline();
			}
			setCut(fails);
		}
	}

	public Stack<Instr> getCode() {
		return code;
	}

	public Stack<Fail> getCut() {
		return cut;
	}

	public Stack<Fail> getFails() {
		return fails;
	}

	public int getMaxFailDepth() {
		return maxFailDepth;
	}

	public int getNumberOfCalls() {
		return numberOfCalls;
	}

	public int getNumberOfFails() {
		return numberOfFails;
	}

	public int getNumberOfInstrs() {
		return numberOfInstrs;
	}

	public Succ getSucc() {
		return succ;
	}

	public Stack<Value> getValues() {
		return values;
	}

	public boolean isOk() {
		return ok;
	}

	public boolean isTerminated() {
		return (code.isEmpty() && succ == null) || !ok;
	}

	@SuppressWarnings("unchecked")
	private void popCallStack() {
		code = (Stack<Instr>) succ.getCode().clone();
		Value value = popValue();
		setGrammar(succ.getGrammar());
		setCut(succ.getCut());
		setEnv(succ.getEnv());
		setValues(succ.getValues());
		setIndent(succ.getIndent());
		pushValue(value);
		setSucc(succ.getSucc());
		if (getGrammar().isDebug()) {
			debug("ok");
			newline();
			debug("return: " + value);
			newline();
		}
	}

	public Value popValue() {
		return values.pop();
	}

	public void pushInstr(Instr instr) {
		code.push(instr);
	}

	public void pushValue(Value value) {
		values.push(value);
	}

	public Value run() {
		while (!isTerminated()) {
			maxFailDepth = Math.max(maxFailDepth, fails.size());
			if (code.isEmpty())
				popCallStack();
			else {
				Instr instr = code.pop();
				numberOfInstrs++;
				instr.exec(this);
			}
		}
		// System.out.println(stats());
		if (ok)
			return values.pop();
		else return null;
	}

	public void setCode(Stack<Instr> code) {
		this.code = code;
	}

	public void setCut(Stack<Fail> cut) {
		this.cut = cut;
	}

	public void setFails(Stack<Fail> fails) {
		this.fails = fails;
	}

	public void setMaxFailDepth(int maxFailDepth) {
		this.maxFailDepth = maxFailDepth;
	}

	public void setNumberOfCalls(int numberOfCalls) {
		this.numberOfCalls = numberOfCalls;
	}

	public void setNumberOfFails(int numberOfFails) {
		this.numberOfFails = numberOfFails;
	}

	public void setNumberOfInstrs(int numberOfInstrs) {
		this.numberOfInstrs = numberOfInstrs;
	}

	public void setOk(boolean ok) {
		this.ok = ok;
	}

	public void setSucc(Succ succ) {
		this.succ = succ;
	}

	public void setValues(Stack<Value> values) {
		this.values = values;
	}

	public void skipLine() {
		while (!EOF() && peek() != 10)
			consume();
		while (!EOF() && (peek() == 10 || peek() == 13))
			consume();
	}

	public String stats() {
		return "[max fail depth=" + maxFailDepth + ", fails=" + numberOfFails + ", instrs=" + numberOfInstrs + ", calls=" + numberOfCalls + "]";
	}

	public Value topValue() {
		return values.peek();
	}
}
