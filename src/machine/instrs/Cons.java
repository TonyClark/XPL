package machine.instrs;

import machine.Machine;
import values.List;
import values.Value;

public class Cons extends Instr {

	public static final Instr	CONS	= new Cons();

	public void exec(Machine machine) {
		List list = (List)machine.popValue();
		Value value = machine.popValue();
		machine.pushValue(List.cons(value, list));
	}

}
