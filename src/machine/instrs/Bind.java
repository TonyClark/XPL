package machine.instrs;

import machine.Machine;
import patterns.Pattern;

public class Bind extends Instr {
	
	private Pattern pattern;

	public Bind(Pattern pattern) {
		super();
		this.pattern = pattern;
	}

	public void exec(Machine machine) {
		if(!pattern.match(machine.topValue(), machine))
			machine.fail(false);
	}

}
