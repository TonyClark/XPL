package grammar;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Vector;

import context.Context;
import context.TerminalSet;
import env.Env;
import exp.BoaConstructor;
import grammar.simple.SimplifiedGrammar;
import grammar.simple.SimplifiedPTerm;
import machine.Machine;
import patterns.Pattern;
import values.Value;

@BoaConstructor(fields = { "name", "bodies" })
public class Rule extends Value {

  public String   name;
  public Body[]   bodies;

  private boolean debug   = false;
  private Grammar grammar = null;

  public Rule() {
  }

  public Rule(Grammar grammar, String name, PTerm body, Pattern... args) {
    this(grammar, name, new Body(body, args));
  }

  public Rule(String name, PTerm body, Pattern... args) {
    this(null, name, new Body(body, args));
  }

  public Rule(Grammar grammar, String name, Body... bodies) {
    super();
    this.name = name;
    this.bodies = bodies;
    this.grammar = grammar;
  }

  public boolean isDebug() {
    return debug;
  }

  public void setDebug(boolean debug) {
    this.debug = debug;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Body[] getBodies() {
    return bodies;
  }

  public void setBodies(Body[] bodies) {
    this.bodies = bodies;
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

  public TerminalSet predictors(HashSet<String> NTs) {
    if (NTs.contains(name))
      return new TerminalSet();
    else {
      TerminalSet ts = new TerminalSet();
      NTs.add(name);
      for (Body body : bodies)
        ts.addAll(body.predictors(NTs));
      return ts;
    }
  }

  public String toString() {
    return "<rule:" + name + hashCode() + ">";
  }

  public Rule close(Env<String, Value> env) {
    Body[] newBodies = new Body[bodies.length];
    for (int i = 0; i < bodies.length; i++)
      newBodies[i] = bodies[i].close(env);
    return new Rule(grammar, name, newBodies);
  }

  public void setGrammar(Grammar grammar) {
    this.grammar = grammar;
    for (Body body : bodies)
      body.setGrammar(grammar);
  }

  public Grammar getGrammar() {
    return grammar;
  }

  public boolean grammarDebug() {
    if (getGrammar() != null)
      return getGrammar().isDebug();
    else return false;
  }

  public boolean traced() {
    return isDebug() || grammarDebug();
  }

  public void call(Machine machine, Value[] args) {
    if (traced())
      callTraced(machine, args);
    else callUntraced(machine, args);
  }

  public void callUntraced(Machine machine, Value[] args) {
    for (int i = bodies.length - 1; i >= 0; i--) {
      Body body = bodies[i];
      Env<String, Value> env = body.env;
      Context c = machine.copy();
      c.setEnv(env);
      if (body.match(args, c)) {
        machine.callChoice(body.body, body.grammar, c.getEnv());
      }
    }
    machine.enter();
  }

  public void callTraced(Machine machine, Value[] args) {
    machine.debug(traceCall(args));
    machine.indent();
    machine.newline();
    for (int i = bodies.length - 1; i >= 0; i--) {
      Body body = bodies[i];
      Env<String, Value> env = body.env;
      Context c = machine.copy();
      c.setEnv(env);
      if (body.match(args, c)) {
        machine.callTracedChoice(name, body.formalArgs(), body.body, body.grammar, c.getEnv());
      }
    }
    machine.enter();
  }

  public String traceCall(Value[] args) {
    String s = name + "(";
    for (Value v : args) {
      s = s + v;
      if (v != args[args.length - 1]) s = s + ",";
    }
    return s + ")";
  }

  public Rule add(Rule rule) {
    Body[] newBodies = new Body[bodies.length + rule.bodies.length];
    for (int i = 0; i < bodies.length; i++)
      newBodies[i] = bodies[i];
    for (int i = 0; i < rule.bodies.length; i++)
      newBodies[i + bodies.length] = rule.bodies[i];
    return new Rule(grammar, name, newBodies);
  }

  public String pprint() {
    String s = "";
    for (Body body : bodies) {
      s = s + name + body.pprint();
      if (body != bodies[bodies.length - 1]) s = s + ";\n  ";
    }
    return s;
  }

  public void simplify(SimplifiedGrammar g) {
    for (Body body : bodies) {
      for (Vector<SimplifiedPTerm> newBody : body.simplify()) {
        g.addRule(name, newBody);
      }
    }
  }

}
