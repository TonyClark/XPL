package types;


public class List extends Type {
	
	private Type elementType;

	public List(Type elementType) {
		super();
		this.elementType = elementType;
	}

	public Type getElementType() {
		return elementType;
	}

	public void setElementType(Type elementType) {
		this.elementType = elementType;
	}

}
