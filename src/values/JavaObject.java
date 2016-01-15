package values;

import exp.BoaConstructor;

@BoaConstructor(fields = { "object"})
public class JavaObject extends Value {

	public Object value;
	
	public JavaObject(Object o) {
		value = o;
	}

	public Value send(String name, Value[] values) {
		Class<?> c = value.getClass();
		return send(c,name,values);
	}
	
	public Object getTarget() {
		return value;
	}
	
	public String toString() {
		return value.toString();
	}

}
