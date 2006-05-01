package org.eventb.internal.pp.translator;

public class Counter{
	private int count = 0;
	public Counter() {/*empty*/}
	public Counter(Counter c) { count = c.value(); }
	public Counter(int value) { count = value; }
	public int increment() { return count++; }
	public int add(int n) { count += n; return count - n;}
	public int sub(int n) { count -= n; return count + n;}
	public int value() { return count;}
	public void reset() { count = 0; }
} 

