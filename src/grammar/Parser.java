package grammar;

import java.io.IOException;
import java.io.Reader;

import machine.Machine;
import values.Value;

public abstract class Parser {

	public Parser() {
	}

	public Reader getReader(final Machine machine) {
		return new Reader() {
			public int read(char[] cbuf, int off, int len) throws IOException {
				// Always return 1 character....
				if(machine.EOF())
					return -1;
				else {
					cbuf[off] = (char)machine.next();
					return 1;
				}
				/*
				int charsRead = 0;
				for (int i = 0; i < len && !machine.EOF(); i++) {
					cbuf[off + i] = (char) machine.next();
					charsRead++;
				}
				if (charsRead == len)
					return len;
				else return -1;
				*/
			}

			public void close() throws IOException {
			}
		};
	}

	public abstract Value parse(Machine machine);

}
