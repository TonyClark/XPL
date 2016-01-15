package xpl;

import machine.Machine;
import values.Record;
import values.Value;

import commands.Command;
import commands.Quit;

import context.Context;
import context.TopLevel;
import env.Env;
import exp.Exp;
import exp.Import;
import grammar.Call;
import grammar.Grammar;

public class Interpreter {

  public static void main(String[] args) {
    Env<String, Value> env = Value.builtinEnv;
    Machine machine = new Machine(null, env, new TopLevel(System.in), 0, null, 0);
    XPL.XPL = (Grammar) XPL.XPL.eval(machine);
    Record xpl = Import.getFile("../xpl/src/xpl/xpl.xpl");
    XPL.setXPL((Grammar) xpl.dot("XPL"));
    if (args.length == 2) XPL.setXPL(readLanguage(machine, args[0], args[1]));
    boolean quit = false;
    while (!quit) {
      try {
        Context.reset();
        machine = new Machine(XPL.XPL, env, new TopLevel(System.in), 0, null, 0);
        machine.bind("XPL", XPL.XPL);
        System.out.print("> ");
        machine.pushInstr(new Call("topLevelCommand"));
        Command command = (Command) machine.run();
        if (machine.isOk())
          quit = command.perform(machine);
        else System.out.println(machine.getError());
        env = machine.getEnv();
        XPL.setXPL((Grammar) machine.lookup("XPL"));
      } catch (Error e) {
        e.printStackTrace();
        System.out.println("error: " + e.getCause());
      }
    }
    System.out.println("...terminated");
  }

  private static Grammar readLanguage(Machine machine, String file, String languageName) {
    values.Record record = Import.getFile(file);
    for (values.Field field : record.fields)
      machine.bind(field.name, field.value);
    return (Grammar) record.getField(languageName).value;
  }

}
