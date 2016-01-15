package exp;

import values.Value;
import context.Context;

@BoaConstructor(fields = { "exps","arms"})
public class Case extends Exp {
	
	public Exp[] exps;
	public Arm[] arms;

	public Value eval(Context context) {
		Value[] values = new Value[exps.length];
		for(int i = 0; i < exps.length; i++) 
			values[i] = exps[i].eval(context);
		for(Arm arm : arms) {
			Context copy = context.copy();
			if(arm.match(values,copy))
				return arm.eval(copy);
		}
		String s = "";
		for(Value v : values) {
			s = s + v;
			if(v != values[values.length-1])
				s = s + ",";
		}
		throw new Error("No arm matches value: " + s + "\n in " + this);
	}

	public String pprint(int opPrec) {
		String s = "case ";
		for(Exp e : exps) {
			s = s + e.pprint(MAXOP);
			if(e != exps[exps.length-1])
				s = s + ",";
		}
		s = s + " { ";
		for(Arm arm : arms) {
			s = s + arm.pprint();
			if(arm != arms[arms.length-1])
				s = s + ";";
		}
		return s + " }";
	}

}
