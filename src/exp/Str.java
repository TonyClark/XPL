package exp;

import values.Value;
import context.Context;

@BoaConstructor(fields = { "value" })
public class Str extends Exp {

	public String value;
	
	public Str() {}

	public Str(String value) {
		super();
		this.value = value;
	}

	public Value eval(Context context) {
		return new values.Str(value);
	}
	
	public String pprint(int opPrec) {
		return "'" + value + "'";
	}

}
