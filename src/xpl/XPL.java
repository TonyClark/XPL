package xpl;

import exp.Apply;
import exp.Cons;
import exp.Send;
import exp.Var;
import grammar.Action;
import grammar.Bind;
import grammar.Call;
import grammar.Char;
import grammar.Cut;
import grammar.DisjointCalls;
import grammar.Dot;
import grammar.EOF;
import grammar.Grammar;
import grammar.Not;
import grammar.Or;
import grammar.PDelay;
import grammar.Plus;
import grammar.Range;
import grammar.Rule;
import grammar.Seq;
import grammar.Star;
import grammar.Term;

import java.util.Hashtable;

import values.xobj.XClass;

public class XPL {
	
	public static Hashtable<String,XClass> classes = new Hashtable<String,XClass>();
	
	public static Grammar Exp = new Grammar("Exp",
			new Rule("file",new Seq(new Bind("ds",new Star(new Seq(new Bind("m",new Call("ModuleDef")),new Cut(),new Action(new Var("m"))))),new EOF(),new Action(new Apply("modules.ModuleBinding",new Var("name"),new Var("ds")))),new patterns.Var("name")),
			new Rule("WhiteSpace",new Seq(new Star(new Or(new Char(' '),new Char('\n'),new Char('\r'),new Char('\t'))),new Cut())),
			new Rule("Comment",new Seq(new Term("//"),new Star(new Seq(new Not(new Char('\n')),new Dot())))),
			new Rule("TopLevelCommand",new Seq(new Call("WhiteSpace"),new Bind("d",new Call("Command")),new Term(";"),new Action(new Var("d")))),
			new Rule("Command",new DisjointCalls("TplvlQuit","TplvlExp","TplvlImport","TplvlBind")),
			new Rule("TplvlBind",new Seq(new Bind("n",new Call("Name")),new Call("WhiteSpace"),new Term(":="),new Bind("e",new Call("Exp")),new Action(new Apply("commands.Bind",new Var("n"),new Var("e"))))),
			new Rule("TplvlQuit",new Seq(new Term("quit"),new Action(new Apply("commands.Quit")))),
			new Rule("TplvlExp",new Seq(new Bind("e",new Call("Exp")),new Action(new Apply("commands.Exp",new Var("e"))))),
			new Rule("TplvlImport",new Seq(new Term("import"),new Bind("ns",new Call("Args")),new Action(new Apply("commands.Import",new Var("ns"))))),
			new Rule("Exp",new DisjointCalls("Let","Letrec","If","Intern","Fun","Bool","Comment")),
			new Rule("Let",new Seq(new Term("let"),new Bind("bs",new Star(new Call("Binding"))),new Term("in"),new Bind("b",new Call("Exp")),new Action(new Apply("exp.Let",new Var("bs"),new Var("b"))))),
			new Rule("Letrec",new Seq(new Term("letrec"),new Bind("bs",new Star(new Call("Binding"))),new Term("in"),new Bind("b",new Call("Exp")),new Action(new Apply("exp.Letrec",new Var("bs"),new Var("b"))))),
			new Rule("Binding",new Or(new Seq(new Call("Comment"),new Call("Binding")),new Or(new Call("ModuleBinding"),new Call("ValBinding"),new Call("FunBinding")))),
			new Rule("ValBinding",new Seq(new Bind("n",new Call("BindingName")),new Term("="),new Bind("v",new Call("Exp")),new Action(new Apply("exp.ValueBinding",new Var("n"),new Var("v"))))),
			new Rule("FunBinding",new Seq(new Bind("n",new Call("BindingName")),new Term("("),new Bind("as",new Call("ArgNames")),new Term(")"),new Term("="),new Bind("v",new Call("Exp")),new Action(new Apply("exp.ValueBinding",new Var("n"),new Apply("exp.Lambda",new Var("as"),new Var("v")))))),
			new Rule("If",new Seq(new Term("if"),new Bind("t",new Call("Exp")),new Term("then"),new Bind("c",new Call("Exp")),new Term("else"),new Bind("a",new Call("Exp")),new Action(new Apply("exp.If",new Var("t"),new Var("c"),new Var("a"))))),
			new Rule("Record",new Seq(new Term("{"),new Bind("fs",new Call("Fields")),new Term("}"),new Action(new Apply("exp.Record",new Var("fs"))))),
			new Rule("Fields",new Or(new Call("SomeFields"),new Action(new exp.List()))),
			new Rule("SomeFields",new Seq(new Bind("f",new Call("Field")),new Bind("fs",new Star(new Seq(new Term(";"),new Call("Field")))),new Action(new exp.Cons(new Var("f"),new Var("fs"))))),
			new Rule("Field",new Or(new Call("FunField"),new Call("ValueField"))),
			new Rule("ValueField",new Seq(new Bind("n",new Call("Name")),new Term("="),new Bind("e",new Call("Exp")),new Action(new Apply("exp.Field",new Var("n"),new Var("e"))))),
			new Rule("FunField",new Seq(new Bind("n",new Call("Name")),new Term("("),new Bind("as",new Call("ArgNames")),new Term(")"),new Term("="),new Bind("e",new Call("Exp")),new Action(new Apply("exp.Field",new Var("n"),new Apply("exp.Lambda",new Var("as"),new Var("e")))))),
			new Rule("Intern",new Seq(new Term("intern"),new Bind("g",new Call("Exp")),new PDelay("{","}",new Var("g")))),
			new Rule("BindingName",new Or(new Seq(new Bind("n",new Call("Name")),new Action(new Apply("exp.BindingNameLiteral",new Var("n")))),new Seq(new Bind("d",new Call("Drop")),new Action(new Apply("exp.BindingNameDrop",new Var("d")))))),
			new Rule("Fun",new Seq(new Term("fun"),new Term("("),new Bind("as",new Call("ArgNames")),new Term(")"),new Bind("b",new Call("Exp")),new Action(new Apply("exp.Lambda",new Var("as"),new Var("b"))))),
			new Rule("Bool",new Seq(new Bind("l",new Call("Compare")),new Or(new Seq(new Bind("o",new Call("BoolOp")),new Bind("r",new Call("Bool")),new Action(new Apply("exp.BinExp",new Var("l"),new Var("o"),new Var("r")))),new Action(new Var("l"))))),
			new Rule("Compare",new Seq(new Bind("l",new Call("Arith")),new Or(new Seq(new Bind("o",new Call("CompareOp")),new Bind("r",new Call("Compare")),new Action(new Apply("exp.BinExp",new Var("l"),new Var("o"),new Var("r")))),new Action(new Var("l"))))),
			new Rule("Arith",new Seq(new Bind("l",new Call("Apply")),new Or(new Seq(new Bind("o",new Call("ArithOp")),new Bind("r",new Call("Arith")),new Action(new Apply("exp.BinExp",new Var("l"),new Var("o"),new Var("r")))),new Action(new Var("l"))))),
			new Rule("BoolOp",new Or(new Term("and"),new Term("or"))),
			new Rule("CompareOp",new Or(new Term("<"),new Term(">"),new Term("="),new Term("!="), new Term(":"))),
			new Rule("ArithOp",new Or(new Term("+"),new Term("-"),new Term("*"),new Term("/"))),
			new Rule("Apply",new Or(new Call("ValueApply"),new Call("Send"))),
			new Rule("ValueApply",new Seq(new Bind("a",new Call("Atom")),new Term("("),new Bind("as",new Call("Args")),new Term(")"),new Action(new Apply("exp.Apply",new Var("a"),new Var("as"))))),
			new Rule("Send",new Seq(new Bind("t",new Call("Atom")),new Call("SendTail",new exp.Var("t")))),
			new Rule("SendTail",new Or(new Seq(new Bind("t",new Call("SendMessage",new exp.Var("a"))),new Call("SendTail",new exp.Var("t"))),new Action(new Var("a"))),new patterns.Var("a")),
			new Rule("SendMessage",new Seq(new Term("."),new Bind("n",new Call("Name")),new Call("SendOrDot",new exp.Var("t"),new exp.Var("n"))),new patterns.Var("t")),
			new Rule("SendOrDot",new Or(new Seq(new Term("("),new Bind("as",new Call("Args")),new Term(")"),new Action(new Apply("exp.Send",new Var("t"),new Var("n"),new Var("as")))),new Action(new Apply("exp.Dot",new Var("t"),new Var("n")))),new patterns.Var("t"),new patterns.Var("n")),
			new Rule("Args",new Or(new Call("SomeArgs"),new Action(new exp.List()))),
			new Rule("SomeArgs",new Seq(new Bind("a",new Call("Exp")),new Bind("as",new Star(new Seq(new Term(","),new Call("Exp")))),new Action(new Cons(new Var("a"),new Var("as"))))),
			new Rule("ArgNames",new Or(new Call("SomeArgNames"),new Action(new exp.List()))),
			new Rule("SomeArgNames",new Seq(new Bind("a",new Call("Name")),new Bind("as",new Star(new Seq(new Term(","),new Call("Name")))),new Action(new Cons(new Var("a"),new Var("as"))))),
			new Rule("Atom",new DisjointCalls("Thunk","Case","Import","True","Record","Grammar","False","Lift","Drop","List","Reify","Var","IntExp","StrExp","ParenExp")),
			new Rule("Reify",new Seq(new Term("reify"),new Action(new Apply("exp.Reify")))),
			new Rule("Thunk",new Seq(new Call("WhiteSpace"),new Term("thunk"),new Term("("),new Bind("e",new Call("Exp")),new Term(")"),new Action(new Apply("exp.Thunk",new Var("e"))))),
			new Rule("Lift",new Seq(new Term("[|"),new Bind("e",new Call("Exp")),new Term("|]"),new Action(new Send(new Var("e"),"lift")))),
			new Rule("Drop",new Seq(new Term("${"),new Bind("d",new Call("Exp")),new Term("}"),new Action(new Apply("exp.Drop",new Var("d"))))),
			new Rule("Import",new Seq(new Call("WhiteSpace"),new Term("import"),new Bind("es",new Call("Args")),new Call("WhiteSpace"),new Term("{"),new Bind("e",new Call("Exp")),new Call("WhiteSpace"),new Term("}"),new Action(new Apply("exp.Import",new Var("es"),new Var("e"))))),
			new Rule("ModuleBinding",new Seq(new Call("WhiteSpace"),new Term("module"),new Bind("n",new Call("BindingName")),new Call("WhiteSpace"),new Term("{"),new Bind("ds",new Star(new Call("ModuleDef"))),new Call("WhiteSpace"),new Term("}"),new Action(new Apply("modules.ModuleBinding",new Var("n"),new Var("ds"))))),
			new Rule("ModuleDef",new Or(new Seq(new Call("Comment"),new Call("ModuleDef")),new Call("ModuleImport"),new Call("ModuleExport"),new Call("ModuleBind"))),
			new Rule("ModuleImport",new Seq(new Call("WhiteSpace"),new Term("import"),new Bind("es",new Call("Args")),new Action(new Apply("modules.Import",new Var("es"))))),
			new Rule("ModuleExport",new Seq(new Call("WhiteSpace"),new Term("export"),new Bind("ns",new Call("ArgNames")),new Action(new Apply("modules.Export",new Var("ns"))))),
			new Rule("ModuleBind",new Seq(new Bind("b",new Call("Binding")),new Action(new Apply("modules.Bind",new Var("b"))))),
			new Rule("List",new Seq(new Term("["),new Bind("es",new Call("Args")),new Term("]"),new Action(new Apply("exp.List",new Var("es"))))),
			new Rule("ParenExp",new Seq(new Term("("),new Bind("e",new Call("Exp")),new Term(")"),new Action(new Var("e")))),
			new Rule("Var",new Seq(new Bind("n",new Call("Name")),new Action(new Apply("exp.Var",new Var("n"))))),
			new Rule("True",new Seq(new Term("true"),new Action(new Apply("exp.True")))),
			new Rule("False",new Seq(new Term("false"),new Action(new Apply("exp.False")))),
			new Rule("StrExp",new Seq(new Term("'"),new Bind("cs",new Star(new Seq(new Not(new Char('\'')),new Call("StringChar")))),new Char('\''),new Action(new Apply("exp.Str",new Var("cs"))))),
			new Rule("String",new Seq(new Term("'"),new Bind("cs",new Star(new Seq(new Not(new Char('\'')),new Call("StringChar")))),new Char('\''),new Action(new Apply("values.Str",new Var("cs"))))),
			new Rule("StringChar",new Or(new Seq(new Char('\\'),new Bind("c",new Dot()),new Cut(),new Action(new Var("c"))),new Dot())),
			new Rule("IntExp",new Seq(new Call("WhiteSpace"),new Seq(new Bind("i",new Plus(new Range('0','9'))),new Action(new Apply("exp.Int",new Var("i")))))),
			new Rule("Int",new Seq(new Call("WhiteSpace"),new Seq(new Bind("i",new Plus(new Range('0','9'))),new Action(new Apply("values.Int",new Var("i")))))),
	        new Rule("AlphaChar",new Or(new Range('A','Z'),new Range('a','z'))),
	        new Rule("NumChar",new Range('0','9')),
	        new Rule("Case",new Seq(new Call("WhiteSpace"),new Term("case"),new Bind("es",new Call("Args")),new Call("WhiteSpace"),new Term("{"),new Bind("as",new Call("Arms")),new Call("WhiteSpace"),new Term("}"),new Action(new Apply("exp.Case",new Var("es"),new Var("as"))))),
	        new Rule("Arms",new Seq(new Bind("a",new Call("Arm")),new Bind("as",new Star(new Seq(new Term(";"),new Call("Arm")))),new Action(new Cons(new Var("a"),new Var("as"))))),
	        new Rule("Arm",new Seq(new Bind("ps",new Call("Patterns")),new Call("WhiteSpace"),new Term("->"),new Bind("e",new Call("Exp")),new Action(new Apply("exp.Arm",new Var("ps"),new Var("e"))))),
	        new Rule("Pattern",new Seq(new Bind("d",new Call("DataPattern")),new Or(new Seq(new Call("WhiteSpace"),new Term("when"),new Bind("e",new Call("Exp")),new Action(new Apply("patterns.Cond",new Var("d"),new Var("e")))),new Action(new Var("d"))))),
	        new Rule("DataPattern",new Seq(new Bind("a",new Call("AtomicPattern")),new Or(new Seq(new Call("WhiteSpace"),new Term(":"),new Bind("t",new Call("Pattern")),new Action(new Apply("patterns.Cons",new Var("a"),new Var("t")))),new Action(new Var("a"))))),
	        new Rule("AtomicPattern",new Or(new Call("CnstrPattern"),new Call("VarPattern"),new Call("ConstPattern"),new Call("ListPattern"),new Call("RecordPattern"),new Call("ParenPattern"))),
	        new Rule("ParenPattern",new Seq(new Call("WhiteSpace"),new Term("("),new Bind("p",new Call("Pattern")),new Call("WhiteSpace"),new Term(")"),new Action(new Var("p")))),
	        new Rule("VarPattern",new Seq(new Bind("n",new Call("Name")),new Or(new Seq(new Call("WhiteSpace"),new Term(":="),new Bind("p",new Call("Pattern")),new Action(new Apply("patterns.Bind",new Var("n"),new Var("p")))),new Action(new Apply("patterns.Var",new Var("n")))))),
	        new Rule("ConstPattern",new Or(new Call("StrPattern"),new Call("BoolPattern"),new Call("IntPattern"))),
	        new Rule("StrPattern",new Seq(new Term("'"),new Bind("cs",new Star(new Seq(new Not(new Char('\'')),new Dot()))),new Char('\''),new Action(new Apply("patterns.Str",new Var("cs"))))),
	        new Rule("BoolPattern",new Or(new Call("TruePattern"),new Call("FalsePattern"))),
	        new Rule("TruePattern",new Seq(new Call("WhiteSpace"),new Term("true"),new Action(new Apply("patterns.True")))),
	        new Rule("FalsePattern",new Seq(new Call("WhiteSpace"),new Term("false"),new Action(new Apply("patterns.False")))),
	        new Rule("IntPattern",new Seq(new Call("WhiteSpace"),new Seq(new Bind("i",new Plus(new Range('0','9'))),new Action(new Apply("patterns.Int",new Var("i")))))),
	        new Rule("ListPattern",new Seq(new Call("WhiteSpace"),new Term("["),new Bind("ps",new Call("Patterns")),new Call("WhiteSpace"),new Term("]"),new Action(new Apply("patterns.List",new Var("ps"))))),
	        new Rule("Patterns",new Or(new Call("SomePatterns"),new Action(new exp.List()))),
			new Rule("SomePatterns",new Seq(new Bind("p",new Call("Pattern")),new Bind("ps",new Star(new Seq(new Term(","),new Call("Pattern")))),new Action(new Cons(new Var("p"),new Var("ps"))))),
			new Rule("RecordPattern",new Seq(new Call("WhiteSpace"),new Term("{"),new Bind("fs",new Call("FieldPatterns")),new Call("WhiteSpace"),new Term("}"),new Action(new Apply("patterns.Record",new Var("fs"))))),
			new Rule("FieldPatterns",new Or(new Call("SomeFieldPatterns"),new Action(new exp.List()))),
			new Rule("SomeFieldPatterns",new Seq(new Bind("f",new Call("FieldPattern")),new Bind("fs",new Star(new Seq(new Term(";"),new Call("FieldPattern")))),new Action(new Cons(new Var("f"),new Var("fs"))))),
			new Rule("FieldPattern",new Seq(new Bind("n",new Call("Name")),new Call("WhiteSpace"),new Term("="),new Bind("p",new Call("Pattern")),new Action(new Apply("patterns.Field",new Var("n"),new Var("p"))))),
			new Rule("CnstrPattern",new Seq(new Bind("n",new Call("Name")),new Call("WhiteSpace"),new Term("("),new Bind("ps",new Call("Patterns")),new Call("WhiteSpace"),new Term(")"),new Action(new Apply("patterns.Cnstr",new Var("n"),new Var("ps"))))),
			new Rule("KeyWord",new Seq(new Call("Key"),new Not(new Or(new Range('a','z'),new Range('A','Z'))))),
			new Rule("Key",new Or(new Term("EOF"),new Term("thunk"),new Term("not"),new Term("and"),new Term("or"),new Term("when"),new Term("case"),new Term("module"),new Term("export"),new Term("import"),new Term("true"),new Term("false"),new Term("fun"),new Term("let"),new Term("in"),new Term("reify"),new Term("letrec"),new Term("if"),new Term("then"),new Term("else"))),
			new Rule("Name",new Seq(new Call("WhiteSpace"),new Not(new Call("KeyWord")),new Bind("c",new Call("AlphaChar")),new Bind("chars",new Star(new Or(new Call("AlphaChar"),new Call("NumChar")))),new Action(new Apply("values.Str",new Cons(new Var("c"),new Var("chars")))))));
	
