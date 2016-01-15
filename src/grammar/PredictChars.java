package grammar;

import java.util.Arrays;

public class PredictChars extends Predictor {

  int[] chars;

  public PredictChars(int[] chars) {
	super();
	this.chars = chars;
  }

  public String toString() {
	char[] cs = new char[chars.length];
	for(int i = 0; i < chars.length;i++)
	  cs[i] = (char)chars[i];
	return Arrays.toString(cs);
  }

}
