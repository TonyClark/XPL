package exp;

import values.Value;
import context.Context;

@BoaConstructor(fields = { "elements" })
public class List extends Exp {
	
	public Exp[] elements = new Exp[0];
	
	public List() {}
	
	public List(Exp...elements) {
		this.elements = elements;
	}

	public Value eval(Context context) {
		Value[] values = new Value[elements.length];
		for(int i = 0;i < elements.length;i++)
			values[i]=elements[i].eval(context);
		return new values.List(values);
	}

	public String pprint(int opPrec) {
		String s = "[";
		for(Exp e : elements) {
			s = s + e.pprint(MAXOP);
			if(e != elements[elements.length-1])
				s = s + ",";
		}
		return s + "]";
	}

}
