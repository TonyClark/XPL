package context;

public interface CharSource {
	
	public String getText();
	
	public char charAt(int i);
	
	public int length();
	
	public boolean hasPrefix(int ptr,String prefix);
	
	public boolean EOF(int i);

}
