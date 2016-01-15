package exp;

import values.Value;
import context.Context;

@BoaConstructor(fields = { "value" })
public class Bool extends Exp {
	
	public boolean value;
	
	public Bool() {}

	public Bool(boolean value) {
		super();
		this.value = value;
	}

	public Value eval(Context context) {
		return new values.Bool(value);
	}
	
	public String pprint(int opPrec) {
		return value + "";
	}
	
	public boolean isAtom() {
		return true;
	}

}
