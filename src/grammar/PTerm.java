package grammar;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Vector;

import machine.instrs.Instr;
import values.Value;
import context.Context;
import context.TerminalSet;
import env.Env;
import grammar.simple.SimplifiedPTerm;

public abstract class PTerm extends Instr {

  public static final int MAXOP  = 10;
  public static final int OROP   = 9;
  public static final int SEQOP  = 8;
  public static final int BINDOP = 2;
  public static final int CALLOP = 1;
  public static final int PREDOP = 1;
  public static final int PLUSOP = 0;
  public static final int STAROP = 0;

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

  public abstract PTerm close(Env<String, Value> env);

  public Value eval(Context context) {
    return this;
  }

  public abstract TerminalSet predictors(Env<String, Value> env, HashSet<String> NTs);

  public abstract String pprint(int opPrec);

  public abstract Vector<Vector<SimplifiedPTerm>> simplify();

}
