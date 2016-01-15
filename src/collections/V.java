package collections;

import java.util.Vector;

public class V<T> extends Vector<T> {
	
	public V(T... values) {
		for(T e : values)
			add(e);
	}

}
