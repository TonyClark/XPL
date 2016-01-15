package values.xobj;

import values.Closure;

public class XMethod {

	private String	name;
	private Closure	closure;

	public XMethod(String name, Closure closure) {
		super();
		this.name = name;
		this.closure = closure;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Closure getClosure() {
		return closure;
	}

	public void setClosure(Closure closure) {
		this.closure = closure;
	}

}
