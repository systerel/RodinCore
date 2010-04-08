package org.eventb.internal.ui;

public class Pair<S extends Object, T extends Object> {

	S obj1;

	T obj2;

	public Pair(S obj1, T obj2) {
		this.obj1 = obj1;
		this.obj2 = obj2;
	}

	public S getFirst() {
		return obj1;
	}

	public T getSecond() {
		return obj2;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return this == null;
		if (obj instanceof Pair<?, ?>) {
			Pair<?, ?> pair = (Pair<?, ?>) obj;
			return pair.getFirst().equals(obj1)
					&& pair.getSecond().equals(obj2);
		}
		return super.equals(obj);
	}

	@Override
	public String toString() {
		return "(" + obj1 + "," + obj2 + ")";
	}

}