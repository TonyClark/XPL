package exp;

import values.Value;
import context.Context;

@BoaConstructor(fields = { "name" })
public class Var extends Exp {
	
	public String name;
	
	public Var() {}

	public Var(String name) {
		super();
		this.name = name;
	}

	public Value eval(Context context) {
		if(context.binds(name))
			return context.lookup(name);
		else throw new Error("Unbound variable " + name);
	}
	
	public String pprint(int opPrec) {
		return name;
	}
	
	public boolean isAtom() {
		return true;
	}

}
