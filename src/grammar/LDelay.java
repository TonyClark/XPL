package grammar;

import machine.Machine;
import exp.BoaConstructor;
import exp.Exp;
import exp.LClosure;

@BoaConstructor(fields = { "start", "end", "grammar" })
public class LDelay extends PDelay {

  public LDelay() {
  }

  public LDelay(String start, String end, Exp grammar) {
    super(start, end, grammar);
  }

  public void exec(Machine machine) {
    machine.skipWhiteSpace();
    if (machine.hasPrefix(start)) {
      String s = getText(machine);
      Exp g = (Exp) grammar.eval(machine);
      machine.pushValue(new LClosure(s, g));
    } else {
      machine.error(machine, "expecting " + start);
      machine.fail(false);
    }
  }

  public String pprint(int opPrec) {
    return "LDELAY('" + start + "','" + end + "'," + grammar.pprint(Exp.MAXOP) + ")";
  }

}
