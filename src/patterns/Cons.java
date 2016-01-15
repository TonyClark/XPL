package patterns;

import values.Value;
import context.Context;
import exp.BoaConstructor;

@BoaConstructor(fields = { "head","tail"})
public class Cons extends Pattern {
	
	public Pattern head;
	public Pattern tail;

	public boolean match(Value value, Context context) {
		if(value instanceof values.List) {
			values.List list = (values.List)value;
			if(list.values.length == 0)
				return false;
			else {
				return head.match(list.getHead(), context) && tail.match(list.getTail(), context);
			}
		} else return false;
	}

	public String pprint() {
		return head.pprint() + ":" + tail.pprint();
	}

}
