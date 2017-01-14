package grammar;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import context.TerminalSet;
import env.Env;
import exp.BoaConstructor;
import machine.Machine;
import values.Value;

@BoaConstructor(fields = { "exp" })
public class Regexp extends PTerm {

  public String exp;
  Pattern       pattern = null;

  public Regexp() {
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
    machine.skipWhiteSpace();
    if (pattern == null) pattern = Pattern.compile(exp);
    Matcher matcher = pattern.matcher(machine.getText().getText());
    matcher = matcher.region(machine.getTextPtr(), machine.getText().length());
    if (matcher.find()) { 
      String s = matcher.group(0);
      machine.setTextPtr(machine.getTextPtr() + s.length());
      machine.pushValue(new values.Str(s));
    } else {
      machine.error(machine, "expecting " + exp);
      machine.fail(false);
    }
  }

  public String pprint(int opPrec) {
    return "REGEXP('" + exp + "')";
  }

  private static boolean needsProtection(char c) {
    return false;
  }

}
