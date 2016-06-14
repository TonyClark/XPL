package machine.instrs;

import machine.Machine;

public class PopLine extends Instr {

	public static PopLine POP = new PopLine();

	public void exec(Machine machine) {
		machine.popLinePos();
	}

}
