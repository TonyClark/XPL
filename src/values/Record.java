package values;

import env.Bind;
import env.Empty;
import env.Env;
import exp.BoaConstructor;

@BoaConstructor(fields = { "fields" })
public class Record extends Value {

  public Field[] fields;

  public Record() {
  }

  public Record(Field[] fields) {
	super();
	this.fields = fields;
  }

  public Field getSafeField(String name) {
	for (Field f : fields)
	  if (f.name.equals(name)) return f;
	throw new Error("Cannot find field " + name);
  }

  public Field getField(String name) {
	for (Field f : fields)
	  if (f.name.equals(name)) return f;
	return null;
  }

  public Value send(String name, Value[] values) {
	values.Field field = getField(name);
	if (field != null) {
	  if (field.value instanceof values.Closure) {
		values.Closure closure = (values.Closure) field.value;
		return closure.apply(values);
	  } else throw new Error("Cannot send a message to " + field.value);
	} else return super.send(name, values);
  }

  public Value dot(String name) {
	Field field = getSafeField(name);
	return field.value;
  }

  public String toString() {
	String s = "{";
	for (Field field : fields) {
	  s = s + field;
	  if (fields[fields.length - 1] != field) s = s + ";";
	}
	return s + "}";
  }

  public boolean hasField(String name) {
	for (Field f : fields)
	  if (f.name.equals(name)) return true;
	return false;
  }

  public Record add(Record r2) {
	Field[] newFields = new Field[fields.length + r2.fields.length];
	for (int i = 0; i < fields.length; i++)
	  newFields[i] = fields[i];
	for (int i = 0; i < r2.fields.length; i++)
	  newFields[i + fields.length] = r2.fields[i];
	return new Record(newFields);

  }

  public Record subst(Str name, Value value) {
	return add(new Record(new Field[] { new Field(name.value, value) }));
  }

  public Env<String, Value> asEnv() {
	Env<String, Value> env = new Empty<String, Value>();
	for (Field f : fields)
	  env = new Bind<String, Value>(f.name, f.value, env);
	return env;
  }

}
