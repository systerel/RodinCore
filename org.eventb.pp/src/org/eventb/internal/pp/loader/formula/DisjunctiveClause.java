package org.eventb.internal.pp.loader.formula;

import java.util.ArrayList;
import java.util.List;

import org.eventb.internal.pp.core.elements.Literal;
import org.eventb.internal.pp.loader.clause.BooleanEqualityTable;
import org.eventb.internal.pp.loader.clause.ClauseBuilder;
import org.eventb.internal.pp.loader.clause.LabelManager;
import org.eventb.internal.pp.loader.clause.VariableTable;
import org.eventb.internal.pp.loader.formula.descriptor.DisjunctiveClauseDescriptor;
import org.eventb.internal.pp.loader.formula.terms.TermSignature;
import org.eventb.internal.pp.loader.predicate.IIntermediateResult;

public class DisjunctiveClause extends AbstractClause<DisjunctiveClauseDescriptor> {
	
	public DisjunctiveClause(List<SignedFormula<?>> children,
			List<TermSignature> terms, DisjunctiveClauseDescriptor descriptor) {
		super(children,terms,descriptor);
	}
	
	
	private List<List<Literal<?,?>>> copyClauseList(List<List<Literal<?,?>>> original, VariableTable table) {
		List<List<Literal<?,?>>> result = new ArrayList<List<Literal<?,?>>>();
		for (List<Literal<?,?>> list : original) {
			result.add(new ArrayList<Literal<?,?>>(list));
		}
		return result;
	}

	@Override
	List<List<Literal<?,?>>> getDefinitionClauses(List<TermSignature> terms,
			LabelManager manager, List<List<Literal<?,?>>> prefix,
			TermVisitorContext flags, VariableTable table, BooleanEqualityTable bool) {
		List<List<Literal<?,?>>> result = new ArrayList<List<Literal<?,?>>>();
		int start = 0;
		if (flags.isPositive) {
			for (SignedFormula<?> child : children) {
				List<TermSignature> subIndex = terms.subList(start, start + child.getIndexSize());
				prefix = child.getClauses(subIndex, manager, prefix, table, flags, bool);
				start += child.getIndexSize();
			}
			result = prefix;
		} else {
			// we split because it is a conjunction
			for (SignedFormula<?> child : children) {
				List<TermSignature> subIndex = terms.subList(start, start + child.getIndexSize());
				List<List<Literal<?,?>>> copy = copyClauseList(prefix,table);
				result.addAll(child.getClauses(subIndex, manager, copy, table, flags, bool));
				start += child.getIndexSize();
			}
		}
		return result;
	}
	
	@Override
	public ClauseResult getFinalClauses(LabelManager manager, BooleanEqualityTable bool, VariableTable table, boolean positive, boolean equivalence) {
		if (positive) {
			if (ClauseBuilder.DEBUG) ClauseBuilder.debug("----------------");
			if (ClauseBuilder.DEBUG) ClauseBuilder.debug("Positive definition:");
			return getFinalClausesHelper(manager, false, true, equivalence, bool, table);
		} else {
			if (ClauseBuilder.DEBUG) ClauseBuilder.debug("----------------");
			if (ClauseBuilder.DEBUG) ClauseBuilder.debug("Negative definition:");
			return getFinalClausesHelper(manager, true, false, equivalence, bool, table);
		}
	}
	
	@Override
	DisjunctiveClauseDescriptor getNewDescriptor(List<IIntermediateResult> result, int index) {
		return new DisjunctiveClauseDescriptor(descriptor.getContext(), result, index);
	}

	private boolean isLabelizable(LabelVisitor context) {
		if (context.isQuantified) {
			if (context.isPositiveLabel || context.isNegativeLabel) return true;
			else if (!context.isForall) return true;
		}
		if (context.equivalenceCount > 0) return true;
		return false;
	}

	@Override
	boolean getContextAndSetLabels(LabelVisitor context, LabelManager manager) {
		LabelVisitor newContext = new LabelVisitor();
		if (isLabelizable(context)) {
//			if (context.isPositiveLabel || context.equivalenceCount > 0) manager.addLabel(this, context.isPositive);
//			if (context.isNegativeLabel || context.equivalenceCount > 0) manager.addLabel(this, !context.isPositive);
//			if (!context.isPositiveLabel && !context.isNegativeLabel && context.equivalenceCount == 0) manager.addLabel(this, context.isPositive);
			manager.addLabel(this, true);
			manager.addLabel(this, false);
			
			// this becomes a label
			// we construct labels below
			if (context.equivalenceCount > 0) {
				newContext.isNegativeLabel = true;
				newContext.isPositiveLabel = true;
			}
			else {
				if (context.isPositive) {
					newContext.isPositiveLabel = true;
					newContext.isNegativeLabel = context.isNegativeLabel;
				}
				else {
					newContext.isNegativeLabel = true;
					newContext.isPositiveLabel = context.isNegativeLabel;
				}
			}
		}
		newContext.isPositive = context.isPositive;
		newContext.isDisjunction = true;
		newContext.isQuantified = false;
		
		// we continue
		boolean isPositive = newContext.isPositive;
		for (SignedFormula<?> formula : children) {
			newContext.isPositive = isPositive;
			formula.getContextAndSetLabels(newContext, manager);
		}
		return false;
	}
	
}
