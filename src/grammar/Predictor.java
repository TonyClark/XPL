package grammar;

public abstract class Predictor {

  public static Predictor EPSILON = new Epsilon();
  public static Predictor ANY     = new PredictAnyChar();

}
