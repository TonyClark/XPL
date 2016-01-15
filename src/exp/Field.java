package exp;

import values.Value;
import context.Context;

@BoaConstructor(fields = { "name","value" })
public class Field extends Value {
	
	public String name;
	public Exp value;
	
	public Field() {}

	public Field(String name, Exp value) {
		super();
		this.name = name;
		this.value = value;
	}
	
	public values.Field eval(Context context) {
		return new values.Field(name,value.eval(context));
	}

	public String pprint() {
		return name + "=" + value.pprint(Exp.MAXOP);
	}

}
