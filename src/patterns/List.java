package patterns;

import values.Value;
import context.Context;
import exp.BoaConstructor;
import exp.Exp;

@BoaConstructor(fields = {"patterns"})
public class List extends Pattern {
	
	public Pattern[] patterns;

	public boolean match(Value value, Context context) {
		if(value instanceof values.List){
			values.List list = (values.List)value;
			if(list.values.length == patterns.length) {
				for(int i = 0; i < patterns.length;i++)
					if(!patterns[i].match(list.values[i], context))
						return false;
				return true;
			} else return false;
		} else return false;
	}

	public String pprint(int opPrec) {
		String s = "[";
		for(Pattern e : patterns) {
			s = s + e.pprint();
			if(e != patterns[patterns.length-1])
				s = s + ",";
		}
		return s + "]";
	}

}
