package values;

import context.Context;
import exp.BoaConstructor;
import exp.Exp;

@BoaConstructor(fields = { "args","context","body"})
public class Closure extends Value {
	
	public String[] args;
	public Context context;
	public Exp body;

	public Closure(String[] args, Context context, Exp body) {
		super();
		this.args = args;
		this.context = context;
		this.body = body;
	}

	public String[] getArgs() {
		return args;
	}

	public void setArgs(String[] args) {
		this.args = args;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public Exp getBody() {
		return body;
	}

	public void setBody(Exp body) {
		this.body = body;
	}

	public Value apply(Value[] values) {
		Context copy = context.copy();
		for(int i = 0; i < values.length;i++)
			copy.bind(args[i], values[i]);
		return body.eval(copy);
	}
	
	public String toString() {
		return "<closure>";
	}

}
