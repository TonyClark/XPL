package exp;

import java.lang.reflect.Field;

import values.Value;
import context.Context;

public abstract class Exp extends Value {

	public static final int	MAXOP	= 11;
	public static final int	IFOP	= 10;
	public static final int	FUNOP	= 10;
	public static final int	LETOP	= 10;
	public static final int	ANDOP	= 9;
	public static final int	OROP	= 9;
	public static final int	GREOP	= 8;
	public static final int	LESSOP	= 8;
	public static final int	EQOP	= 7;
	public static final int	NEQOP	= 7;
	public static final int	MULOP	= 6;
	public static final int	DIVOP	= 6;
	public static final int	ADDOP	= 5;
	public static final int	SUBOP	= 5;
	public static final int	CONSOP	= 4;
	public static final int	DOTOP	= 3;
	public static final int	APPOP	= 0;

	public abstract Value eval(Context context);

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
	
	public String toString() {
		return "[| " + pprint(MAXOP) + " |]";
	}

	public abstract String pprint(int opPrec);
	
	public boolean isAtom() {
		return false;
	}

}
