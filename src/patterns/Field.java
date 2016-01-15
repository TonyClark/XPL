package patterns;

import values.Value;
import exp.BoaConstructor;

@BoaConstructor(fields = { "name","pattern"})
public class Field extends Value {
	
	public String name;
	public Pattern pattern;
	
	public String pprint() {
		return name + "=" + pattern.pprint();
	}

}
