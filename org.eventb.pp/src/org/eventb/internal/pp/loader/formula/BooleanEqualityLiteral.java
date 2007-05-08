package org.eventb.internal.pp.loader.formula;

import java.util.ArrayList;
import java.util.List;

import org.eventb.internal.pp.core.elements.ILiteral;
import org.eventb.internal.pp.core.elements.PPEquality;
import org.eventb.internal.pp.core.elements.PPProposition;
import org.eventb.internal.pp.core.elements.Sort;
import org.eventb.internal.pp.core.elements.terms.Term;
import org.eventb.internal.pp.loader.clause.BooleanEqualityTable;
import org.eventb.internal.pp.loader.clause.ClauseBuilder;
import org.eventb.internal.pp.loader.clause.VariableTable;
import org.eventb.internal.pp.loader.formula.descriptor.EqualityDescriptor;
import org.eventb.internal.pp.loader.formula.terms.TermSignature;
import org.eventb.internal.pp.loader.formula.terms.TrueConstantSignature;
import org.eventb.internal.pp.loader.predicate.IIntermediateResult;

public class BooleanEqualityLiteral extends EqualityLiteral {

	public BooleanEqualityLiteral(List<TermSignature> terms,
			EqualityDescriptor descriptor) {
		super(terms,descriptor);
		
		assert descriptor.getSort().equals(Sort.BOOLEAN);
	}
	
	@Override
	public void split() {
		List<IIntermediateResult> result = new ArrayList<IIntermediateResult>();
		for (IIntermediateResult res : getLiteralDescriptor().getResults()) {
			if (contains(res.getTerms(), getTerms().get(0)) ||
				contains(res.getTerms(), getTerms().get(1))) {
					result.add(res);
			}
		}
		
		if (result.size() != descriptor.getResults().size()) {
			ClauseBuilder.debug("Splitting "+this+", terms remaining: "+result.toString());
		}

		descriptor = new EqualityDescriptor(descriptor.getContext(), result, descriptor.getSort());
	}
	
	private boolean contains(List<TermSignature> list, TermSignature sig) {
		return sig instanceof TrueConstantSignature ? false : list.contains(sig);
	}
	
	@Override
	public ILiteral<?> getLiteral(List<TermSignature> termList, TermVisitorContext context, VariableTable table, BooleanEqualityTable bool) {
		List<TermSignature> newList = descriptor.getUnifiedResults();
		ILiteral result;
		if (newList.get(1) instanceof TrueConstantSignature) {
			TermSignature sig = termList.get(0);
			Integer i;
			if (bool.containsKey(sig)) {
				i = bool.get(sig);
			}
			else {
				i = bool.getNextLiteralIdentifier();
				bool.put(sig, i);
			}
			result = new PPProposition(i, context.isPositive);
		} else {
			List<Term> newTerms = getTermsFromTermSignature(termList, context, table);
			result = new PPEquality(newTerms.get(0), newTerms.get(1), context.isPositive);
		}
		ClauseBuilder.debug("Creating literal from "+this+": "+result);
		return result;
	}
	
}
