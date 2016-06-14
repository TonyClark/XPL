package context;

import java.util.Arrays;
import java.util.Vector;

public class ParseError extends Error {

	private static int	ERRBACKUP	= 160;

	Vector<String>			messages;
	int									position;
	String							text;

	public ParseError(Vector<String> messages, int position, String text) {
		super(messages.toString());
		this.messages = messages;
		this.position = position;
		this.text = text;
	}

	public Vector<String> getMessages() {
		return messages;
	}

	public void setMessages(Vector<String> messages) {
		this.messages = messages;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String toString() {
		int linePos = 0;
		int start = Math.max(0, position - ERRBACKUP);
		String error = "ERROR: ";
		for (String msg : messages)
			error = error + msg + "\n       ";
		error = error + '\n';
		for (int i = start; i < Math.min(position, text.length()); i++) {
			char c = text.charAt(i);
			if (c == '\n')
				linePos = 0;
			else linePos++;
			error = error + c;
		}
		error = error + '\n';
		for (int i = 0; i < linePos; i++)
			error = error + ' ';
		error = error + "^\n";
		for (int i = 0; i < linePos; i++)
			error = error + ' ';
		error = error + "|\n";
		return error;
	}

	public String getSyntaxError() {
		String e = "Syntax Error";
		for(String message : messages)
			e = e + "\n  " + message;
		return e;
	}

}
