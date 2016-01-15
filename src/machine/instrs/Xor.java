package machine.instrs;

import java.util.Stack;

import machine.Fail;
import machine.Machine;

public class Xor extends Instr {
	
	private Stack<Fail> fails;

	public Xor(Stack<Fail> fails) {
		super();
		this.fails = fails;
	}

	public void exec(Machine machine) {
		machine.setFails(fails);
	}

}
