package patterns;

import values.Value;
import context.Context;
import exp.BoaConstructor;

@BoaConstructor(fields = { "name"})
public class Var extends Pattern {
	
	public Var() {}
	
	public Var(String name) {
		this.name = name;
	}
	
	public String name;

	public boolean match(Value value, Context context) {
		context.bind(name, value);
		return true;
	}
	
	public String pprint() {
		return name;
	}

}
