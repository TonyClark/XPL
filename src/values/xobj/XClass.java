package values.xobj;

import java.util.Vector;

import values.Closure;
import values.Thunk;
import values.Value;
import context.Context;

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
	
	public XMethod getMethod(String name) {
		for(XMethod method : methods)
			if(method.getName().equals(name))
				return method;
		return null;
	}
	
	public XObj newObj() {
		XObj obj = new XObj(this);
		for(XAtt att : attributes)
			obj.addSlot(att.getName(),Value.toJava(att.eval(),Object.class));
		return obj;
	}
	
	public Object sendx(XObj target,String message,Object... args) {
		XMethod method = getMethod(message);
		if(method == null)
			throw new Error(target + " cannot handle message " + message);
		Context context = method.getClosure().getContext().copy();
		String[] names = method.getClosure().getArgs();
		if(names.length != args.length)
			throw new Error("Message " + message + " expects " + names.length + " args but received " + args.length);
		for(XSlot slot : target.getSlots())
			context.bind(slot.getName(), Value.toValue(slot.getValue()));
		for(int i = 0; i < names.length; i++)
			context.bind(names[i],Value.toValue(args[i]));
		context.bind("self",target);
		return method.getClosure().getBody().eval(context);
	}
	

}
