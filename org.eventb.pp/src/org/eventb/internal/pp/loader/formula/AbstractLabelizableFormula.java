package org.eventb.internal.pp.loader.formula;

import java.util.ArrayList;
import java.util.List;

import org.eventb.internal.pp.core.elements.Literal;
import org.eventb.internal.pp.loader.clause.BooleanEqualityTable;
import org.eventb.internal.pp.loader.clause.ClauseBuilder;
import org.eventb.internal.pp.loader.clause.LabelManager;
import org.eventb.internal.pp.loader.clause.VariableTable;
import org.eventb.internal.pp.loader.formula.descriptor.LiteralDescriptor;
import org.eventb.internal.pp.loader.formula.terms.TermSignature;

public abstract class AbstractLabelizableFormula<T extends LiteralDescriptor>
		extends AbstractFormula<T> {

	public AbstractLabelizableFormula(List<TermSignature> terms, T descriptor) {
		super(terms, descriptor);
	}

	
	final ClauseResult getFinalClausesHelper(LabelManager manager,
			boolean positive1, boolean positive2, boolean equivalence, 
			BooleanEqualityTable bool, VariableTable variableTable) {
		TermVisitorContext context = new TermVisitorContext(equivalence);
		// positive part of label
		context.isPositive = positive1;
		TermVisitorContext newContext = getNewContext(context);
		
		List<List<Literal<?, ?>>> prefix = new ArrayList<List<Literal<?, ?>>>();
		prefix.add(new ArrayList<Literal<?, ?>>());
		List<List<Literal<?, ?>>> positiveLiterals = 
			getDefinitionClauses(descriptor.getUnifiedResults(), manager, prefix, newContext, variableTable, bool);
		
		context.isPositive = positive2;
		newContext = getNewContext(context);
		Literal<?, ?> posLiteral = getLiteral(descriptor.getUnifiedResults(),
				newContext, variableTable, bool);
		for (List<Literal<?, ?>> positiveClause : positiveLiterals) {
			positiveClause.add(0, posLiteral);
		}
		return new ClauseResult(equivalence, positiveLiterals);
	}

	abstract List<List<Literal<?, ?>>> getDefinitionClauses(
			List<TermSignature> termList, LabelManager manager,
			List<List<Literal<?, ?>>> prefix, TermVisitorContext flags,
			VariableTable table, BooleanEqualityTable bool);

	
	public abstract ClauseResult getFinalClauses(LabelManager manager,
			BooleanEqualityTable bool, VariableTable table,
			boolean positive, boolean equivalence);

	
	@Override
	List<List<Literal<?, ?>>> getClauses(List<TermSignature> termList,
			LabelManager manager, List<List<Literal<?, ?>>> prefix,
			TermVisitorContext context, VariableTable table,
			BooleanEqualityTable bool) {
		ClauseBuilder.debugEnter(this);
		List<List<Literal<?, ?>>> result;
		if (manager.hasLabel(this)) {
			Literal<?, ?> literal = getLiteral(termList, context, table, bool);
			for (List<Literal<?, ?>> list : prefix) {
				list.add(literal);
			}
			result = prefix;
		} else {
			TermVisitorContext newContext = getNewContext(context);
			if (ClauseBuilder.DEBUG) ClauseBuilder.debug(this + " can be simplified");
			result = getDefinitionClauses(termList, manager, prefix,
					newContext, table, bool);
		}
		ClauseBuilder.debugExit(this);
		return result;
	}
	
	abstract TermVisitorContext getNewContext(TermVisitorContext context);
	
}
