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
import org.eventb.internal.pp.loader.formula.descriptor.EquivalenceClauseDescriptor;
import org.eventb.internal.pp.loader.formula.terms.TermSignature;
import org.eventb.internal.pp.loader.predicate.IIntermediateResult;

public class EquivalenceClause extends AbstractClause<EquivalenceClauseDescriptor> {
	
	public EquivalenceClause(List<ISignedFormula> children,
			List<TermSignature> terms, EquivalenceClauseDescriptor descriptor) {
		super(children,terms,descriptor);
	}

	public List<List<Literal<?,?>>> getDefinitionClauses(List<TermSignature> termList,
			LabelManager manager, List<List<Literal<?,?>>> prefix, TermVisitorContext flags, VariableTable table, BooleanEqualityTable bool) {
		List<List<Literal<?,?>>> result = new ArrayList<List<Literal<?,?>>>();
		int start = 0;
		if (flags.isPositive) {
			for (ISignedFormula child : children) {
				List<TermSignature> subIndex = termList.subList(start, start + child.getIndexSize());
				prefix = child.getClauses(subIndex, manager, prefix, table, flags, bool);
				start += child.getIndexSize();
			}
			result = prefix;
		}
		else {
			boolean first = true;
			for (ISignedFormula child : children) {
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

	public void getFinalClauses(Collection<Clause> clauses, LabelManager manager, ClauseFactory factory, BooleanEqualityTable bool, VariableTable table, IVariableContext variableContext, boolean positive) {
		if (ClauseBuilder.DEBUG) ClauseBuilder.debug("----------------");
		if (ClauseBuilder.DEBUG) ClauseBuilder.debug("Equivalence definition:");
		getFinalClausesHelper(manager, clauses, factory, true, true, bool, table, variableContext);
	}
	
	@Override
	public boolean isEquivalence() {
		return true;
	}

	@Override
	protected EquivalenceClauseDescriptor getNewDescriptor(List<IIntermediateResult> result, int index) {
		return new EquivalenceClauseDescriptor(descriptor.getContext(), result, index);
	}

	public boolean hasEquivalenceFirst() {
		return true;
	}

}