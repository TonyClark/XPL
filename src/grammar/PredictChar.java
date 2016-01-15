package grammar;

public class PredictChar extends Predictor {

  int c;

  public PredictChar(int c) {
	super();
	this.c = c;
  }

  public String toString() {
	return "" + ((char)c);
  }

}
