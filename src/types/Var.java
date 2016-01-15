package types;

import java.util.Stack;

public class Var extends Type {

	public static int			counter			= 0;

	private static boolean		savingGenerics	= false;

	private String				id				= "v" + counter++;

	private boolean				generic			= false;

	private static Stack<Var>	stack			= new Stack<Var>();

	public Var() {
		super();
		if (savingGenerics)
			stack.push(this);
	}

	public Var(boolean generic) {
		this();
		this.generic = generic;
	}

	public static void startGeneric() {
		savingGenerics = true;
	}

	public static void endGeneric() {
		savingGenerics = false;
		while (!stack.isEmpty())
			stack.pop().setGeneric(true);
	}

	public static int getCounter() {
		return counter;
	}

	public static void setCounter(int counter) {
		Var.counter = counter;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isGeneric() {
		return generic;
	}

	public void setGeneric(boolean generic) {
		this.generic = generic;
	}

	public String toString() {
		return "Var(" + id + "," + generic + ")";
	}
}
