package exp;


@BoaConstructor(fields = { "name","value" })
public class ValueBinding extends Binding {
	
	public Exp value;
	
	public ValueBinding() {}

	public ValueBinding(BindingName name, Exp value) {
		super();
		this.name = name;
		this.value = value;
	}

	public Exp getValue() {
		return value;
	}

	public void setValue(Exp value) {
		this.value = value;
	}

	public String pprint() {
		return getName() + "=" + value.pprint(Exp.MAXOP);
	}

}
