package types;


public class Field extends Record {
	
	private String name;
	private Type type;
	private Record record;

	public Field(String name, Type type, Record record) {
		super();
		this.name = name;
		this.type = type;
		this.record = record;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Record getRecord() {
		return record;
	}

	public void setRecord(Record record) {
		this.record = record;
	}

}
