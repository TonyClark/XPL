package exp;

import values.Value;
import context.Context;

@BoaConstructor(fields = { "name", "value" })
public class DroppedSingletonRecord extends Exp {

  public Exp name;
  public Exp value;

  public DroppedSingletonRecord() {
  }

  public DroppedSingletonRecord(Exp name, Exp value) {
	super();
	this.name = name;
	this.value = value;
  }

  public Exp getName() {
	return name;
  }

  public void setName(Exp name) {
	this.name = name;
  }

  public Exp getValue() {
	return value;
  }

  public void setValue(Exp value) {
	this.value = value;
  }

  public Value eval(Context context) {
	throw new Error("cannot evaluate a dropped singleton record.");
  }

  public String pprint(int opPrec) {
	return "{ ${" + name.pprint(Exp.MAXOP) + "} =" + value.pprint(Exp.MAXOP) + "}";
  }

  public boolean isAtom() {
	return false;
  }

  public Exp lift() {
	return new Apply(new Str("exp.Record"), new List(new Apply(new Str("exp.Field"), name, value.lift())));
  }

}
