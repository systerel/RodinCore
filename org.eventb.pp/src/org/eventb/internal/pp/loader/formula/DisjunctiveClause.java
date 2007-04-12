package org.eventb.internal.pp.loader.formula;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eventb.internal.pp.core.elements.ClauseFactory;
import org.eventb.internal.pp.core.elements.IClause;
import org.eventb.internal.pp.core.elements.ILiteral;
import org.eventb.internal.pp.loader.clause.BooleanEqualityTable;
import org.eventb.internal.pp.loader.clause.ClauseBuilder;
import org.eventb.internal.pp.loader.clause.LabelManager;
import org.eventb.internal.pp.loader.clause.VariableTable;
import org.eventb.internal.pp.loader.formula.descriptor.DisjunctiveClauseDescriptor;
import org.eventb.internal.pp.loader.formula.terms.TermSignature;
import org.eventb.internal.pp.loader.predicate.IIntermediateResult;

public class DisjunctiveClause extends AbstractClause<DisjunctiveClauseDescriptor> implements
		ILabelizableFormula<DisjunctiveClauseDescriptor>, ISubFormula<DisjunctiveClauseDescriptor> {
	
	public DisjunctiveClause(List<ISignedFormula> children,
			List<TermSignature> terms, DisjunctiveClauseDescriptor descriptor) {
		super(children,terms,descriptor);
	}
	
	
	private List<List<ILiteral>> copyClauseList(List<List<ILiteral>> original) {
		List<List<ILiteral>> result = new ArrayList<List<ILiteral>>();
		for (List<ILiteral> list : original) {
			result.add(new ArrayList<ILiteral>(list));
		}
		return result;
	}

	public List<List<ILiteral>> getDefinitionClauses(List<TermSignature> terms,
			LabelManager manager, List<List<ILiteral>> prefix,
			TermVisitorContext flags, VariableTable table, BooleanEqualityTable bool) {
		flags.isEquivalence = false;
		flags.isQuantified = false;
		List<List<ILiteral>> result = new ArrayList<List<ILiteral>>();
		int start = 0;
		if (!flags.isPositive) {
			for (ISignedFormula child : children) {
				List<TermSignature> subIndex = terms.subList(start, start + child.getIndexSize());
				List<List<ILiteral>> copy = copyClauseList(prefix);
				result.addAll(child.getClauses(subIndex, manager, copy, flags, table, bool));
				manager.setForceLabelize(false);
				start += child.getIndexSize();
			}
		}
		else {
			for (ISignedFormula child : children) {
				List<TermSignature> subIndex = terms.subList(start, start + child.getIndexSize());
				prefix = child.getClauses(subIndex, manager, prefix, flags, table, bool);
				manager.setForceLabelize(false);
				start += child.getIndexSize();
			}
			result = prefix;
		}
		return result;
	}
	
	public void getFinalClauses(Collection<IClause> clauses, LabelManager manager, ClauseFactory factory, BooleanEqualityTable bool, VariableTable variableTable, boolean positive) {
		if (!positive) {
			ClauseBuilder.debug("----------------");
			ClauseBuilder.debug("Negative definition:");
			getFinalClausesHelper(manager, clauses, factory, true, false, bool, variableTable);
		}
		else {
			ClauseBuilder.debug("----------------");
			ClauseBuilder.debug("Positive definition:");
			getFinalClausesHelper(manager, clauses, factory, false, true, bool, variableTable);
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

}
