package exp;

import values.Value;
import context.Context;
import grammar.Grammar;

@BoaConstructor(fields = { "target", "name" })
public class Dot extends Exp {

	public Exp		target;
	public String	name;

	public Value eval(Context context) {
		Value value = target.eval(context);
		return value.dot(name);
	}

	public String pprint(int opPrec) {
		if(opPrec <= DOTOP)
			return "(" + target.pprint(DOTOP) + "." + name + ")";
		else return target.pprint(DOTOP) + "." + name ;
	}

}
