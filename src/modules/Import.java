package modules;

import exp.BoaConstructor;
import exp.Exp;

@BoaConstructor(fields = { "packages" })
public class Import extends ModuleDef {
	
	public Exp[] packages;
	
	public Import() {}

}
