package patterns;

import values.Value;
import context.Context;
import exp.BoaConstructor;

@BoaConstructor(fields = { "value"})
public class Str extends Pattern {
	
	public String value;

	public boolean match(Value value, Context context) {
		if(value instanceof values.Str){
			values.Str i = (values.Str)value;
			return (i.getValue().equals(this.value));
		} else return false;
	}
	
	public String pprint() {
		return value;
	}

}
