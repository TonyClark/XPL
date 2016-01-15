package patterns;

import values.Value;
import context.Context;
import exp.BoaConstructor;
import exp.Exp;

@BoaConstructor(fields = { "pattern", "cond" })
public class Cond extends Pattern {
	
	public Pattern pattern;
	public Exp cond;
	
	public Cond() {}

	public boolean match(Value value, Context context) {
		if(pattern.match(value, context)) {
			Value v = cond.eval(context);
			if(v instanceof values.Bool){
				values.Bool bool = (values.Bool)v;
				return bool.value;
			} else return false;
		} else return false;
	}
	
	public String pprint() {
		return pattern.pprint() + "?" + cond.pprint(Exp.MAXOP);
	}

}
