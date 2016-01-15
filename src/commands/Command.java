package commands;

import machine.Machine;
import values.Value;

public abstract class Command extends Value {

  public abstract boolean perform(Machine machine);

}
