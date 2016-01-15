package values;

import exp.BoaConstructor;

@BoaConstructor(fields = { "value"})
public class Int extends Value {
	
	public int value;
	
	public Int() {}

	public Int(int value) {
		super();
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
	public String toString() {
		return ""+value;
	}

}
