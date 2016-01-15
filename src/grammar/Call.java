package grammar;

import java.util.HashSet;
import java.util.Hashtable;

import machine.Machine;
import values.Thunk;
import values.Value;
import context.Context;
import context.TerminalSet;
import env.Env;
import exp.BoaConstructor;
import exp.Exp;

@BoaConstructor(fields = { "name", "args" })
public class Call extends PTerm {

  public String name;
  public Exp[]  args;

  private Rule  rule = null;

  public Call() {
  }

  public Call(String name, Exp... args) {
    super();
    this.name = name;
    this.args = args;
  }

  public boolean hasRule(Context context) {
    if (rule != null) return true;
    if (context.hasRule(name)) return true;
    if (context.binds(name)) {
      Value value = context.lookup(name);
      return value instanceof Rule || value instanceof Grammar || value instanceof Thunk;
    } else return false;
  }

  public Rule getRule(Context context) {
    if (rule != null) return rule;
    if (context.hasRule(name)) {
      rule = context.getRule(name);
      return rule;
    }
    if (context.binds(name)) {
      Value value = context.lookup(name);
      if (value instanceof Thunk) value = ((Thunk) value).force();
      if (value instanceof Rule) {
        rule = (Rule) value;
        return rule;
      }
      if (value instanceof Grammar) {
        Grammar grammar = (Grammar) value;
        rule = grammar.getRule(grammar.start);
        return rule;
      }
      throw new Error("Cannot find rule in " + value);
    } else throw new Error("Cannot find rule " + name);
  }

  public PTerm close(Env<String, Value> env) {
    return this;
  }

  public TerminalSet predictors(Env<String, Value> env, HashSet<String> NTs) {
    if (NTs.contains(name))
      return new TerminalSet();
    else {
      if (env.binds(name)) {
        Value value = env.lookup(name);
        if (value instanceof Rule) {
          Rule rule = (Rule) value;
          return rule.predictors(NTs);
        }
        return new TerminalSet();
      }
      return new TerminalSet();
    }
  }

  public void exec(Machine machine) {
    if (hasRule(machine)) {
      Value[] values = new Value[args.length];
      for (int i = 0; i < args.length; i++)
        values[i] = args[i].eval(machine);
      getRule(machine).call(machine, values);
    } else {
      machine.error(machine, "cannot find rule named " + name + " in " + machine.getEnv().dom());
      machine.fail(false);
    }
  }

  public String pprint(int opPrec) {
    if (opPrec <= CALLOP)
      return "(" + name + pprintArgs() + ")";
    else return name + pprintArgs() + "";
  }

  public String pprintArgs() {
    if (args.length == 0)
      return "";
    else {
      String s = "^(";
      for (Exp arg : args) {
        s = s + arg.pprint(MAXOP);
        if (arg != args[args.length - 1]) s = s + ",";
      }
      return s + ")";
    }
  }

}