	public static Grammar Grammar = new Grammar("Grammar",
			new Rule("Grammar",new Seq(new Term("{"),new Bind("rs",new Call("Rules")),new Term("}"),new Cut(),new Action(new Apply("grammar.Grammar",new exp.Str(""),new Var("rs"))))),
			new Rule("Rule",new Seq(new Bind("n",new Call("Name")),new Bind("as",new Call("RuleArgs")),new Term("->"),new Bind("b",new Call("PTerm")),
					new Action(new Apply("grammar.Rule",new Var("n"),new exp.List(new Apply("grammar.Body",new Var("as"),new Var("b"))))))),
			new Rule("Rules",new Seq(new Bind("r",new Call("Rule")),new Bind("rs",new Star(new Seq(new Term(";"),new Cut(),new Call("Rule")))),new Action(new Cons(new Var("r"),new Var("rs"))))),
			new Rule("RuleArgs",new Or(new Call("SomeRuleArgs"),new Action(new exp.List()))),
			new Rule("SomeRuleArgs",new Seq(new Term("("),new Bind("as",new Call("SomePatterns")),new Term(")"),new Action(new Var("as")))),
			new Rule("PTerm",new Seq(new Bind("l",new Call("Seq")),new Or(new Seq(new Term("|"),new Cut(),new Bind("r",new Call("PTerm")),new Action(new Apply("grammar.Or",new Var("l"),new Var("r")))),new Action(new Var("l"))))),
			new Rule("Seq",new Seq(new Bind("l",new Call("Bind")),new Or(new Seq(new Bind("r",new Call("Seq")),new Action(new Apply("grammar.Seq",new Var("l"),new Var("r")))),new Action(new Var("l"))))),
			new Rule("Bind",new Or(new Seq(new Bind("n",new Call("AtomicPattern")),new Term("="),new Cut(),new Bind("a",new Call("Repeat")),new Action(new Apply("grammar.Bind",new Var("n"),new Var("a")))),new Call("Repeat"))),
			new Rule("Repeat",new DisjointCalls("Star","Plus","PAtom")),
			new Rule("Star",new Seq(new Bind("a",new Call("PAtom")),new Term("*"),new Action(new Apply("grammar.Star",new Var("a"))))),
			new Rule("Plus",new Seq(new Bind("a",new Call("PAtom")),new Term("+"),new Action(new Apply("grammar.Plus",new Var("a"))))),
			new Rule("PAtom",new Or(new Call("EndOfInput"),new Call("Cut"),new Call("Pred"),new Call("CharCode"),new Call("PDelay"),new Call("LDelay"),new Call("Not"),new Call("Call"),new Call("ECall"),new Call("Term"),new Call("Range"), new Call("Dot"), new Call("Action"),new Seq(new Term("("),new Bind("a",new Call("PTerm")),new Term(")"),new Action(new Var("a"))))),
			new Rule("Cut",new Seq(new Term("!"),new Action(new Apply("grammar.Cut")))),
			new Rule("PDelay",new Seq(new Term("PDELAY"),new Term("("),new Bind("start",new Call("String")),new Term(","),new Bind("end",new Call("String")),new Term(","),new Bind("g",new Call("Exp")),new Term(")"),new Action(new Apply("grammar.PDelay",new Var("start"),new Var("end"),new Var("g"))))),
			new Rule("LDelay",new Seq(new Term("LDELAY"),new Term("("),new Bind("start",new Call("String")),new Term(","),new Bind("end",new Call("String")),new Term(","),new Bind("g",new Call("Exp")),new Term(")"),new Action(new Apply("grammar.LDelay",new Var("start"),new Var("end"),new Var("g"))))),
			new Rule("Call",new Seq(new Bind("n",new Call("Name")),new Bind("as",new Call("CallArgs")),new Cut(),new Action(new Apply("grammar.Call",new Var("n"),new Var("as"))))),
			new Rule("CallArgs",new Or(new Call("SomeCallArgs"),new Action(new exp.List()))),
			new Rule("SomeCallArgs",new Seq(new Term("^"),new Term("("),new Bind("as",new Call("Args")),new Term(")"),new Action(new Var("as")))),
			new Rule("ECall",new Seq(new Term("<"),new Bind("e",new Call("Exp")),new Term(">"),new Action(new Apply("grammar.EvalCall",new Var("e"),new exp.List())))),
			new Rule("Term",new Seq(new Term("'"),new Bind("cs",new Star(new Seq(new Not(new Char('\'')),new Call("StringChar")))),new Char('\''),new Action(new Apply("grammar.Term",new Var("cs"))))),
			new Rule("Range",new Seq(new Term("["),new Bind("l",new Or(new Call("Int"),new Call("CharX"))),new Term(","),new Bind("u",new Or(new Call("Int"),new Call("CharX"))),new Term("]"),new Action(new Apply("grammar.Range",new Var("l"),new Var("u"))))),
			new Rule("Dot",new Seq(new Term("."),new Action(new Apply("grammar.Dot")))),
			new Rule("Pred",new Seq(new Call("WhiteSpace"),new Term("?"),new Bind("n",new Call("Atom")),new Term("("),new Bind("as",new Call("Args")),new Term(")"),new Action(new Apply("grammar.Predicate",new Var("n"),new Var("as"))))),
			new Rule("Not",new Seq(new Call("WhiteSpace"),new Term("not"),new Term("("),new Bind("t",new Call("PTerm")),new Term(")"),new Action(new Apply("grammar.Not",new Var("t"))))),
			new Rule("Char",new Or(new Call("CharLiteral"),new Call("CharCode"))),
			new Rule("CharCode",new Seq(new Bind("c",new Call("Int")),new Action(new Apply("grammar.Char",new Var("c"))))),
			new Rule("CharLiteral",new Seq(new Char('\''),new Bind("x",new Dot()),new Char('\''),new Action(new Apply("grammar.Char",new Var("x"))))),
			new Rule("CharX",new Seq(new Char('\''),new Bind("x",new Dot()),new Char('\''),new Action(new Var("x")))),
			new Rule("EndOfInput",new Seq(new Term("EOF"),new Action(new Apply("grammar.EOF")))),
	        new Rule("Action",new Seq(new Term("{"),new Bind("e",new Call("Exp")),new Term("}"),new Action(new Apply("grammar.Action",new Var("e")))))
	);
	
	public static Grammar XPL = Exp.add(Grammar);
	
	public static Grammar setXPL(Grammar grammar) {
		XPL = grammar;
		return grammar;
	}
	
	public static XClass newClass(String name) {
		XClass c = new XClass(name);
		classes.put(name,c);
		return c;
	}
	
	public static XClass getClass(String name) {
		if(classes.containsKey(name))
			return classes.get(name);
		return null;
	}
	
}
