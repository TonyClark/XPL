package values;

import exp.BoaConstructor;

@BoaConstructor(fields = { "value"})
public class Bool extends Value {
	
	public boolean value;
	
	public Bool() {}

	public Bool(boolean value) {
		super();
		this.value = value;
	}

	public boolean getValue() {
		return value;
	}

	public void setValue(boolean value) {
		this.value = value;
	}
	
	public String toString() {
		return ""+value;
	}

}
