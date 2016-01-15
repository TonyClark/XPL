package patterns;

import values.Value;
import context.Context;
import exp.BoaConstructor;

@BoaConstructor(fields = { "fields" })
public class Record extends Pattern {

	public Field[]	fields;

	public boolean match(Value value, Context context) {
		if (value instanceof values.Record) {
			values.Record record = (values.Record) value;
			for (Field field : fields) {
				if (record.hasField(field.name)) {
					if (!field.pattern.match(record.getSafeField(field.name).value, context))
						return false;
				} else
					return false;
			}
			return true;
		} else
			return false;
	}

	public String pprint() {
		String s = "{";
		for(Field f : fields) {
			s = s + f.pprint();
			if(f != fields[fields.length-1])
				s = s + ",";
		}
		return s + "}";
	}

}
