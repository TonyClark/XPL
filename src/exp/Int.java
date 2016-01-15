package exp;

import values.Value;
import context.Context;

@BoaConstructor(fields = { "value" })
public class Int extends Exp {
	
	public int value;
	
	public Int() {}

	public Int(int value) {
		super();
		this.value = value;
	}

	public Value eval(Context context) {
		return new values.Int(value);
	}
	
	public String pprint(int opPrec) {
		return value + "";
	}
	
	public boolean isAtom() {
		return true;
	}

}
