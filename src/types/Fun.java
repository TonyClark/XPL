package types;

import java.util.Vector;

public class Fun extends Type {

	private Vector<Type>	domain;
	private Type			range;

	public Fun(Vector<Type> domain, Type range) {
		super();
		this.domain = domain;
		this.range = range;
	}

	public Vector<Type> getDomain() {
		return domain;
	}

	public void setDomain(Vector<Type> domain) {
		this.domain = domain;
	}

	public Type getRange() {
		return range;
	}

	public void setRange(Type range) {
		this.range = range;
	}

}
