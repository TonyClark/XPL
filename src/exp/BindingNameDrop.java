package exp;

@BoaConstructor(fields = { "name" })
public class BindingNameDrop extends BindingName {
	
	public Drop name;
	
	public BindingNameDrop() {}

	public BindingNameDrop(Drop name) {
		super();
		this.name = name;
	}

	public Drop getName() {
		return name;
	}

	public void setName(Drop name) {
		this.name = name;
	}
	
	public Exp lift() {
		return new Apply("exp.BindingNameLiteral",name.lift());
	}

}
