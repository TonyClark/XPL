package exp;

import values.Value;
import context.Context;

@BoaConstructor(fields = { "target", "name", "args" })
public class Send extends Exp {

  public Exp    target;
  public String name;
  public Exp[]  args;

  public Send() {
  }

  public Send(Exp target, String name, Exp... args) {
	super();
	this.target = target;
	this.name = name;
	this.args = args;
  }

  public Value eval(Context context) {
	Value value = target.eval(context);
	Value[] values = new Value[args.length];
	for (int i = 0; i < args.length; i++)
	  values[i] = args[i].eval(context);
	return value.send(name, values);
  }

  public String pprint(int opPrec) {
	if (opPrec <= APPOP)
	  return "(" + target.pprint(APPOP) + "." + name + pprintArgs() + ")";
	else return target.pprint(APPOP) + "." + name + pprintArgs();
  }

  public String pprintArgs() {
	String s = "(";
	for (Exp arg : args) {
	  s = s + arg.pprint(MAXOP);
	  if (arg != args[args.length - 1]) s = s + ",";
	}
	return s + ")";
  }

}
