package values;

import java.util.Vector;

public class XClass extends Value {
	
	private String name;
	private Vector<XAtt> attributes = new Vector<XAtt>();
	private Vector<XMethod> methods = new Vector<XMethod>();
	
	public XClass(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Vector<XAtt> getAttributes() {
		return attributes;
	}

	public void setAttributes(Vector<XAtt> attributes) {
		this.attributes = attributes;
	}

	public Vector<XMethod> getMethods() {
		return methods;
	}

	public void setMethods(Vector<XMethod> methods) {
		this.methods = methods;
	}
	
	public XClass addAttribute(String name,Thunk value) {
		attributes.add(new XAtt(name,value));
		return this;
	}
	
	public XClass addMethod(String name, Closure closure) {
		methods.add(new XMethod(name,closure));
		return this;
	}
	
	public XObj newObj() {
		XObj obj = new XObj(this);
		for(XAtt att : attributes)
			obj.addSlot(att.getName(),Value.toJava(att.eval(),Object.class));
		return obj;
	}
	

}
