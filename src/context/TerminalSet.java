package context;

import java.util.HashSet;

public class TerminalSet extends HashSet<String> {

	private static final long	serialVersionUID	= 1L;

	public boolean add(String terminal) {
		if (!isWhiteSpace(terminal))
			return super.add(terminal);
		return false;
	}

	public boolean isWhiteSpace(String terminal) {
		return terminal.equals(" ") || terminal.equals("\n") || terminal.equals("\t") || terminal.equals("\r");
	}

}
