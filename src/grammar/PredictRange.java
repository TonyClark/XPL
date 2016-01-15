package grammar;

public class PredictRange extends Predictor {

  int lower;
  int upper;

  public PredictRange(int lower, int upper) {
	super();
	this.lower = lower;
	this.upper = upper;
  }

  public String toString() {
	return "[" + lower + "," + upper + "]";
  }
}
