package exp;

import values.Value;

public abstract class Binding extends Value {
	
	public BindingName name;
	
	public Binding() {}

	public BindingName getName() {
		return name;
	}

	public void setName(BindingName name) {
		this.name = name;
	}

	public String getLiteralName() {
		BindingNameLiteral l = (BindingNameLiteral)name;
		return l.getName();
	}
	
	public abstract Exp getValue();
	
	public abstract String pprint();

}
