package exp;

import machine.Machine;
import values.Value;
import context.Context;
import context.StringSource;
import grammar.Action;
import grammar.Bind;
import grammar.Call;
import grammar.EOF;
import grammar.Grammar;
import grammar.Seq;

@BoaConstructor(fields = { "text", "grammar" })
public class PClosure extends Exp {

	String	text;
	Exp		grammar;

	public PClosure(String text, Exp grammar) {
		super();
		this.text = text;
		this.grammar = grammar;
	}

	public Value eval(Context evalContext) {
		Grammar g = (Grammar) grammar.eval(evalContext);
		Machine.reset();
		Machine machine = new Machine(g, evalContext.getEnv(), new StringSource(text), 0, null, 0);
		machine.pushInstr(new Seq(new Bind(new patterns.Var("x"), new Call(g.start)), new EOF(), new Action(new Var("x"))));
		Value value = machine.run();
		if (machine.isOk())
			return value.send("eval", new Value[] { evalContext });
		else
			throw new Error(machine.getError());
	}

	public String pprint(int opPrec) {
		return "intern " + grammar.pprint(MAXOP) + "{" + text + "}";
	}
}
