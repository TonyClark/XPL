package commands;

import exp.BoaConstructor;
import machine.Machine;
import values.Value;

@BoaConstructor(fields = { "name", "value" })
public class Bind extends Command {

  public String  name;
  public exp.Exp value;

  public boolean perform(Machine machine) {
    Value v = value.eval(machine);
    machine.bind(name, v);
    System.out.println(name + " => " + v);
    return false;
  }

}
