package commands;

import exp.BoaConstructor;
import machine.Machine;

@BoaConstructor(fields = { "exp" })
public class Exp extends Command {
	
	public exp.Exp exp;

  public boolean perform(Machine machine) {
    System.out.println(exp.eval(machine));
    return false;
  }

}
