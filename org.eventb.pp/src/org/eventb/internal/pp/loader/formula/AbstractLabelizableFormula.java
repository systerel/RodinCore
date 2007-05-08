package org.eventb.internal.pp.loader.formula;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.elements.ClauseFactory;
import org.eventb.internal.pp.core.elements.IClause;
import org.eventb.internal.pp.core.elements.ILiteral;
import org.eventb.internal.pp.core.tracing.DefinitionOrigin;
import org.eventb.internal.pp.loader.clause.BooleanEqualityTable;
import org.eventb.internal.pp.loader.clause.ClauseBuilder;
import org.eventb.internal.pp.loader.clause.LabelManager;
import org.eventb.internal.pp.loader.clause.VariableTable;
import org.eventb.internal.pp.loader.formula.descriptor.LiteralDescriptor;
import org.eventb.internal.pp.loader.formula.terms.TermSignature;

public abstract class AbstractLabelizableFormula<T extends LiteralDescriptor> extends AbstractFormula<T> implements
		ILabelizableFormula<T> {

	public AbstractLabelizableFormula(List<TermSignature> terms, T descriptor) {
		super(terms, descriptor);
	}

	protected void getFinalClausesHelper(LabelManager manager, Collection<IClause> clauses, 
			ClauseFactory factory, boolean positive1, boolean positive2, 
			BooleanEqualityTable bool, VariableTable variableTable, IVariableContext variableContext) {
		TermVisitorContext context = new TermVisitorContext(hasEquivalenceFirst());
		// positive part of label
		context.isPositive = positive1;
		TermVisitorContext newContext = getNewContext(context);
		List<List<ILiteral<?>>> positiveLiterals = getDefinitionClauses(manager, newContext, variableTable, bool);
		context.isPositive = positive2;
		newContext = getNewContext(context);
		ILiteral<?> posLiteral = getLiteral(descriptor.getUnifiedResults(), newContext, variableTable, bool);
		for (List<ILiteral<?>> positiveClause : positiveLiterals) {
			positiveClause.add(0, posLiteral);
			IClause clause;
			if (context.isEquivalence && positiveClause.size() > 1) {
				clause = factory.newEqClauseWithCopy(new DefinitionOrigin(), positiveClause,variableContext);
			}
			else {
				clause = factory.newDisjClauseWithCopy(new DefinitionOrigin(), positiveClause,variableContext);	
			}
			clauses.add(clause);
			ClauseBuilder.debug("New clause: "+clause);
		}
	}
	
	protected List<List<ILiteral<?>>> getDefinitionClauses(LabelManager manager, TermVisitorContext context, VariableTable table, BooleanEqualityTable bool) {
		List<List<ILiteral<?>>> prefix = new ArrayList<List<ILiteral<?>>>();
		prefix.add(new ArrayList<ILiteral<?>>());
		return getDefinitionClauses(descriptor.getUnifiedResults(), manager, prefix, context, table, bool);
	}
	
	protected abstract boolean isLabelizable(LabelManager manager, TermVisitorContext context);
	
	public List<List<ILiteral<?>>> getClauses(List<TermSignature> termList, LabelManager manager, List<List<ILiteral<?>>> prefix, TermVisitorContext context, VariableTable table, BooleanEqualityTable bool) {
		ClauseBuilder.debugEnter(this);
		List<List<ILiteral<?>>> result;
		if (isLabelizable(manager, context)) {
			ClauseBuilder.debug(this + " cannot be simplified");
			
			if (isEquivalence()) manager.addEquivalenceLabel(this);
			// if it is used only once, we labelize only one
			// TODO optimize more, if used several times, but only positively or negatively
			if (!context.isEquivalence && descriptor.getResults().size() == 1) {
				manager.addLabel(this, context.isPositive);
			}
			else {
				manager.addLabel(this, true);
				manager.addLabel(this, false);
			}
				
			for (List<ILiteral<?>> list : prefix) {
				list.add(getLiteral(termList, context, table, bool));
			}
			result = prefix;
		}
		else {
			TermVisitorContext newContext = getNewContext(context);
			ClauseBuilder.debug(this + " can be simplified");
			result = getDefinitionClauses(termList, manager, prefix, newContext, table, bool);
		}
		ClauseBuilder.debugExit(this);
		return result;
	}
}
