package exp;

import values.Value;
import context.Context;

public class Reify extends Exp {

	public Value eval(Context context) {
		return context.getGrammar();
	}

	public String pprint(int opPrec) {
		return "REIFY";
	}

}
