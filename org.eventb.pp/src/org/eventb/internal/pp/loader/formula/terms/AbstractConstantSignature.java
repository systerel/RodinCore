package org.eventb.internal.pp.loader.formula.terms;

import java.util.List;

import org.eventb.internal.pp.core.elements.Sort;
import org.eventb.internal.pp.core.elements.terms.Term;
import org.eventb.internal.pp.loader.clause.VariableTable;
import org.eventb.internal.pp.loader.formula.TermVisitorContext;

public abstract class AbstractConstantSignature extends TermSignature {

	public AbstractConstantSignature(Sort sort) {
		super(sort);
		// TODO Auto-generated constructor stub
	}

	@Override
	public TermSignature getUnquantifiedTerm(int startOffset, int endOffset, List<TermSignature> termList) {
		addTerm(this.deepCopy(), termList);
		return new VariableHolder(sort);
	}

	@Override
	public boolean isQuantified(int startOffset, int endOffset) {
		return false;
	}

	@Override
	public boolean isConstant() {
		return true;
	}
	
	public abstract Term getTerm(VariableTable table, TermVisitorContext context);

	@Override
	public void appendTermFromTermList(List<TermSignature> indexList, List<TermSignature> newList, int startOffset, int endOffset) {
		newList.add(this.deepCopy());
	}

}