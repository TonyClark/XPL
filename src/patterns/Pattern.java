package patterns;

import context.Context;
import values.Value;

public abstract class Pattern extends Value {

	public abstract boolean match(Value value, Context context);

	public String pprint() {
		return toString();
	}

}
