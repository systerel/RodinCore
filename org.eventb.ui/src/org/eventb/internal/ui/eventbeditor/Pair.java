package org.eventb.internal.ui.eventbeditor;

public class Pair {
	
	Object obj1, obj2;
	
	public Pair(Object obj1, Object obj2) {
		this.obj1 = obj1;
		this.obj2 = obj2;
	}
	
	public Object getFirst() {return obj1;}
	
	public Object getSecond() {return obj2;}
}