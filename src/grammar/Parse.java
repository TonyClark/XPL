package grammar;

import java.util.HashSet;
import java.util.Vector;

import context.TerminalSet;
import env.Env;
import exp.BoaConstructor;
import grammar.simple.SimplifiedPTerm;
import machine.Machine;
import values.Value;

@BoaConstructor(fields = { "path" })
public class Parse extends PTerm {

  public String path;

  public Parse() {
  }

  public PTerm subst(PTerm term, String extensionPoint) {
    return this;
  }

  public PTerm close(Env<String, Value> env) {
    return this;
  }

  public TerminalSet predictors(Env<String, Value> env, HashSet<String> NTs) {
    TerminalSet Ts = new TerminalSet();
    return Ts;
  }

  public void exec(Machine machine) {
    try {
      Class<?> c = Class.forName(path);
      Object o = c.newInstance();
      if (Parser.class.isInstance(o)) {
        Parser parser = (Parser) o;
        machine.pushValue(parser.parse(machine));
      } else throw new Error("Illegal parser " + path);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  public String pprint(int opPrec) {
    return "'PARSE'";
  }

  public Vector<Vector<SimplifiedPTerm>> simplify() {
    return SimplifiedPTerm.parse(path);
  }

}
