package grammar;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Stack;
import java.util.Vector;

import context.Context;
import context.StringSource;
import context.TerminalSet;
import env.Empty;
import env.Env;
import exp.BoaConstructor;
import exp.Exp;
import exp.Var;
import grammar.simple.SimplifiedGrammar;
import machine.Machine;
import values.Value;

@BoaConstructor(fields = { "start", "rules" })
public class Grammar extends Exp {

  public String   start;
  public Rule[]   rules;

  private boolean debug = false;

  public Grammar() {
  }

  public Grammar(Rule... rules) {
    this(rules[0].getName(), rules);
  }

  public Grammar(String start, Rule... rules) {
    super();
    try {
      this.rules = mergeRules(rules, new Rule[0]);
      this.start = start.equals("") ? rules[0].getName() : start;
      //SimplifiedGrammar g = simplify();
      //System.out.println(g.getRules());
    } catch (Throwable t) {
      t.printStackTrace();
      throw new Error("STOP");
    }
  }

  public SimplifiedGrammar simplify() {

    // Return a grammar that consists of rules of the form:
    // X -> [[Y1 ... Yn],...]
    // where X is a non-terminal and Yi are:
    //
    // Call(X)
    // CharCode(c)
    // Dot()
    // Not(...)
    // Parse(path)
    // Term(t)
    //
    // All disjunctions are transformed to the top-level.

    SimplifiedGrammar g = new SimplifiedGrammar();
    for (Rule rule : rules) {
      rule.simplify(g);
    }
    return g;
  }

  public boolean isDebug() {
    return debug;
  }

  public Grammar setDebug(boolean debug) {
    this.debug = debug;
    return this;
  }

  public void mergeRules() {

  }

  public boolean hasRule(String name) {
    for (Rule rule : rules)
      if (rule.getName().equals(name)) return true;
    return false;
  }

  public Rule getRule(String name) {
    for (Rule rule : rules)
      if (rule.getName().equals(name)) return rule;
    throw new Error("No rule named " + name);
  }

  public Value dot(String name) {
    return getRule(name);
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

  public void addRule(Vector<Rule> rules, Rule rule2) {
    boolean added = false;
    for (int j = 0; j < rules.size() && !added; j++) {
      Rule rule1 = rules.elementAt(j);
      if (rule1.name.equals(rule2.name)) {
        if (rule1 == rule2)
          added = true;
        else {
          rules.set(j, rule1.add(rule2));
          added = true;
        }
      }
    }
    if (!added) rules.add(rule2);
  }

  public Rule[] mergeRules(Rule[] rules1, Rule[] rules2) {
    Vector<Rule> newRules = new Vector<Rule>();
    for (int i = 0; i < rules1.length; i++)
      addRule(newRules, rules1[i]);
    for (int i = 0; i < rules2.length; i++)
      addRule(newRules, rules2[i]);
    Rule[] rules = new Rule[newRules.size()];
    for (int i = 0; i < newRules.size(); i++)
      rules[i] = newRules.elementAt(i);
    return rules;
  }

  public Grammar add(Grammar grammar) {
    Grammar newGrammar = new Grammar();
    newGrammar.start = start;
    newGrammar.rules = mergeRules(rules, grammar.rules);
    return newGrammar;
  }

  public Value eval(Context context) {
    Rule[] newRules = new Rule[rules.length];
    Env<String, Value> env = context.getEnv();
    for (Rule rule : rules)
      env = env.bind(rule.name, null);
    for (int i = 0; i < rules.length; i++)
      newRules[i] = rules[i].close(env);
    for (int i = 0; i < rules.length; i++)
      env.set(newRules[i].name, newRules[i]);
    Grammar grammar = new Grammar(start, newRules);
    grammar.setDebug(debug);
    for (int i = 0; i < grammar.rules.length; i++)
      grammar.rules[i].setGrammar(grammar);
    // System.out.println(grammar.pprint());
    return grammar;
  }

  public Value parse(String s, Value... args) {
    return parse(s, start, args);
  }

  public Value parse(String s, String start, Value... args) {
    Context.reset();
    Env<String, Value> env = new Empty<String, Value>();
    Exp[] vars = new Exp[args.length];
    for (int i = 0; i < args.length; i++) {
      env = env.bind("v" + i, args[i]);
      vars[i] = new Var("v" + i);
    }
    Machine machine = new Machine(this, env, new Stack<Integer>(), new StringSource(s), 0, null, 0);
    machine.pushInstr(new Seq(new Bind(new patterns.Var("x"), new Call(start, vars)), new EOF(), new Action(new Var("x"))));
    Value value = machine.run();
    if (machine.isOk())
      return value;
    else throw new Error(machine.getError());
  }

  public TerminalSet predictors(String name, HashSet<String> NTs) {
    return getRule(name).predictors(NTs);
  }

  public String toString() {
    return "<grammar:" + start + ">";
  }

  public String pprint() {
    String s = "{ ";
    for (Rule r : rules) {
      s = s + r.pprint();
      if (r != rules[rules.length - 1]) s = s + ";\n  ";
    }
    return s + "}";

  }

  public String pprint(int opPrec) {
    return pprint();
  }
}
