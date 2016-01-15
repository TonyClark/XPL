package exp;

import java.lang.reflect.Field;
import java.util.Arrays;

import values.Builtin;
import values.Closure;
import values.JavaClass;
import values.JavaObject;
import values.Str;
import values.Value;
import context.Context;

@BoaConstructor(fields = { "operator", "operands" })
public class Apply extends Exp {

	public Exp		operator;
	public Exp[]	operands;

	public Apply() {
	}

	public Apply(Exp operator, Exp... operands) {
		this.operator = operator;
		this.operands = operands;
	}

	public Apply(String operator, Exp... operands) {
		this.operator = new exp.Str(operator);
		this.operands = operands;
	}

	public Value eval(Context context) {
		Value v = operator.eval(context);
		if (v instanceof Closure)
			return applyClosure((Closure) v, context);
		else if (v instanceof values.Str)
			return applyStr((values.Str) v, context);
		else if (v instanceof Builtin)
			return applyBuiltin((Builtin) v, context);
		else if (v instanceof JavaClass)
			return applyClass(((JavaClass) v).getClass_(), context);
		else
			throw new Error("Illegal operator " + v);
	}

	private Value applyBuiltin(Builtin b, Context context) {
		Value[] args = new Value[operands.length];
		for (int i = 0; i < operands.length; i++)
			args[i] = operands[i].eval(context);
		return b.apply(args);
	}

	public Value applyStr(Str s, Context context) {
		String name = s.getValue();
		Class<?> c = null;
		try {
			c = Class.forName(name);
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		return applyClass(c, context);
	}

	public Value applyClass(Class<?> c, Context context) {
		Object o = null;
		try {
			o = c.newInstance();
		} catch (InstantiationException e1) {
			throw new Error("Cannot instantiate the class " + c);
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		}
		int fieldIndex = 0;
		if (c.isAnnotationPresent(BoaConstructor.class)) {
			BoaConstructor cnstr = c.getAnnotation(BoaConstructor.class);
			for (String name : cnstr.fields()) {
				Value value = operands[fieldIndex++].eval(context);
				Field field;
				try {
					field = c.getField(name);
					Class<?> type = field.getType();
					Object object = toJava(value, type);
					try {
						field.set(o, object);
					} catch (IllegalArgumentException e) {
						System.out.println("Error setting " + field + " to " + object);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (Exception x) {
						System.out.println("The value " + object + ":" + object.getClass() + " is not of type " + type);
					}
				} catch (SecurityException e1) {
					e1.printStackTrace();
				} catch (NoSuchFieldException e1) {
					e1.printStackTrace();
				} catch (Exception x) {
          System.out.println("cannot set " + name + " to " + value);
        }
			}
		}
		if (Value.class.isAssignableFrom(o.getClass()))
			return (Value) o;
		else
			return new JavaObject(o);
	}

	public Value applyClosure(Closure c, Context context) {
	  if(operands.length == c.getArgs().length) {
		Context newContext = c.context.copy();
		for (int i = 0; i < operands.length; i++)
			newContext.bind(c.getArgs()[i], operands[i].eval(context));
		//System.out.println("Apply: " + c.getBody() + " in " + newContext.getEnv());
		return c.getBody().eval(newContext);
	  } else throw new Error("closure with body " + c.getBody() + " expects args " + Arrays.toString(c.getArgs()) + " but was supplied with " + Arrays.toString(operands));
	}

	public String pprint(int opPrec) {
		if(opPrec <= APPOP)
			return "(" + operator.pprint(APPOP) + pprintArgs() + ")";
		else return operator.pprint(APPOP) + pprintArgs();
	}
	
	public String pprintArgs() {
		String s = "(";
		for(Exp arg : operands) {
			s = s + arg.pprint(MAXOP);
			if(arg != operands[operands.length-1])
				s = s + ",";
		}
		return s + ")";
	}

}
