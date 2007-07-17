package org.eventb.internal.pp.loader.formula;

import java.util.List;

import org.eventb.internal.pp.core.elements.Literal;
import org.eventb.internal.pp.loader.clause.BooleanEqualityTable;
import org.eventb.internal.pp.loader.clause.ClauseBuilder;
import org.eventb.internal.pp.loader.clause.LabelManager;
import org.eventb.internal.pp.loader.clause.VariableTable;
import org.eventb.internal.pp.loader.formula.descriptor.LiteralDescriptor;
import org.eventb.internal.pp.loader.formula.terms.TermSignature;

public abstract class AbstractSingleFormula<T extends LiteralDescriptor> extends AbstractFormula<T> {


	public AbstractSingleFormula(List<TermSignature> terms, T descriptor) {
		super(terms, descriptor);
	}

	@Override
	List<List<Literal<?,?>>> getClauses(List<TermSignature> termList,
			LabelManager manager, List<List<Literal<?,?>>> prefix,
			TermVisitorContext flags, VariableTable table, BooleanEqualityTable bool) {
		ClauseBuilder.debugEnter(this);
		Literal<?,?> literal = getLiteral(termList, flags, table, bool);
		for (List<Literal<?,?>> list : prefix) {
			list.add(literal);
		}
		ClauseBuilder.debugExit(this);
		return prefix;
	}

	@Override
	String toTreeForm(String prefix) {
		return toString() + getTerms().toString();
	}

	@Override
	protected boolean getContextAndSetLabels(LabelVisitor context, LabelManager manager) {
		return false;
	}
	
}
