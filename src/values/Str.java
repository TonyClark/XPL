package values;

import exp.BoaConstructor;

@BoaConstructor(fields = { "value"})
public class Str extends Value {
	
	public String value;
	
	public Str() {}

	public Str(String value) {
		super();
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public Value send(String name,Value[] values) {
		if(name.equals("java") && values.length == 0) {
			try {
				getClass();
				return new JavaClass(Class.forName(value));
			} catch (ClassNotFoundException e) {
				throw new Error("Cannot find class " + value);
			}
		} else return super.send(name, values);
	}
	
	public String toString() {
		return "'" + value + "'";
	}
	
	public List asList() {
		Value[] ints = new Value[value.length()];
		for(int i = 0; i < value.length(); i++)
			ints[i]=new Int(value.charAt(i));
		return new List(ints);
	}
		

}
