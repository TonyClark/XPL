package values;

import context.Context;
import exp.BoaConstructor;
import exp.Exp;

@BoaConstructor(fields = { "context","exp" })
public class Thunk extends Value {
	
	public Context context;
	public Exp exp;
	
	public Thunk() {}
	
	public Thunk(Context context, Exp exp) {
		super();
		this.context = context;
		this.exp = exp;
	}

	public Value force() {
		return exp.eval(context);
	}

}
