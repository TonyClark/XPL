package exp;

import values.Value;
import context.Context;

@BoaConstructor(fields = { "exp" })
public class Thunk extends Exp {
	
	public Exp exp;

	public Value eval(Context context) {
		return new values.Thunk(context,exp);
	}

	public String pprint(int opPrec) {
		return "<" + exp.pprint(MAXOP) + ">";
	}

}
