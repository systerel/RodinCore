package org.eventb.internal.pp.loader.formula;

import java.util.ArrayList;
import java.util.List;

import org.eventb.internal.pp.core.elements.Literal;
import org.eventb.internal.pp.loader.clause.BooleanEqualityTable;
import org.eventb.internal.pp.loader.clause.ClauseBuilder;
import org.eventb.internal.pp.loader.clause.VariableTable;
import org.eventb.internal.pp.loader.formula.descriptor.IndexedDescriptor;
import org.eventb.internal.pp.loader.formula.terms.TermSignature;
import org.eventb.internal.pp.loader.predicate.IIntermediateResult;

public abstract class AbstractClause<T extends IndexedDescriptor> extends AbstractLabelizableFormula<T> {

	protected List<SignedFormula<?>> children;
	
	public AbstractClause(List<SignedFormula<?>> children,
			List<TermSignature> terms, T descriptor) {
		super(terms,descriptor);
		this.children = children;
	}
	
	@Override
	public String toString() {
		return super.toString();
	}
	
	@Override
	public String getStringDeps() {
		StringBuffer str = new StringBuffer();
		for (SignedFormula<?> child : children) {
			str.append("["+child.toString()+"] ");
		}
		return str.toString();
	}
	
	public List<SignedFormula<?>> getChildren() {
		return children;
	}
	
	@Override
	Literal<?,?> getLiteral(List<TermSignature> terms, TermVisitorContext context, VariableTable table, BooleanEqualityTable bool) {
		return getLiteral(descriptor.getIndex(), terms, context, table);
	}
	
	@Override
	final void split() {
		for (SignedFormula<?> child : children) {
			child.split();
		}
		List<IIntermediateResult> result = new ArrayList<IIntermediateResult>(getLiteralDescriptor().getResults());
		for (int i = 0; i < children.size(); i++) {
			for (IIntermediateResult current : getLiteralDescriptor().getResults()) {
				if (!contains(current, children.get(i).getFormula().getLiteralDescriptor().getResults(), i)) result.remove(current);
			}
		}
		if (result.size() != descriptor.getResults().size()) {
			if (ClauseBuilder.DEBUG) ClauseBuilder.debug("Splitting "+this+", terms remaining: "+result.toString());
		}
		descriptor = getNewDescriptor(result, descriptor.getIndex());	
	}
	
	abstract T getNewDescriptor(List<IIntermediateResult> result, int index);
	
	private boolean contains(IIntermediateResult original, List<IIntermediateResult> child, int position) {
		for (IIntermediateResult result : child) {
			if (original.getResultList().get(position).equals(result)) return true;
		}
		return false;
	}
	
	@Override
	String toTreeForm(String prefix) {
		StringBuilder str = new StringBuilder();
		str.append(toString()+getTerms()+"\n");
		for (SignedFormula<?> child : children) {
			str.append(child.toTreeForm(prefix+" ")+"\n");
		}
		str.deleteCharAt(str.length()-1);
		return str.toString();
	}
	
	@Override
	TermVisitorContext getNewContext(TermVisitorContext context) {
		TermVisitorContext newContext = new TermVisitorContext(context.isEquivalence);
		
		newContext.isQuantified = false;
		newContext.isPositive = context.isPositive;
		
		return newContext;
	}


}
