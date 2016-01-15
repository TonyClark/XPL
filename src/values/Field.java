package values;

import exp.BoaConstructor;

@BoaConstructor(fields = { "name","value"})
public class Field extends Value {
	
	public String name;
	public Value value;
	
	public Field() {}

	public Field(String name, Value value) {
		super();
		this.name = name;
		this.value = value;
	}
	
	public String toString() {
		return name + "=" + value;
	}

}
