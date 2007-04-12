package org.eventb.internal.pp.loader.formula;

import java.util.ArrayList;
import java.util.List;

import org.eventb.internal.pp.core.elements.ILiteral;
import org.eventb.internal.pp.loader.clause.BooleanEqualityTable;
import org.eventb.internal.pp.loader.clause.ClauseBuilder;
import org.eventb.internal.pp.loader.clause.LabelManager;
import org.eventb.internal.pp.loader.clause.VariableTable;
import org.eventb.internal.pp.loader.formula.descriptor.IndexedDescriptor;
import org.eventb.internal.pp.loader.formula.terms.TermSignature;
import org.eventb.internal.pp.loader.predicate.IIntermediateResult;

public abstract class AbstractClause<T extends IndexedDescriptor> extends AbstractLabelizableFormula<T> implements
		ISubFormula<T>, ILabelizableFormula<T> {

	protected List<ISignedFormula> children;
	
	public AbstractClause(List<ISignedFormula> children,
			List<TermSignature> terms, T descriptor) {
		super(terms,descriptor);
		this.children = children;
	}
	
	@Override
	public String toString() {
		return super.toString();
	}
	
	public String getStringDeps() {
		StringBuffer str = new StringBuffer();
		for (ISignedFormula child : children) {
			str.append("["+child.toString()+"] ");
		}
		return str.toString();
	}
	
	public List<ISignedFormula> getChildren() {
		return children;
	}
	
	public abstract boolean isEquivalence();
	
	@Override
	protected boolean isLabelizable(LabelManager manager, TermVisitorContext context) {
		return (descriptor.getResults().size() > 1 || manager.isForceLabelize() 
				|| (isEquivalence()?!context.isEquivalence:context.isEquivalence));
	}
	
	public ILiteral getLiteral(List<TermSignature> terms, TermVisitorContext context, VariableTable table, BooleanEqualityTable bool) {
		return getLiteral(descriptor.getIndex(), terms, context, table);
	}
	
	public void split() {
		for (ISignedFormula child : children) {
			child.split();
		}
		List<IIntermediateResult> result = new ArrayList<IIntermediateResult>(getLiteralDescriptor().getResults());
		for (int i = 0; i < children.size(); i++) {
			for (IIntermediateResult current : getLiteralDescriptor().getResults()) {
				if (!contains(current, children.get(i).getFormula().getLiteralDescriptor().getResults(), i)) result.remove(current);
			}
		}
		if (result.size() != descriptor.getResults().size()) {
			ClauseBuilder.debug("Splitting "+this+", terms remaining: "+result.toString());
		}
		descriptor = getNewDescriptor(result, descriptor.getIndex());	
	}
	
	protected abstract T getNewDescriptor(List<IIntermediateResult> result, int index);
	
	private boolean contains(IIntermediateResult original, List<IIntermediateResult> child, int position) {
		for (IIntermediateResult result : child) {
			if (original.getResultList().get(position).equals(result)) return true;
		}
		return false;
	}
	
	public void setFlags(TermVisitorContext context) {
		context.isEquivalence = isEquivalence();
	}
	
	
	public String toTreeForm(String prefix) {
		StringBuilder str = new StringBuilder();
		str.append(toString()+getTerms()+"\n");
		for (ISignedFormula child : children) {
			str.append(child.toTreeForm(prefix+" ")+"\n");
		}
		str.deleteCharAt(str.length()-1);
		return str.toString();
	}

}
