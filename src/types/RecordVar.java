package types;

import collections.Set;

public class RecordVar extends Record {

	private Set<String>	excludedNames	= new Set<String>();

	private Var			var;

	public RecordVar(Var var, String... excludedNames) {
		super();
		for (String name : excludedNames)
			this.excludedNames.add(name);
		this.var = var;
	}

	public RecordVar(Var var, Set<String> excludedNames) {
		super();
		this.var = var;
		this.excludedNames = excludedNames;
	}

	public Set<String> getExcludedNames() {
		return excludedNames;
	}

	public void setExcludedNames(Set<String> excludedNames) {
		this.excludedNames = excludedNames;
	}

	public Var getVar() {
		return var;
	}

	public void setVar(Var var) {
		this.var = var;
	}

}
