package context;

import values.Printable;
import exp.BoaConstructor;

@BoaConstructor(fields = { "str" })
public class StringSource extends Printable implements CharSource {

	public String	str;

	public StringSource(String str) {
		this.str = str;
	}

	public boolean EOF(int i) {
		return i >= str.length();
	}

	public char charAt(int i) {
		return str.charAt(i);
	}

	public String getText() {
		return str;
	}

	public int length() {
		return str.length();
	}

	public boolean hasPrefix(int ptr, String prefix) {
		if (ptr + prefix.length() > str.length())
			return false;
		else
			return str.substring(ptr, ptr + prefix.length()).equals(prefix);
	}

}
