package patterns;

import values.Value;
import context.Context;
import exp.BoaConstructor;

public class False extends Pattern {

	public boolean match(Value value, Context context) {
		if(value instanceof values.Bool){
			values.Bool i = (values.Bool)value;
			return !i.getValue();
		} else return false;
	}
	
	public String pprint() {
		return "false";
	}

}
