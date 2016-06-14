package exp;

import grammar.Action;
import grammar.Bind;
import grammar.Call;
import grammar.EOF;
import grammar.Grammar;
import grammar.Seq;
import machine.Machine;
import values.Value;
import context.Context;
import context.StringSource;

@BoaConstructor(fields = { "text", "grammar" })
public class LClosure extends PClosure {

	public LClosure(String text, Exp grammar) {
		super(text, grammar);
	}

	public Value eval(Context evalContext) {
		Grammar g = (Grammar) grammar.eval(evalContext);
		Machine.reset();
		Machine machine = new Machine(g, evalContext.getEnv(), (java.util.Stack<Integer>) evalContext.getLines().clone(), new StringSource(text), 0, null, 0);
		machine.pushInstr(new Seq(new Bind(new patterns.Var("x"), new Call(g.start)), new EOF(), new Action(new Var("x"))));
		Value value = machine.run();
		if (machine.isOk())
			return value.lift().eval(evalContext);
		else throw new Error(machine.getError());
	}

	public String pprint(int opPrec) {
		return "[ " + grammar.pprint(MAXOP) + " | " + text + " |]";
	}

}
