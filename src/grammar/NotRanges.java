package grammar;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Vector;

import machine.Machine;
import values.Value;
import context.TerminalSet;
import env.Env;
import grammar.simple.SimplifiedPTerm;

public class NotRanges extends PTerm {

  Range[] ranges;

  public NotRanges(Range[] ranges) {
    super();
    this.ranges = ranges;
  }

  public PTerm close(Env<String, Value> env) {
    return this;
  }

  public TerminalSet predictors(Env<String, Value> env, HashSet<String> NTs) {
    TerminalSet Ts = new TerminalSet();
    for (Range r : ranges)
      Ts.add("[" + (char) r.lower + "," + (char) r.upper + "]");
    return Ts;
  }

  public String pprint(int opPrec) {
    return "not(" + Arrays.toString(ranges) + "?)";
  }

  public void exec(Machine machine) {
    if (!machine.EOF()) {
      int c = machine.peek();
      boolean found = false;
      for (int i = 0; i < ranges.length && !found; i++) {
        Range r = ranges[i];
        if (c >= r.lower && c <= r.upper) {
          found = true;
          machine.fail(false);
        }
      }
      if (!found) machine.pushValue(null);
    } else machine.pushValue(null);
  }

  public Vector<Vector<SimplifiedPTerm>> simplify() {
    return SimplifiedPTerm.empty();
  }

}
