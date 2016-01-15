package exp;

import values.Value;
import context.Context;

@BoaConstructor(fields = { "head","tail" })
public class Cons extends Exp {
	
	public Exp head;
	public Exp tail;
	
	public Cons() {}

	public Cons(Exp head, Exp tail) {
		super();
		this.head = head;
		this.tail = tail;
	}

	public Value eval(Context context) {
		Value headValue = head.eval(context);
		values.List tailValue = (values.List)tail.eval(context);
		return values.List.cons(headValue,tailValue);
	}

	public String pprint(int opPrec) {
		if(opPrec <= CONSOP) 
			return "(" + head.pprint(CONSOP) + ":" + tail.pprint(CONSOP) + ")";
		else return head.pprint(CONSOP) + ":" + tail.pprint(CONSOP);
	}

}
