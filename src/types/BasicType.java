package types;


public class BasicType extends Type {

	public static BasicType				STRING			= new BasicType("str");

	public static BasicType				INTEGER			= new BasicType("int");

	public static BasicType				BOOLEAN			= new BasicType("bool");

	private String						name;

	public BasicType(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}

}
