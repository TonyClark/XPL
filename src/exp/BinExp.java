package exp;

import values.Value;
import context.Context;
import grammar.Grammar;

@BoaConstructor(fields = { "left", "name", "right" })
public class BinExp extends Exp {

	public Exp		left;
	public String	name;
	public Exp		right;

	public BinExp() {
	}

	public BinExp(Exp left, String name, Exp right) {
		super();
		this.left = left;
		this.name = name;
		this.right = right;
	}

	public Value eval(Context context) {
		Value v1 = left.eval(context);
		Value v2 = right.eval(context);
		if (name.equals("+")) {
			if ((v1 instanceof values.Int) && (v2 instanceof values.Int)) {
				values.Int i1 = (values.Int) v1;
				values.Int i2 = (values.Int) v2;
				return new values.Int(i1.getValue() + i2.getValue());
			} else if ((v1 instanceof Grammar) && (v2 instanceof Grammar)) {
				Grammar g1 = (Grammar) v1;
				Grammar g2 = (Grammar) v2;
				return g1.add(g2);
			} else if((v1 instanceof values.Record) && (v2 instanceof values.Record)) {
				values.Record r1 = (values.Record)v1;
				values.Record r2 = (values.Record)v2;
				return r1.add(r2);
			} else if ((v1 instanceof values.Str) || (v2 instanceof values.Str)) {
				return new values.Str(v1.toString() + v2.toString());
			} else if ((v1 instanceof values.List) && (v2 instanceof values.List)) {
				values.List l1 = (values.List)v1;
				values.List l2 = (values.List)v2;
				return l1.append(l2);
			}
			else throw new Error("Illegal args for +: " + v1 + " " + v2);
		}
		if (name.equals("-")) {
			if ((v1 instanceof values.Int) && (v2 instanceof values.Int)) {
				values.Int i1 = (values.Int) v1;
				values.Int i2 = (values.Int) v2;
				return new values.Int(i1.getValue() - i2.getValue());
			} else
				throw new Error("Illegal args for -: " + v1 + " " + v2);
		}
		if (name.equals("<")) {
			if ((v1 instanceof values.Int) && (v2 instanceof values.Int)) {
				values.Int i1 = (values.Int) v1;
				values.Int i2 = (values.Int) v2;
				return new values.Bool(i1.getValue() < i2.getValue());
			} else
				throw new Error("Illegal args for <: " + v1 + " " + v2);
		}
		if (name.equals(">")) {
			if ((v1 instanceof values.Int) && (v2 instanceof values.Int)) {
				values.Int i1 = (values.Int) v1;
				values.Int i2 = (values.Int) v2;
				return new values.Bool(i1.getValue() > i2.getValue());
			} else
				throw new Error("Illegal args for >: " + v1 + " " + v2);
		}
		if (name.equals("*")) {
			if ((v1 instanceof values.Int) && (v2 instanceof values.Int)) {
				values.Int i1 = (values.Int) v1;
				values.Int i2 = (values.Int) v2;
				return new values.Int(i1.getValue() * i2.getValue());
			} else
				throw new Error("Illegal args for *: " + v1 + " " + v2);
		}
		if (name.equals("/")) {
			if ((v1 instanceof values.Int) && (v2 instanceof values.Int)) {
				values.Int i1 = (values.Int) v1;
				values.Int i2 = (values.Int) v2;
				return new values.Int(i1.getValue() / i2.getValue());
			} else
				throw new Error("Illegal args for *: " + v1 + " " + v2);
		}
		if (name.equals("and")) {
			if ((v1 instanceof values.Bool) && (v2 instanceof values.Bool)) {
				values.Bool i1 = (values.Bool) v1;
				values.Bool i2 = (values.Bool) v2;
				return new values.Bool(i1.getValue() && i2.getValue());
			} else
				throw new Error("Illegal args for *: " + v1 + " " + v2);
		}
		if (name.equals("or")) {
			if ((v1 instanceof values.Bool) && (v2 instanceof values.Bool)) {
				values.Bool i1 = (values.Bool) v1;
				values.Bool i2 = (values.Bool) v2;
				return new values.Bool(i1.getValue() || i2.getValue());
			} else
				throw new Error("Illegal args for *: " + v1 + " " + v2);
		}
		if (name.equals("="))
			return new values.Bool(v1.equals(v2));
		if (name.equals("!="))
			return new values.Bool(!v1.equals(v2));
		if (name.equals(":")) {
			if (v2 instanceof values.List)
				return values.List.cons(v1, (values.List) v2);
		}
		throw new Error("Unknown binary operator " + name);
	}

	public String pprint(int opPrec) {
		if(opPrec <= opPrec())
			return "(" + left.pprint(opPrec()) + " " + name + " " + right.pprint(opPrec()) + ")";
		else return left.pprint(opPrec()) + " " + name + " " + right.pprint(opPrec());
	}

	private int opPrec() {
		if(name.equals("and"))
			return ANDOP;
		if(name.equals("or"))
			return OROP;
		if(name.equals("<"))
			return LESSOP;
		if(name.equals(">"))
			return GREOP;
		if(name.equals("="))
			return EQOP;
		if(name.equals("!="))
			return NEQOP;
		if(name.equals("+"))
			return ADDOP;
		if(name.equals("*"))
			return MULOP;
		if(name.equals("-"))
			return SUBOP;
		if(name.equals("/"))
			return DIVOP;
		if(name.equals(":"))
			return CONSOP;
		else throw new Error("Unkown operator: " + name);
	}

}
