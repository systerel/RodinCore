package org.eventb.internal.pp.loader.formula;

import java.util.ArrayList;
import java.util.List;

import org.eventb.internal.pp.core.elements.Literal;
import org.eventb.internal.pp.loader.clause.BooleanEqualityTable;
import org.eventb.internal.pp.loader.clause.ClauseBuilder;
import org.eventb.internal.pp.loader.clause.LabelManager;
import org.eventb.internal.pp.loader.clause.VariableTable;
import org.eventb.internal.pp.loader.formula.descriptor.EquivalenceClauseDescriptor;
import org.eventb.internal.pp.loader.formula.terms.TermSignature;
import org.eventb.internal.pp.loader.predicate.IIntermediateResult;

public class EquivalenceClause extends AbstractClause<EquivalenceClauseDescriptor> {
	
	public EquivalenceClause(List<SignedFormula<?>> children,
			List<TermSignature> terms, EquivalenceClauseDescriptor descriptor) {
		super(children,terms,descriptor);
	}

	@Override
	List<List<Literal<?,?>>> getDefinitionClauses(List<TermSignature> termList,
			LabelManager manager, List<List<Literal<?,?>>> prefix, TermVisitorContext flags, VariableTable table, BooleanEqualityTable bool) {
		List<List<Literal<?,?>>> result = new ArrayList<List<Literal<?,?>>>();
		int start = 0;
		if (flags.isPositive) {
			for (SignedFormula<?> child : children) {
				List<TermSignature> subIndex = termList.subList(start, start + child.getIndexSize());
				prefix = child.getClauses(subIndex, manager, prefix, table, flags, bool);
				start += child.getIndexSize();
			}
			result = prefix;
		}
		else {
			boolean first = true;
			for (SignedFormula<?> child : children) {
				if (!first) flags.isPositive = true;
				List<TermSignature> subIndex = termList.subList(start, start + child.getIndexSize());
				prefix = child.getClauses(subIndex, manager, prefix, table, flags, bool);
				start += child.getIndexSize();
				first = false;
			}
			flags.isPositive = false;
			result = prefix;
		}
		return result;
	}

	@Override
	public ClauseResult getFinalClauses(LabelManager manager, BooleanEqualityTable bool, VariableTable table, boolean positive, boolean equivalence) {
		if (ClauseBuilder.DEBUG) ClauseBuilder.debug("----------------");
		if (ClauseBuilder.DEBUG) ClauseBuilder.debug("Equivalence definition:");
		return getFinalClausesHelper(manager, true, true, equivalence, bool, table);
	}
	
	@Override
	EquivalenceClauseDescriptor getNewDescriptor(List<IIntermediateResult> result, int index) {
		return new EquivalenceClauseDescriptor(descriptor.getContext(), result, index);
	}

	
	private boolean isLabelizable(LabelVisitor context) {
		if (context.isQuantified) {
			if (context.isPositiveLabel || context.isNegativeLabel) return true;
			else if (context.equivalenceCount > 0) return true;
			else if (!context.isForall) return true;
		}
		if (context.isDisjunction) return true;
		return false;
	}

	@Override
	boolean getContextAndSetLabels(LabelVisitor context, LabelManager manager) {
		LabelVisitor newContext = new LabelVisitor();
		if (isLabelizable(context)) {
			manager.addEquivalenceLabel(this);

			// this becomes a label
			// we construct labels below
			newContext.isNegativeLabel = true;
			newContext.isPositiveLabel = true;
		}
		newContext.equivalenceCount++;
		newContext.isQuantified = false;
		// we continue
		boolean first = true;
		for (SignedFormula<?> child : children) {
			if (first && !context.isPositive) newContext.isPositive = false;
			else newContext.isPositive = true;
			child.getContextAndSetLabels(newContext, manager);
			first = false;
		}
		return true;
	}
	
}