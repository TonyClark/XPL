package patterns;

import values.JavaObject;
import values.Value;
import context.Context;
import exp.BoaConstructor;
import exp.Exp;

@BoaConstructor(fields = { "name", "args" })
public class Cnstr extends Pattern {

	public String		name;
	public Pattern[]	args;

	public boolean match(Value value, Context context) {
		if (context.binds(name)) {
			Value v = context.lookup(name);
			if (v instanceof values.JavaClass) {
				values.JavaClass class_ = (values.JavaClass) v;
				Object object = (value instanceof JavaObject) ? ((JavaObject) value).value : value;
				if (class_.c.isInstance(object)) {
					if (class_.c.isAnnotationPresent(BoaConstructor.class)) {
						BoaConstructor cnstr = class_.c.getAnnotation(BoaConstructor.class);
						for (int i = 0; i < cnstr.fields().length; i++) {
							java.lang.reflect.Field field;
							try {
								field = class_.c.getField(cnstr.fields()[i]);
							} catch (SecurityException e) {
								throw new Error(e);
							} catch (NoSuchFieldException e) {
								throw new Error(e);
							}
							try {
								if (!args[i].match(Value.toValue(field.get(object)), context))
									return false;
							} catch (IllegalArgumentException e) {
								throw new Error(e);
							} catch (IllegalAccessException e) {
								throw new Error(e);
							}
						}
						return true;
					} else
						return true;
				} else
					return false;
			} else
				return false;
		} else
			return false;
	}
	
	public String pprint() {
		return name + pprintArgs();
	}
	
	public String pprintArgs() {
		String s = "(";
		for(Pattern arg : args) {
			s = s + arg.pprint();
			if(arg != args[args.length-1])
				s = s + ",";
		}
		return s + ")";
	}
}
