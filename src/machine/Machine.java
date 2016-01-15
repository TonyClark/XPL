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

  private Stack<Fail>                               fails          = new Stack<Fail>();
  private Stack<Fail>                               cut            = new Stack<Fail>();
  private Stack<Instr>                              code           = new Stack<Instr>();
  private Succ                                      succ           = null;
  private boolean                                   ok             = true;
  private Stack<Value>                              values         = new Stack<Value>();
  private int                                       maxFailDepth   = 0;
  private int                                       numberOfFails  = 0;
  private int                                       numberOfInstrs = 0;
  private int                                       numberOfCalls  = 0;

  public Machine(Grammar grammar, Env<String, Value> env, CharSource text, int textPtr, Value value, int indent) {
	super(grammar, env, text, textPtr, value, indent);
  }

  public String stats() {
	return "[max fail depth=" + maxFailDepth + ", fails=" + numberOfFails + ", instrs=" + numberOfInstrs + ", calls=" + numberOfCalls + "]";
  }

  public int getNumberOfFails() {
	return numberOfFails;
  }

  public void setNumberOfFails(int numberOfFails) {
	this.numberOfFails = numberOfFails;
  }

  public int getNumberOfInstrs() {
	return numberOfInstrs;
  }

  public void setNumberOfInstrs(int numberOfInstrs) {
	this.numberOfInstrs = numberOfInstrs;
  }

  public int getNumberOfCalls() {
	return numberOfCalls;
  }

  public void setNumberOfCalls(int numberOfCalls) {
	this.numberOfCalls = numberOfCalls;
  }

  public int getMaxFailDepth() {
	return maxFailDepth;
  }

  public void setMaxFailDepth(int maxFailDepth) {
	this.maxFailDepth = maxFailDepth;
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

  public boolean isOk() {
	return ok;
  }

  public void setOk(boolean ok) {
	this.ok = ok;
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

  public Stack<Fail> getFails() {
	return fails;
  }

  public void setFails(Stack<Fail> fails) {
	this.fails = fails;
  }

  public void pushInstr(Instr instr) {
	code.push(instr);
  }

  public void pushValue(Value value) {
	values.push(value);
  }

  public Value topValue() {
	return values.peek();
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
	//System.out.println(stats());
	if (ok)
	  return values.pop();
	else return null;
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

  public boolean isTerminated() {
	return (code.isEmpty() && succ == null) || !ok;
  }

  @SuppressWarnings("unchecked")
  public void choice(Instr instr) {
	Stack<Instr> code = (Stack<Instr>) getCode().clone();
	code.push(instr);
	fails.push(new Fail(succ, getGrammar(), code, getEnv(), (Stack<Value>) values.clone(), getText(), getTextPtr(), getIndent()));
  }

  @SuppressWarnings("unchecked")
  public void callChoice(Instr instr, Grammar grammar, Env<String, Value> env) {
	Succ succ = new Succ((Stack<Instr>) getCode().clone(), (Stack<Fail>) getCut().clone(), getSucc(), getGrammar(), getEnv(), (Stack<Value>) values.clone(), getIndent());
	Stack<Instr> code = new Stack<Instr>();
	code.push(instr);
	fails.push(new Fail(succ, grammar, code, env, (Stack<Value>) values.clone(), getText(), getTextPtr(), getIndent()));
  }

  @SuppressWarnings("unchecked")
  public void callTracedChoice(String name, String args, Instr instr, Grammar grammar, Env<String, Value> env) {
	Stack<Instr> succCode = (Stack<Instr>) getCode().clone();
	Succ succ = new Succ(succCode, (Stack<Fail>) getCut().clone(), getSucc(), getGrammar(), getEnv(), (Stack<Value>) values.clone(), getIndent() - 2);
	Stack<Instr> code = new Stack<Instr>();
	code.push(instr);
	fails.push(new Fail(succ, grammar, code, env, (Stack<Value>) values.clone(), getText(), getTextPtr(), getIndent()));
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

  @SuppressWarnings("unchecked")
  public void choice() {
	Stack<Value> values = (Stack<Value>) getValues().clone();
	values.push(null);
	fails.push(new Fail(succ, getGrammar(), (Stack<Instr>) code.clone(), getEnv(), values, getText(), getTextPtr(), getIndent()));
  }

  public Value popValue() {
	return values.pop();
  }

  public boolean canFail() {
	return !fails.isEmpty();
  }

  public void cut() {
	setFails(getCut());
  }
}
