package values;

public class XAtt {

	private String	name;
	private Thunk	value;

	public XAtt(String name, Thunk initial) {
		super();
		this.name = name;
		this.value = initial;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Thunk getInitial() {
		return value;
	}

	public void setInitial(Thunk value) {
		this.value = value;
	};
	
	public Value eval() {
		return value.force();
	}

}
