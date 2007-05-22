package org.eventb.internal.pp.loader.formula;

import java.util.List;

import org.eventb.internal.pp.core.elements.ILiteral;
import org.eventb.internal.pp.loader.clause.BooleanEqualityTable;
import org.eventb.internal.pp.loader.clause.ClauseBuilder;
import org.eventb.internal.pp.loader.clause.LabelManager;
import org.eventb.internal.pp.loader.clause.VariableTable;
import org.eventb.internal.pp.loader.formula.descriptor.LiteralDescriptor;
import org.eventb.internal.pp.loader.formula.terms.TermSignature;

public abstract class AbstractSingleFormula<T extends LiteralDescriptor>
		extends AbstractFormula<T> {

	public AbstractSingleFormula(List<TermSignature> terms, T descriptor) {
		super(terms, descriptor);
	}

	public List<List<ILiteral<?>>> getClauses(List<TermSignature> termList,
			LabelManager manager, List<List<ILiteral<?>>> prefix,
			TermVisitorContext flags, VariableTable table,
			BooleanEqualityTable bool) {
		ClauseBuilder.debugEnter(this);
		ILiteral<?> literal = getLiteral(termList, flags, table, bool);
		for (List<ILiteral<?>> list : prefix) {
			list.add(literal);
		}
		ClauseBuilder.debugExit(this);
		return prefix;
	}

	public void split() {
		return;
	}

	public TermVisitorContext getNewContext(TermVisitorContext context) {
		return context;
	}

	public String toTreeForm(String prefix) {
		return toString() + getTerms().toString();
	}

}
