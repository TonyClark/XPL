package commands;

import exp.BoaConstructor;
import exp.Exp;
import machine.Machine;
import values.Value;

@BoaConstructor(fields = { "exps" })
public class Import extends Command {

  public exp.Exp[] exps;

  public boolean perform(Machine machine) {
    for (Exp e : exps) {
      Value value = e.eval(machine);
      values.Record record = exp.Import.getRecord(value);
      for (values.Field field : record.fields)
        machine.bind(field.name, field.value);
    }
    return false;
  }

}
