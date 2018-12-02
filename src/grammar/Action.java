package grammar;

import java.util.HashSet;
import java.util.Vector;

import context.TerminalSet;
import env.Env;
import exp.BoaConstructor;
import exp.Exp;
import grammar.simple.SimplifiedPTerm;
import machine.Machine;
import values.JavaObject;
import values.Located;
import values.Value;

@BoaConstructor(fields = { "exp" })
public class Action extends PTerm {

  public Exp exp;

  public Action() {
  }

  public Action(Exp exp) {
    super();
    this.exp = exp;
  }

  public PTerm close(Env<String, Value> env) {
    return new Closure(env, exp);
  }

  public TerminalSet predictors(Env<String, Value> env, HashSet<String> Nts) {
    return new TerminalSet();
  }

  public void exec(Machine machine) {
    Value v = exp.eval(machine);
    if (v instanceof JavaObject) {
      JavaObject jo = (JavaObject) v;
      if (jo.getTarget() instanceof Located) {
        Located l = (Located) jo.getTarget();
        if (l.getLineStart() == -1) {
          l.setLineStart(machine.getLines().peek());
          l.setLineEnd(machine.getTextPtr());
        }
      }
    }
    machine.pushValue(v);
  }

  public String pprint(int opPrec) {
    return "{" + exp.pprint(Exp.MAXOP) + "}";
  }

  public Vector<Vector<SimplifiedPTerm>> simplify() {
    return SimplifiedPTerm.empty();
  }

}
