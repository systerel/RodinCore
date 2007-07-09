package org.eventb.internal.pp.loader.formula;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.ClauseFactory;
import org.eventb.internal.pp.core.elements.Literal;
import org.eventb.internal.pp.loader.clause.BooleanEqualityTable;
import org.eventb.internal.pp.loader.clause.ClauseBuilder;
import org.eventb.internal.pp.loader.clause.LabelManager;
import org.eventb.internal.pp.loader.clause.VariableTable;
import org.eventb.internal.pp.loader.formula.descriptor.DisjunctiveClauseDescriptor;
import org.eventb.internal.pp.loader.formula.terms.TermSignature;
import org.eventb.internal.pp.loader.predicate.IIntermediateResult;

public class DisjunctiveClause extends AbstractClause<DisjunctiveClauseDescriptor> {
	
	public DisjunctiveClause(List<ISignedFormula> children,
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

	public List<List<Literal<?,?>>> getDefinitionClauses(List<TermSignature> terms,
			LabelManager manager, List<List<Literal<?,?>>> prefix,
			TermVisitorContext flags, VariableTable table, BooleanEqualityTable bool) {
		List<List<Literal<?,?>>> result = new ArrayList<List<Literal<?,?>>>();
		int start = 0;
		if (flags.isPositive) {
			for (ISignedFormula child : children) {
				List<TermSignature> subIndex = terms.subList(start, start + child.getIndexSize());
				prefix = child.getClauses(subIndex, manager, prefix, table, flags, bool);
				start += child.getIndexSize();
			}
			result = prefix;
		} else {
			// we split because it is a conjunction
			for (ISignedFormula child : children) {
				List<TermSignature> subIndex = terms.subList(start, start + child.getIndexSize());
				List<List<Literal<?,?>>> copy = copyClauseList(prefix,table);
				result.addAll(child.getClauses(subIndex, manager, copy, table, flags, bool));
				start += child.getIndexSize();
			}
		}
		return result;
	}
	
	public void getFinalClauses(Collection<Clause> clauses, LabelManager manager, ClauseFactory factory, BooleanEqualityTable bool, VariableTable table, IVariableContext context, boolean positive) {
		if (positive) {
			if (ClauseBuilder.DEBUG) ClauseBuilder.debug("----------------");
			if (ClauseBuilder.DEBUG) ClauseBuilder.debug("Positive definition:");
			getFinalClausesHelper(manager, clauses, factory, false, true, bool, table, context);
		} else {
			if (ClauseBuilder.DEBUG) ClauseBuilder.debug("----------------");
			if (ClauseBuilder.DEBUG) ClauseBuilder.debug("Negative definition:");
			getFinalClausesHelper(manager, clauses, factory, true, false, bool, table, context);
		}
	}
	
	@Override
	public boolean isEquivalence() {
		return false;
	}

	@Override
	protected DisjunctiveClauseDescriptor getNewDescriptor(List<IIntermediateResult> result, int index) {
		return new DisjunctiveClauseDescriptor(descriptor.getContext(), result, index);
	}


	public boolean hasEquivalenceFirst() {
		return false;
	}

}
