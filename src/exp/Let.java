package exp;

import values.Value;
import context.Context;

@BoaConstructor(fields = { "bindings","body" })
public class Let extends Exp {
	
	public Binding[] bindings;
	public Exp body;
	
	public Let() {}

	public Let(Binding[] bindings, Exp body) {
		super();
		this.bindings = bindings;
		this.body = body;
	}

	public Value eval(Context context) {
		Context copy = context.copy();
		for(Binding b : bindings) 
			copy.bind(b.getLiteralName(),b.getValue().eval(context));
		return body.eval(copy);
	}

	public String pprint(int opPrec) {
		if(opPrec <= LETOP)
			return "(let " + pprintBindings() + " in " + body.pprint(MAXOP) + ")";
		else return "let " + pprintBindings() + " in " + body.pprint(MAXOP);
	}

	private String pprintBindings() {
		String s = "";
		for(Binding b : bindings) {
			s = s + b.pprint();
			if(b != bindings[bindings.length-1])
				s = s + " ";
		}
		return s;
	}

}
