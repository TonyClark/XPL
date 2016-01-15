package values;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Vector;

import context.Context;
import context.StringSource;

import xpl.XPL;
import env.Empty;
import env.Env;
import exp.Apply;
import exp.BoaConstructor;
import exp.Exp;

public abstract class Value extends Printable {

	public static Env<String, Value>	builtinEnv;

	static {
		builtinEnv = new Empty<String, Value>();
		builtinEnv = builtinEnv.bind("head", new Builtin() {
			public Value apply(Value[] args) {
				if (args.length == 1 && args[0] instanceof List) {
					List l = (List) args[0];
					return l.elementAt(0);
				} else
					throw new Error("head problem: " + args[0]);
			}
		});
		builtinEnv = builtinEnv.bind("tail", new Builtin() {
			public Value apply(Value[] args) {
				if (args.length == 1 && args[0] instanceof List) {
					List l = (List) args[0];
					return l.tail();
				} else
					throw new Error("tail problem");
			}
		});
		builtinEnv = builtinEnv.bind("asString", new Builtin() {
			public Value apply(Value[] args) {
				if(args.length == 1 && isListOfInt(args[0]))
					return new Str(listOfIntToString((List)args[0]));
				else throw new Error("Illegal argument to asString");
			}
		});
		builtinEnv = builtinEnv.bind("asInt", new Builtin() {
			public Value apply(Value[] args) {
				if(args.length == 1 && isListOfInt(args[0]))
					return new Int(Integer.parseInt(listOfIntToString((List)args[0])));
				else throw new Error("Illegal argument to asInt");
			}
		});
		builtinEnv = builtinEnv.bind("print", new Builtin() {
			public Value apply(Value[] args) {
				if(args.length == 1) {
					System.out.println(args[0]);
					return args[0];
				}
				else throw new Error("Illegal argument to print");
			}
		});
		builtinEnv = builtinEnv.bind("recordUpdate", new Builtin() {
			public Value apply(Value[] args) {
				if(args.length == 3) {
					values.Record record = (values.Record)args[0];
					values.Str name = (values.Str)args[1];
					Value value = args[2];
					return record.subst(name,value);
				}
				else throw new Error("Illegal argument to recordUpdate");
			}
		});
		builtinEnv = builtinEnv.bind("xpl",new JavaClass(XPL.class));
	}

