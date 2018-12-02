package grammar;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

import machine.Fail;
import machine.Machine;
import values.Value;
import context.TerminalSet;
import env.Env;
import exp.BoaConstructor;
import grammar.simple.SimplifiedPTerm;

@BoaConstructor(fields = { "left", "right" })
public class Xor extends PTerm {

  public PTerm left;
  public PTerm right;

  public Xor() {
  }

  public Xor(PTerm left, PTerm right, PTerm... rest) {
    super();
    this.left = left;
    this.right = consTail(right, rest, 0);
  }

  PTerm consTail(PTerm tail, PTerm[] rest, int i) {
    for (PTerm term : rest)
      tail = new Or(tail, term);
    return tail;
  }

  public PTerm close(Env<String, Value> env) {
    return new Xor(left.close(env), right.close(env));
  }

  public TerminalSet predictors(Env<String, Value> env, HashSet<String> NTs) {
    TerminalSet P = left.predictors(env, NTs);
    TerminalSet Q = right.predictors(env, NTs);
    TerminalSet R = new TerminalSet();
    for (String p : P)
      R.add(p);
    for (String q : Q)
      R.add(q);
    return R;
  }

  @SuppressWarnings("unchecked")
  public void exec(Machine machine) {
    Stack<Fail> fails = (Stack<Fail>) machine.getFails().clone();
    machine.choice(right);
    machine.pushInstr(new machine.instrs.Xor(fails));
    machine.pushInstr(left);
  }

  public String pprint(int opPrec) {
    return "XOR(" + left.pprint(10) + "," + right.pprint(10) + ")";
  }

  public Vector<Vector<SimplifiedPTerm>> simplify() {
    return SimplifiedPTerm.appends(left.simplify(), right.simplify());
  }

}
