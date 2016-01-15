package collections;

import java.util.Vector;

public class Set<T> extends java.util.HashSet<T> {
	
	private static final long serialVersionUID = 1L;
	
	public Set() {
		super();
	}
	
	public Set(Vector<T> elements) {
		this();
		for(T e : elements) 
			add(e);
	}
	
	public Set(T element) {
		this();
		add(element);
	}

	public Set<T> union(Set<T> other) {
		Set<T> S = new Set<T>();
		S.addAll(this);
		S.addAll(other);
		return S;
	}
	
	public Set<T> diff(Set<T> other) {
		Set<T> S = new Set<T>();
		for(T e : this)
			if(!other.contains(e))
				S.add(e);
		return S;
	}
	
	public Set<T> diff(T element) {
		Set<T> S = new Set<T>();
		for(T e : this)
			if(!e.equals(element))
				S.add(e);
		return S;
	}
	
	public Set<T> intersection(Set<T> other) {
		Set<T> S = new Set<T>();
		for(T e : this)
			if(other.contains(e))
				S.add(e);
		return S;
	}
	
	public Vector<T> asVector() {
		Vector<T> v = new Vector<T>();
		for(T e : this)
			v.addElement(e);
		return v;
	}
	
	public T elementAt(int i) {
		return asVector().elementAt(i);
	}

	public Set<T> union(T x) {
		return this.union(new Set<T>(x));
	}

}
