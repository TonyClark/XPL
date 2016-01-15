package exp;

@BoaConstructor(fields = { "name" })
public class BindingNameLiteral extends BindingName {
	
	public String name;
	
	public BindingNameLiteral() {}

	public BindingNameLiteral(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String pprint() {
		return getName();
	}
	
	public String toString() {
	  return name;
	}

}
