package machine.instrs;

import machine.Machine;

public class Dedent extends Instr {

	public void exec(Machine machine) {
		machine.dedent();
	}

}
