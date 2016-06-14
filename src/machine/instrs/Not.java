package machine.instrs;

import java.util.HashSet;
import java.util.Stack;

import grammar.PTerm;
import machine.Fail;
import machine.Machine;

public class Not extends Instr {

	private Stack<Fail>	fails;
	private PTerm				term;

	public Not(Stack<Fail> fails, PTerm term) {
		this.fails = fails;
		this.term = term;
	}

	public void exec(Machine machine) {
		String terminals = "";
		for(String key : term.predictors(machine.getEnv(), new HashSet<String>()))
			terminals = terminals + "\n  " + key;
		machine.error(machine,  "The following terminals:" + terminals + "\ncannot occur here.");
		machine.setFails(fails);
		machine.fail(false);
	}

}
