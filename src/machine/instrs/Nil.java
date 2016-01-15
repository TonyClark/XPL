package machine.instrs;

import machine.Machine;
import values.List;

public class Nil extends Instr {

	public void exec(Machine machine) {
		machine.pushValue(List.NIL);
	}

}
