package machine.instrs;

import java.util.Stack;

import machine.Fail;
import machine.Machine;

public class Not extends Instr {

  private Stack<Fail> fails;

  public Not(Stack<Fail> fails) {
	this.fails = fails;
  }

  public void exec(Machine machine) {
	machine.error(machine, "negated term should not be satisfied");
	machine.setFails(fails);
	machine.fail(false);
  }

}
