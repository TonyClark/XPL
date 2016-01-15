package commands;

import machine.Machine;

public class Quit extends Command {

  public boolean perform(Machine machine) {
    return true;
  }

}
