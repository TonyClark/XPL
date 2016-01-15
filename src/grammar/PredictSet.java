package grammar;

import java.util.HashSet;

public class PredictSet extends HashSet<Predictor> {

  public PredictSet(Predictor... ps) {
	for (Predictor p : ps)
	  add(p);
  }

  public PredictSet union(PredictSet other) {
	PredictSet s = new PredictSet();
	for (Predictor p : this)
	  s.add(p);
	for (Predictor p : other)
	  s.add(p);
	return s;
  }

  public boolean containsEpsilon() {
	for (Predictor p : this)
	  if (p instanceof Epsilon) return true;
	return false;
  }

  public PredictSet removeEpsilon() {
	PredictSet s = new PredictSet();
	for (Predictor p : this)
	  if (!(p instanceof Epsilon)) s.add(p);
	return s;
  }

  public PredictSet including(Predictor p) {
	PredictSet s = new PredictSet(p);
	for (Predictor pp : this)
	  s.add(pp);
	return s;
  }

  public PredictSet excluding(Predictor p) {
	PredictSet s = new PredictSet(p);
	for (Predictor pp : this)
	  if (!p.equals(pp)) s.add(pp);
	return s;
  }

  public boolean includes(Predictor p) {
	for (Predictor pp : this)
	  if (pp.equals(p)) return true;
	return false;
  }
}