	public boolean equals(Object other) {
		if (other.getClass() == getClass()) {
			Class<? extends Value> c = getClass();
			Field[] fields = c.getDeclaredFields();
			for (Field f : fields)
				try {
					if (((f.getModifiers() & Modifier.STATIC) == 0) && (!equalJavaValues(f.get(other), f.get(this))))
						return false;
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			return true;
		} else
			return false;
	}

	private boolean equalJavaValues(Object o1, Object o2) {
		if (o1.getClass() == o2.getClass()) {
			if (o1.getClass().isArray()) {
				Object[] a1 = (Object[]) o1;
				Object[] a2 = (Object[]) o2;
				if (a1.length == a2.length) {
					for (int i = 0; i < a1.length; i++)
						if (!equalJavaValues(a1[i], a2[i]))
							return false;
					return true;
				} else
					return false;
			} else if (o1 instanceof Integer) {
				Integer i1 = (Integer) o1;
				Integer i2 = (Integer) o2;
				return i1.intValue() == i2.intValue();
			} else if (o1 instanceof Boolean) {
				Boolean b1 = (Boolean) o1;
				Boolean b2 = (Boolean) o2;
				return b1.booleanValue() == b2.booleanValue();
			} else
				return o1.equals(o2);
		} else
			return false;
	}

	public Exp lift() {
		Class<? extends Value> c = getClass();
		String name = c.getName();
		if (c.isAnnotationPresent(BoaConstructor.class)) {
			BoaConstructor cnstr = c.getAnnotation(BoaConstructor.class);
			String[] fieldNames = cnstr.fields();
			Exp[] exps = new Exp[fieldNames.length];
			for (int i = 0; i < fieldNames.length; i++) {
				try {
					Field field = c.getField(fieldNames[i]);
					Object obj = field.get(this);
					exps[i] = liftJavaValue(obj);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (NoSuchFieldException e) {
					e.printStackTrace();
				}
			}
			return (Exp) new Apply(name, exps);
		} else
			return (Exp) new Apply(name);
	}

	private Exp liftJavaValue(Object obj) {
		if (obj instanceof String)
			return new exp.Str((String) obj);
		if (obj instanceof Integer)
			return new exp.Int((Integer) obj);
		if (obj instanceof Boolean)
			return new exp.Bool((Boolean) obj);
		if (obj.getClass().isArray())
			return liftJavaArray((Object[]) obj);
		if (obj instanceof Value) {
			Value v = (Value) obj;
			return v.lift();
		}
		throw new Error("I don't know how to lift " + obj);
	}

	private Exp liftJavaArray(Object[] array) {
		Exp[] exps = new Exp[array.length];
		for (int i = 0; i < array.length; i++)
			exps[i] = liftJavaValue(array[i]);
		return new exp.List(exps);
	}
	
	Vector<Method> getMethods(Class<?> c) {
		Vector<Method> methods = new Vector<Method>();
		while(c != null) { 
			Method[] ms = c.getDeclaredMethods();
			for(Method m : ms) 
				methods.add(m);
			c = c.getSuperclass();
		}
		if(getTarget().getClass() == Class.class) {
			Class<?> target = (Class<?>)getTarget();
			Method[] ms = target.getDeclaredMethods();
			for(Method m : ms) 
				if((m.getModifiers() & Modifier.STATIC) != 0)
					methods.add(m);
		}
		return methods;
	}

	public Value send(String name, Value[] values) {
		Class<? extends Value> c = getClass();
		return send(c,name,values);
	}
	
	public Value send(Class<?> c,String name,Value[] values) {
		Vector<Method> methods = getMethods(c);
		Method method = null;
		for (Method m : methods)
			if (m.getName().equals(name) && m.getParameterTypes().length == values.length)
				method = m;
		if (method == null)
			throw new Error("Cannot find method named " + name + " in " + this);
		Class<?>[] argTypes = method.getParameterTypes();
		Object[] argValues = new Object[values.length];
		for (int i = 0; i < values.length; i++) {
			argValues[i] = toJava(values[i], argTypes[i]);
		}
		try {
			Object result = method.invoke(getTarget(), argValues);
			return toValue(result);
		} catch (IllegalArgumentException e) {
		  e.printStackTrace();
			throw new Error(e.getCause().getMessage());
		} catch (IllegalAccessException e) {
		  e.printStackTrace();
			throw new Error(e.getCause().getMessage());
		} catch (InvocationTargetException e) {
		  e.printStackTrace();
			throw new Error(e.getCause().getMessage());
		}
	}
	
	public Object getTarget() {
		return this;
	}

	public static Value toValue(Object o) {
		if(o == null)
			return new values.Str("void");
		if (o instanceof Integer)
			return new values.Int((Integer) o);
		if (o instanceof String)
			return new values.Str((String) o);
		if (o instanceof Boolean)
			return new values.Bool((Boolean) o);
		if(o.getClass().isArray()) {
			Object[] array = (Object[])o;
			Value[] values = new Value[array.length];
			for(int i = 0; i < array.length; i++)
				values[i] = toValue(array[i]);
			return new List(values);
		}
		if(!(Value.class.isAssignableFrom(o.getClass())))
			return new JavaObject(o);
		return (Value) o;
	}

	public static Object toJava(Value v, Class<?> type) {
		if (type == Value.class)
			return v;
		if (v instanceof values.Str) {
			values.Str s = (values.Str) v;
			return s.getValue();
		}
		if (v instanceof values.Int) {
			values.Int i = (values.Int) v;
			return i.getValue();
		}
		if (v instanceof values.Bool) {
			values.Bool i = (values.Bool) v;
			return i.getValue();
		}
		if (isListOfInt(v)) {
			if (type == String.class)
				return listOfIntToString((values.List) v);
			if (type == Integer.TYPE)
				return Integer.parseInt(listOfIntToString((values.List) v));
		}
		if (v instanceof values.List) {
			if (type.isArray())
				return listToArray((values.List) v, type);
		}
		if (v instanceof values.Record && type == context.Context.class) {
		  values.Record r = (values.Record)v;
		  return new Context(null,r.asEnv(),new StringSource(""),0,null,0);
		}
		if(v instanceof JavaObject)  {
		  JavaObject j = (JavaObject)v;
		  return j.getTarget();
		}
		return v;
	}

	public static Object[] listToArray(List l, Class<?> type) {
		Object[] values = (Object[]) Array.newInstance(type.getComponentType(), l.size());
		for (int i = 0; i < l.size(); i++) {
			Object value = toJava(l.elementAt(i), type.getComponentType());
			values[i] = value;
		}
		return values;
	}

	public static String listOfIntToString(values.List list) {
		String s = "";
		for (int i = 0; i < list.size(); i++) {
			values.Int n = (values.Int) list.elementAt(i);
			s = s + (char) n.getValue();
		}
		return s;
	}

	public static boolean isListOfInt(Value v) {
		if (v instanceof values.List) {
			values.List list = (values.List) v;
			boolean listOfInt = true;
			for (int i = 0; i < list.size() && listOfInt; i++)
				listOfInt = listOfInt && isChar(list.elementAt(i));
			return listOfInt;
		} else
			return false;
	}

	public static boolean isChar(Value v) {
		if (v instanceof values.Int) {
			values.Int i = (values.Int) v;
			return i.getValue() <= 255;
		} else
			return false;
	}
	
	public Value dot(String name) {
		throw new Error("Cannot access field " + name + " of " + this);
	}
}
