package machine.instrs;

import machine.Machine;
import values.Value;
import context.Context;
import exp.Exp;

public abstract class Instr extends Exp {

	public abstract void exec(Machine machine);

	public Value eval(Context context) {
		throw new Error("Should not evaluate an instruction.");
	}
	
	public String pprint(int opPrec) {
		return this.toString();
	}

}
