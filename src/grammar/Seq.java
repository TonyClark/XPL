package grammar;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Vector;

import machine.Machine;
import values.Value;
import context.TerminalSet;
import env.Env;
import exp.BoaConstructor;
import grammar.simple.SimplifiedPTerm;

@BoaConstructor(fields = { "first", "second" })
public class Seq extends PTerm {

  public PTerm first;
  public PTerm second;

  public Seq() {
  }

  public Seq(PTerm first, PTerm second, PTerm... rest) {
    super();
    this.first = first;
    this.second = consTail(second, rest);
  }

  PTerm consTail(PTerm tail, PTerm[] rest) {
    for (PTerm term : rest)
      tail = new Seq(tail, term);
    return tail;
  }

  public PTerm close(Env<String, Value> env) {
    return new Seq(first.close(env), second.close(env));
  }

  public TerminalSet predictors(Env<String, Value> env, HashSet<String> NTs) {
    TerminalSet P = first.predictors(env, NTs);
    if (P.isEmpty())
      return second.predictors(env, NTs);
    else return P;
  }

  public void exec(Machine machine) {
    machine.pushInstr(second);
    machine.pushInstr(new machine.instrs.Pop());
    machine.pushInstr(first);

  }

  public String pprint(int opPrec) {
    if (opPrec < SEQOP)
      return "(" + first.pprint(SEQOP) + " " + second.pprint(SEQOP) + ")";
    else return first.pprint(SEQOP) + " " + second.pprint(SEQOP);
  }

  public Vector<Vector<SimplifiedPTerm>> simplify() {
    Vector<Vector<SimplifiedPTerm>> termss1 = first.simplify();
    Vector<Vector<SimplifiedPTerm>> termss2 = second.simplify();
    Vector<Vector<SimplifiedPTerm>> termss = new Vector<Vector<SimplifiedPTerm>>();
    for (Vector<SimplifiedPTerm> terms1 : termss1) {
      for (Vector<SimplifiedPTerm> terms2 : termss2) {
        termss.add(SimplifiedPTerm.append(terms1, terms2));
      }
    }
    return termss;
  }
}
