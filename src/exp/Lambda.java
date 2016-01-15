package exp;

import values.Closure;
import values.Value;
import context.Context;

@BoaConstructor(fields = { "args","body" })
public class Lambda extends Exp {
	
	public String[] args;
	public Exp body;
	
	public Lambda() {}
	
	public Lambda(Exp body,String... args) {
		this.body = body;
		this.args = args;
	}

	public Value eval(Context context) {
		return new Closure(args,context,body);
	}

	public String pprint(int opPrec) {
		if(opPrec < FUNOP)
			return "(fun" + pprintArgs() + body.pprint(MAXOP) + ")";
		else return "fun" + pprintArgs() + body.pprint(MAXOP);
	}

	private String pprintArgs() {
		String s = "(";
		for(String a : args) {
			s = s + a;
			if(a != args[args.length-1])
				s = s + ",";
		}
		return s + ") ";
	}

}
