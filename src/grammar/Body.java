package grammar;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;

import patterns.Pattern;
import values.Value;
import context.Context;
import context.TerminalSet;
import env.Empty;
import env.Env;
import exp.BoaConstructor;

@BoaConstructor(fields = { "args", "body" })
public class Body extends Value {

  public Pattern[]          args;
  public PTerm              body;

  public Env<String, Value> env     = new Empty<String, Value>();
  public Grammar            grammar = null;

  public Body() {
  }

  public Body(PTerm body, Pattern... args) {
	super();
	this.args = args;
	this.body = body;
  }

  public Body(PTerm body, Grammar grammar, Pattern... args) {
	super();
	this.args = args;
	this.body = body;
	this.grammar = grammar;
  }

  public Grammar getGrammar() {
	return grammar;
  }

  public void setGrammar(Grammar grammar) {
	this.grammar = grammar;
  }

  public void setEnv(Env<String, Value> env) {
	this.env = env;
  }

  public Env<String, Value> getEnv() {
	return env;
  }

  public Pattern[] getArgs() {
	return args;
  }

  public void setArgs(Pattern[] args) {
	this.args = args;
  }

  public PTerm getBody() {
	return body;
  }

  public void setBody(PTerm body) {
	this.body = body;
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

  public Body close(Env<String, Value> env) {
	Body rule = new Body(body.close(env), grammar, args);
	rule.setEnv(env);
	return rule;
  }

  public TerminalSet predictors(HashSet<String> NTs) {
	return body.predictors(env, NTs);
  }

  public String toString() {
	return "<body>";
  }

  public boolean match(Value[] values, Context c) {
	boolean matched = true;
	if (args.length == values.length) {
	  for (int i = 0; i < args.length && matched; i++) {
		matched = args[i].match(values[i], c);
	  }
	}
	return matched;
  }

  public String pprint() {
	String s = "";
	if (args.length != 0) {
	  s = s + "(";
	  for (Pattern arg : args) {
		s = s + arg.pprint();
		if (arg != args[args.length - 1]) s = s + ",";
	  }
	  s = s + ")";
	}
	s = s + " -> ";
	s = s + body.pprint(PTerm.MAXOP);
	return s;
  }

  public String formalArgs() {
	String s = "(";
	for (Pattern p : args) {
	  s = s + p.pprint();
	  if (p != args[args.length - 1]) s = s + ",";
	}
	return s + ")";
  }
}
