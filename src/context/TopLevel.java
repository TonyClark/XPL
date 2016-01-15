package context;

import java.io.IOException;
import java.io.InputStream;

public class TopLevel implements CharSource {

	private StringBuffer	buffer	= new StringBuffer();
	private InputStream		in;

	public TopLevel(InputStream in) {
		this.in = in;
	}

	public void ensureCharsTo(int i) {
		while (i >= length())
			try {
				buffer.append((char) in.read());
			} catch (IOException e) {
				throw new Error(e.getMessage());
			}
	}

	public char charAt(int i) {
		ensureCharsTo(i);
		return buffer.charAt(i);
	}
	
	public boolean EOF(int i) {
		return false;
	}

	public String getText() {
		return buffer.toString();
	}

	public int length() {
		return buffer.length();
	}

	public boolean hasPrefix(int ptr, String prefix) {
		boolean hasPrefix = true;
		for(int i = ptr; i < ptr + prefix.length() && hasPrefix; i++)
			hasPrefix = charAt(i) == prefix.charAt(i-ptr);
		return hasPrefix;
	}

}
