package exp;

import values.Value;
import context.Context;

@BoaConstructor(fields = { "test","conseq","alt" })
public class If extends Exp {
	
	public Exp test;
	public Exp conseq;
	public Exp alt;
	
	public If() {}

	public If(Exp test, Exp conseq, Exp alt) {
		super();
		this.test = test;
		this.conseq = conseq;
		this.alt = alt;
	}

	public Value eval(Context context) {
		values.Bool b = (values.Bool)test.eval(context);
		if(b.getValue())
			return conseq.eval(context);
		else return alt.eval(context);
	}

	public String pprint(int opPrec) {
		if(opPrec <= IFOP) 
			return "(if " + test.pprint(IFOP) + " then " + conseq.pprint(IFOP) + " else " + alt.pprint(IFOP) + ")";
		else return "if " + test.pprint(IFOP) + " then " + conseq.pprint(IFOP) + " else " + alt.pprint(IFOP);
	}

}
