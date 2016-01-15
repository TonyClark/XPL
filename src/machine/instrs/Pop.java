package machine.instrs;

import machine.Machine;

public class Pop extends Instr {

	public void exec(Machine machine) {
		machine.popValue();
	}

}
