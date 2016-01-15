package exp;

import context.Context;
import patterns.Pattern;
import values.Value;

@BoaConstructor(fields = { "patterns", "exp" })
public class Arm extends Value {

	public Pattern[]	patterns;
	public Exp			exp;

	public boolean match(Value[] values, Context context) {
		if (patterns.length == values.length) {
			for (int i = 0; i < values.length; i++)
				if (!patterns[i].match(values[i], context))
					return false;
		}
		return true;
	}

	public Value eval(Context context) {
		return exp.eval(context);
	}

	public String pprint() {
		String s = "";
		for (Pattern p : patterns) {
			s = s + p.pprint();
			if (p != patterns[patterns.length-1])
				s = s + ",";
		}
		return s + "->" + exp.pprint(Exp.MAXOP);
	}

}
