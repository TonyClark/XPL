package values;

import java.lang.reflect.Modifier;

import exp.BoaConstructor;

@BoaConstructor(fields = { "c" })
public class JavaClass extends Value {

	public Class<?>	c;

	public JavaClass(Class<?> c) {
		super();
		this.c = c;
	}

	public Class<?> getClass_() {
		return c;
	}

	public Value send(String name, Value[] values) {
		Class<?> m = c.getClass();
		return send(m, name, values);
	}

	public Object getTarget() {
		return c;
	}

	public String toString() {
		return c.toString();
	}

	public Value dot(String name) {
		try {
			java.lang.reflect.Field field = c.getField(name);
			if ((field.getModifiers() & Modifier.STATIC) != 0)
				return Value.toValue(field.get(null));
			return super.dot(name);
		} catch (SecurityException e) {
			throw new Error(e);
		} catch (NoSuchFieldException e) {
			throw new Error(e);
		} catch (IllegalArgumentException e) {
			throw new Error(e);
		} catch (IllegalAccessException e) {
			throw new Error(e);
		}

	}

}
