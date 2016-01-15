package machine.instrs;

import machine.Machine;

public class Newline extends Instr {

	public void exec(Machine machine) {
		machine.newline();
	}

}
