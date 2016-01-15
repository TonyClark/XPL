package values;

import java.lang.reflect.Field;

import exp.BoaConstructor;

public abstract class Printable {

	public String toString() {
		Class<? extends Printable> c = getClass();
		String s = c.getSimpleName() + "(";
		if (c.isAnnotationPresent(BoaConstructor.class)) {
			BoaConstructor cnstr = c.getAnnotation(BoaConstructor.class);
			for (String name : cnstr.fields()) {
				Field f;
				try {
					f = c.getField(name);
					s = s + fieldValue(f);
					if (cnstr.fields()[cnstr.fields().length - 1] != name)
						s = s + ',';
				} catch (SecurityException e) {
				} catch (NoSuchFieldException e) {
				}
			}
		}
		return s + ")";
	}

	public String fieldValue(Field field) {
		try {
			Object value = field.get(this);
			return valueString(value);
		} catch (IllegalArgumentException e) {
			return "<ACCESS VIOLATION>";
		} catch (IllegalAccessException e) {
			return "<ACCESS VIOLATION>";
		}

	}

	public String valueString(Object value) {
		if (value == null)
			return "null";
		if (value.getClass().isArray()) {
			Object[] values = (Object[]) value;
			String s = "[";
			for (Object v : values) {
				s = s + v;
				if (v != values[values.length - 1])
					s = s + ",";
			}
			return s + "]";
		} else
			return value.toString();
	}

}
