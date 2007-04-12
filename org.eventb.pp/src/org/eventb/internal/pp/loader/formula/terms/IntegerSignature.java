package org.eventb.internal.pp.loader.formula.terms;

import org.eventb.internal.pp.core.elements.Sort;
import org.eventb.internal.pp.core.elements.terms.Constant;
import org.eventb.internal.pp.core.elements.terms.Term;
import org.eventb.internal.pp.loader.clause.VariableTable;
import org.eventb.internal.pp.loader.formula.TermVisitorContext;

public class IntegerSignature extends AbstractConstantSignature {

	private int literal;
	
	public IntegerSignature(int literal, Sort sort) {
		super(sort);
		
		this.literal = literal;
	}

	@Override
	public Term getTerm(VariableTable table, TermVisitorContext context) {
		return new Constant(literal+"",sort);
	}

	@Override
	protected TermSignature deepCopy() {
		return new IntegerSignature(literal, sort);
	}

	@Override
	public String toString() {
		return literal+"";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IntegerSignature) {
			IntegerSignature temp = (IntegerSignature) obj;
			return temp.literal == literal;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return literal;
	}

	
}
