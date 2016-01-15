package exp;

import machine.Machine;
import modules.ModuleBinding;
import values.Record;
import values.Value;
import xpl.XPL;
import context.Context;
import grammar.Call;
import grammar.Grammar;

@BoaConstructor(fields = { "packages", "body" })
public class Import extends Exp {

	public Exp[]	packages;
	public Exp		body;

	public Import() {
	}

	public Import(Exp[] packages, Exp body) {
		super();
		this.packages = packages;
		this.body = body;
	}

	public Value eval(Context context) {
		values.Record[] records = new values.Record[packages.length];
		for (int i = 0; i < packages.length; i++)
			records[i] = getRecord(packages[i].eval(context));
		Context copy = context.copy();
		for (values.Record record : records)
			for (values.Field field : record.fields)
				copy.bind(field.name, field.value);
		return body.eval(copy);
	}

	public static Record getRecord(Value value) {
		if (value instanceof values.Str)
			return getFile((values.Str) value);
		if (value instanceof Record)
			return (Record) value;
		throw new Error("Cannot import " + value);
	}

	public static Record getFile(values.Str str) {
		String name = str.value;
		return getFile(name);
	}
	
	public static Record getFile(String name) {
		Machine machine = new Machine(null, Value.builtinEnv, Context.readFile(name), 0, null,0);
		Grammar grammar = (Grammar) XPL.XPL; 
		machine.setGrammar(grammar);
		machine.pushInstr(new Call("file",new Apply("exp.BindingNameLiteral",new Str(name))));
		long start = System.currentTimeMillis();
		System.out.print("[" + name);
		Value value = machine.run();
		System.out.println(" " + (System.currentTimeMillis() - start) + " ms," + machine.getMaxFailDepth() + "]");
		if (machine.isOk()) {
			ModuleBinding b = (ModuleBinding) value;
			return (Record) b.getValue().eval(machine);
		} else
			throw new Error(machine.getError());
	}

	public String pprint(int opPrec) {
		String s = "import ";
		for(Exp e : packages) {
			s = s + e.pprint(MAXOP);
			if(e != packages[packages.length-1])
				s = s + ",";
		}
		return s + "}";
	}

}
