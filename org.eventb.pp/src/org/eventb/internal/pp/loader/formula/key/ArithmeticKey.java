package org.eventb.internal.pp.loader.formula.key;

import org.eventb.internal.pp.loader.formula.descriptor.ArithmeticDescriptor;
import org.eventb.internal.pp.loader.predicate.IContext;

public class ArithmeticKey extends SymbolKey<ArithmeticDescriptor> {

	private static int UniqueIndex = 0;
	
	public static void resetCounter() {
		UniqueIndex = 0;
	}
	
	private int index;
	
	public ArithmeticKey() {
		index = UniqueIndex++;
	}
	
	@Override
	public int hashCode() {
		return index; 
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ArithmeticKey) {
			ArithmeticKey temp = (ArithmeticKey) obj;
			return index == temp.index;
		}
		return false;
	}

	@Override
	public String toString() {
		return "A"+index;
	}

	@Override
	public ArithmeticDescriptor newDescriptor(IContext context) {
		return new ArithmeticDescriptor(context);
	}

}
