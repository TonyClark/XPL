package grammar;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Vector;

import machine.Machine;
import values.Value;
import context.TerminalSet;
import env.Env;
import exp.BoaConstructor;

@BoaConstructor(fields = { "left", "right" })
public class Or extends PTerm {

  public PTerm left;
  public PTerm right;

  public Or() {
  }

  public Or(PTerm left, PTerm right, PTerm... rest) {
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
	if (isCharLiteralChoice()) return asCharLiteral().close(env);
	if (isLiteralChoice()) return asLiteral().close(env);
	if (isRangeChoice()) return asRange().close(env);
	return new Or(left.close(env), right.close(env));
  }

  public PTerm asRange() {
	return new RangeChoice(getRanges());
  }

  public Range[] getRanges() {
	Vector<Range> ranges = new Vector<Range>();
	putRanges(this, ranges);
	Range[] rs = new Range[ranges.size()];
	for (int i = 0; i < ranges.size(); i++)
	  rs[i] = ranges.get(i);
	return rs;
  }

  public static void putRanges(PTerm term, Vector<Range> ranges) {
	if (term instanceof Or) {
	  Or or = (Or) term;
	  putRanges(or.left, ranges);
	  putRanges(or.right, ranges);
	} else if (term instanceof Range)
	  ranges.add((Range) term);
	else throw new Error("expecting a range or an or: " + term);
  }

  public PTerm asLiteral() {
	return new TermChoice(getTerms());
  }

  public String[] getTerms() {
	Vector<Term> terms = new Vector<Term>();
	putTerms(this, terms);
	String[] ts = new String[terms.size()];
	for (int i = 0; i < terms.size(); i++)
	  ts[i] = terms.get(i).term;
	return ts;
  }

  public static void putTerms(PTerm term, Vector<Term> terms) {
	if (term instanceof Or) {
	  Or or = (Or) term;
	  putTerms(or.left, terms);
	  putTerms(or.right, terms);
	} else if (term instanceof Term)
	  terms.add((Term) term);
	else throw new Error("expecting a term or an or: " + term);
  }

  public PTerm asCharLiteral() {
	return new CharChoice(getChars());
  }

  public int[] getChars() {
	Vector<Char> chars = new Vector<Char>();
	putChars(this, chars);
	int[] cs = new int[chars.size()];
	for (int i = 0; i < chars.size(); i++)
	  cs[i] = chars.get(i).c;
	return cs;
  }

  public static void putChars(PTerm term, Vector<Char> chars) {
	if (term instanceof Or) {
	  Or or = (Or) term;
	  putChars(or.left, chars);
	  putChars(or.right, chars);
	} else if (term instanceof Char)
	  chars.add((Char) term);
	else throw new Error("expecting a char or an or: " + term);
  }

  public boolean isRangeChoice() {
	return isRangeChoice(left) && isRangeChoice(right);
  }

  public boolean isRangeChoice(PTerm term) {
	return isRange(term) || ((term instanceof Or) && ((Or) term).isRangeChoice());
  }

  public boolean isRange(PTerm term) {
	return term instanceof Range;
  }

  public boolean isLiteralChoice() {
	return isLiteralChoice(left) && isLiteralChoice(right);
  }

  public boolean isLiteralChoice(PTerm term) {
	return isLiteral(term) || ((term instanceof Or) && ((Or) term).isLiteralChoice());
  }

  public boolean isLiteral(PTerm term) {
	return term instanceof Term;
  }

  public boolean isCharLiteralChoice() {
	return isCharLiteralChoice(left) && isCharLiteralChoice(right);
  }

  public boolean isCharLiteralChoice(PTerm term) {
	return isCharLiteral(term) || ((term instanceof Or) && ((Or) term).isCharLiteralChoice());
  }

  public boolean isCharLiteral(PTerm term) {
	return term instanceof Char;
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

  public void exec(Machine machine) {
	machine.choice(right);
	machine.pushInstr(left);
  }

  public String pprint(int opPrec) {
	if (opPrec < OROP)
	  return "(" + left.pprint(OROP) + " | " + right.pprint(OROP) + ")";
	else return left.pprint(OROP) + " | " + right.pprint(OROP);
  }

}
