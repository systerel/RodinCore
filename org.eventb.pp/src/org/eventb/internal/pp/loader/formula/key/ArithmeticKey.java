package org.eventb.internal.pp.loader.formula.key;

import java.util.List;

import org.eventb.internal.pp.loader.formula.ArithmeticFormula.Type;
import org.eventb.internal.pp.loader.formula.descriptor.ArithmeticDescriptor;
import org.eventb.internal.pp.loader.formula.terms.TermSignature;
import org.eventb.internal.pp.loader.predicate.IContext;

public class ArithmeticKey extends SymbolKey<ArithmeticDescriptor> {

//	private static int UniqueIndex = 0;
	private List<TermSignature> definingTerms;
	private Type type;
	
//	public static void resetCounter() {
//		UniqueIndex = 0;
//	}
	
//	private int index;
	
	public ArithmeticKey(List<TermSignature> definingTerms, Type type) {
//		index = UniqueIndex++;
		this.definingTerms = definingTerms;
		this.type = type;
	}
	
	@Override
	public int hashCode() {
		return definingTerms.hashCode()*3+type.hashCode(); 
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ArithmeticKey) {
			ArithmeticKey temp = (ArithmeticKey) obj;
			return /* index == temp.index && */ definingTerms.equals(temp.definingTerms)
				&& type == temp.type;
		}
		return false;
	}

	@Override
	public String toString() {
		return "A "+definingTerms;
	}

	@Override
	public ArithmeticDescriptor newDescriptor(IContext context) {
		return new ArithmeticDescriptor(context);
	}

}
