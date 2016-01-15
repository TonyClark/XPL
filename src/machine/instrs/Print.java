package machine.instrs;

import machine.Machine;

public class Print extends Instr {
	
	private String message;
	
	public Print(String message) {
		
	this.message = message;
	}

	public void exec(Machine machine) {
		machine.debug(message);
	}

}
