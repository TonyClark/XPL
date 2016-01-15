package patterns;

import values.Value;
import context.Context;
import exp.BoaConstructor;

@BoaConstructor(fields = { "value"})
public class Int extends Pattern {
	
	public int value;

	public boolean match(Value value, Context context) {
		if(value instanceof values.Int){
			values.Int i = (values.Int)value;
			return (i.getValue() == this.value);
		} else return false;
	}
	
	public String pprint() {
		return value+"";
	}

}
