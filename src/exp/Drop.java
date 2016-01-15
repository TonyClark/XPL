package exp;

import context.Context;
import values.Value;

@BoaConstructor(fields = { "exp" })
public class Drop extends Exp {
	
	public Exp exp;
	
	public Drop() {}

	public Drop(Exp exp) {
		super();
		this.exp = exp;
	}
	
	public Exp getExp() {
		return exp;
	}

	public void setExp(Exp exp) {
		this.exp = exp;
	}

	public Exp lift() {
		return exp;
	}

	public Value eval(Context context) {
		throw new Error("Drop.eval is illegal.");
	}

	public String pprint(int opPrec) {
		return "${" + exp.pprint(MAXOP) + "}";
	}

}
