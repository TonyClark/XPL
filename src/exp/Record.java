package exp;

import values.Value;
import context.Context;

@BoaConstructor(fields = { "fields" })
public class Record extends Exp {

	public Field[] fields;

	public Record() {
	}

	public Record(Field[] fields) {
		this.fields = fields;
	}

	public Value eval(Context context) {
		values.Field[] fields = new values.Field[this.fields.length];
		for(int i = 0; i < this.fields.length;i++)
			fields[i] = this.fields[i].eval(context);
		return new values.Record(fields);
	}

	public String pprint(int opPrec) {
		String s = "{";
		for(Field f : fields) {
			s = s + f.pprint();
			if(f != fields[fields.length-1])
				s = s + ",";
		}
		return s + "}";
	}

}
