package values.xobj;

import java.util.Vector;

import values.Value;

public class XObj extends Value {

	private XClass			type;
	private Vector<XSlot>	slots	= new Vector<XSlot>();

	public XObj(XClass type) {
		super();
		this.type = type;
	}

	public XClass getType() {
		return type;
	}

	public void setType(XClass type) {
		this.type = type;
	}

	public Vector<XSlot> getSlots() {
		return slots;
	}

	public void setSlots(Vector<XSlot> slots) {
		this.slots = slots;
	}

	public XObj addSlot(String name, Object value) {
		slots.add(new XSlot(name, value));
		return this;
	}

	public Object sendx(String message, Object... args) {
		return type.sendx(this, message, args);
	}

}
