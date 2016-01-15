package values;

import exp.BoaConstructor;

@BoaConstructor(fields = { "values" })
public class List extends Value {

  public static final List NIL = new List();

  public Value[]           values;

  public List() {
	values = new Value[0];
  }

  public List(Value... values) {
	this.values = values;
  }

  public int size() {
	return values.length;
  }

  public Value elementAt(int i) {
	return values[i];
  }

  public static List cons(Value head, List tail) {
	Value[] values = new Value[tail.size() + 1];
	values[0] = head;
	for (int i = 0; i < tail.size(); i++)
	  values[i + 1] = tail.elementAt(i);
	return new List(values);
  }

  public List tail() {
	if (values.length == 0)
	  throw new Error("Cannot take tail.");
	else {
	  Value[] l = new Value[values.length - 1];
	  for (int i = 1; i < values.length; i++)
		l[i - 1] = values[i];
	  return new List(l);
	}
  }

  public String toString() {
	String s = "[";
	for (Value v : values) {
	  s = s + v;
	  if (values[values.length - 1] != v) s = s + ",";
	}
	return s + "]";
  }

  public Value getHead() {
	return values[0];
  }

  public List getTail() {
	Value[] tail = new Value[values.length - 1];
	for (int i = 1; i < values.length; i++)
	  tail[i - 1] = values[i];
	return new List(tail);
  }

  public Value append(List l2) {
	Value[] elements = new Value[size() + l2.size()];
	for (int i = 0; i < size(); i++)
	  elements[i] = elementAt(i);
	for (int i = 0; i < l2.size(); i++)
	  elements[i + size()] = l2.elementAt(i);
	return new List(elements);
  }

}
