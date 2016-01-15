package modules;

import exp.Binding;
import exp.BoaConstructor;
import exp.Exp;
import exp.Field;
import exp.Letrec;
import exp.Record;
import exp.Var;

@BoaConstructor(fields = { "name","defs" })
public class ModuleBinding extends Binding {

	public ModuleDef[]	defs;

	public Exp getValue() {
		return new exp.Import(imports(), new Letrec(bindings(), record()));
	}

	public Exp record() {
		String[] names = names();
		Field[] fields = new Field[names.length];
		for (int i = 0; i < names.length; i++)
			fields[i] = new Field(names[i], new Var(names[i]));
		return new Record(fields);
	}

	public String[] names() {
		int i = 0;
		for (ModuleDef d : defs)
			if (d instanceof Export) {
				Export export = (Export) d;
				i = i + export.names.length;
			}
		String[] names = new String[i];
		i = 0;
		for (ModuleDef d : defs)
			if (d instanceof Export)
				for (String name : ((Export) d).names)
					names[i++] = name;
		return names;
	}

	public Exp[] imports() {
		int i = 0;
		for (ModuleDef d : defs)
			if (d instanceof Import) {
				Import import_ = (Import) d;
				i = i + import_.packages.length;
			}
		Exp[] imports = new Exp[i];
		i = 0;
		for (ModuleDef d : defs)
			if (d instanceof Import)
				for (Exp exp : ((Import) d).packages)
					imports[i++] = exp;
		return imports;
	}

	public Binding[] bindings() {
		int i = 0;
		for (ModuleDef d : defs)
			if (d instanceof Bind) {
				i++;
			}
		Binding[] bindings = new Binding[i];
		i = 0;
		for (ModuleDef d : defs)
			if (d instanceof Bind)
				bindings[i++] = ((Bind) d).binding;
		return bindings;
	}

	public String pprint() {
		return "MODULE";
	}

}
