/*******************************************************************************
 * Copyright (c) 2006, 2007 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.loader.predicate;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.DefaultVisitor;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.internal.pp.core.elements.Sort;
import org.eventb.internal.pp.core.tracing.IOrigin;
import org.eventb.internal.pp.core.tracing.PredicateOrigin;
import org.eventb.internal.pp.loader.formula.AbstractClause;
import org.eventb.internal.pp.loader.formula.AbstractFormula;
import org.eventb.internal.pp.loader.formula.ArithmeticFormula;
import org.eventb.internal.pp.loader.formula.BooleanEqualityFormula;
import org.eventb.internal.pp.loader.formula.DisjunctiveClause;
import org.eventb.internal.pp.loader.formula.EqualityFormula;
import org.eventb.internal.pp.loader.formula.EquivalenceClause;
import org.eventb.internal.pp.loader.formula.PredicateFormula;
import org.eventb.internal.pp.loader.formula.QuantifiedFormula;
import org.eventb.internal.pp.loader.formula.SignedFormula;
import org.eventb.internal.pp.loader.formula.ArithmeticFormula.Type;
import org.eventb.internal.pp.loader.formula.descriptor.ArithmeticDescriptor;
import org.eventb.internal.pp.loader.formula.descriptor.DisjunctiveClauseDescriptor;
import org.eventb.internal.pp.loader.formula.descriptor.EqualityDescriptor;
import org.eventb.internal.pp.loader.formula.descriptor.EquivalenceClauseDescriptor;
import org.eventb.internal.pp.loader.formula.descriptor.LiteralDescriptor;
import org.eventb.internal.pp.loader.formula.descriptor.PredicateDescriptor;
import org.eventb.internal.pp.loader.formula.descriptor.QuantifiedDescriptor;
import org.eventb.internal.pp.loader.formula.key.ArithmeticKey;
import org.eventb.internal.pp.loader.formula.key.DisjunctiveClauseKey;
import org.eventb.internal.pp.loader.formula.key.EqualityKey;
import org.eventb.internal.pp.loader.formula.key.EquivalenceClauseKey;
import org.eventb.internal.pp.loader.formula.key.PredicateKey;
import org.eventb.internal.pp.loader.formula.key.QuantifiedLiteralKey;
import org.eventb.internal.pp.loader.formula.key.SymbolKey;
import org.eventb.internal.pp.loader.formula.key.SymbolTable;
import org.eventb.internal.pp.loader.formula.terms.TermSignature;
import org.eventb.internal.pp.loader.ordering.LiteralOrderer;

/**
 * This class is responsible for building an intermediate data structure that is
 * used to construct the final clauses. The classes composing this intermediate
 * data structure are located in package org.eventb.internal.pp.loader.formula.
 * <p>
 * In that intermediate data structure, all single predicates and all
 * sub-formulas are factored and represented by the same descriptor (descriptors
 * are located in package org.eventb.internal.pp.loader.formula.descriptor).
 * Each individual instance of these predicates and sub-formulas are represented
 * by a different instance of {@link AbstractFormula} or {@link SignedFormula}.
 * </p>
 * 
 * @author François Terrier
 */
public class PredicateBuilder extends DefaultVisitor implements ILiteralBuilder {

	/**
	 * Debug flag for <code>LOADER_PHASE1_TRACE</code>
	 */
	public static boolean DEBUG = false;
	
	private static final BoundIdentDecl[] NO_BIDS = new BoundIdentDecl[0];

	public static void debug(String message){
		System.out.println(message);
	}
	
	private TermBuilder termBuilder;
	
	// these are persistent variables that are completed at each
	// iteration
	private AbstractContext context;
	
	// these are the encountered index and corresponding sorts
	// these 3 variables are reset at each predicate
	private Stack<NormalizedFormula> result;
	private IntermediateResult inRes;
	private boolean isPositive;

	// Origin of the predicate currently being built.
	private IOrigin origin;
	
	public PredicateBuilder() {
		// TODO remove this static call
//		ArithmeticKey.resetCounter();
		this.context = new AbstractContext();
	}
	
	/**
	 * Builds the given predicate and use originalPredicate as its origin.
	 * <p>
	 * If isGoal is true, the predicate will be loaded as a goal (negated).
	 * 
	 * @param predicate the predicate to load
	 * @param originalPredicate the original predicate to use in the origin
	 * @param isGoal <code>true</code> if the predicate should be loaded as the goal
	 */
	public void build(Predicate predicate, Predicate originalPredicate, boolean isGoal) {
		this.origin = new PredicateOrigin(originalPredicate, isGoal);
		buildInternal(predicate, isGoal);
		this.origin = null;
	}
	
	public void build(Predicate predicate, boolean isGoal) {
		this.origin = new PredicateOrigin(predicate, isGoal);
		buildInternal(predicate, isGoal);
		this.origin = null;
	}
	
	private boolean checkPredicateTag(Predicate predicate) {
		if (predicate == null) {
			return false;
		}
		switch (predicate.getTag()) {
		case Formula.EXISTS:
		case Formula.FORALL:
		case Formula.NOT:
		case Formula.LIMP:
		case Formula.LEQV:
		case Formula.LAND:
		case Formula.LOR:
		case Formula.IN:
		case Formula.EQUAL:
		case Formula.NOTEQUAL:
		case Formula.LE:
		case Formula.LT:
		case Formula.GE:
		case Formula.GT:
			return true;
		}
		return false;
	}
	
	private void buildInternal(Predicate predicate, boolean isGoal) {
		assert checkPredicateTag(predicate) : "Unexpected operator: "
				+ predicate;
		assert predicate.isTypeChecked() : predicate;
		
		if (DEBUG) {
			debug("========================================");
			debug("Loading " + (isGoal ? "goal" : "hypothese") + ": "
					+ predicate);
		}
	
		this.result = new Stack<NormalizedFormula>();
		this.termBuilder = new TermBuilder(context);
		this.inRes = new IntermediateResult(/*new TermOrderer()*/);
		this.isPositive = true;
		
		pushNewList(NO_BIDS);
		predicate.accept(this);

		final NormalizedFormula res = result.pop();
		assert result.isEmpty();
		
		if (isGoal) {
			res.getSignature().negate();
		}
		context.addResult(res);
	}

	private void pushNewTerm(TermSignature term) {
		inRes.addResult(term);
	}
	
	private void pushNewList(BoundIdentDecl[] decls) {
		final int startOffset = termBuilder.getNumberOfDecls();
		termBuilder.pushDecls(decls);
		final int endOffset = termBuilder.getNumberOfDecls()-1;
		result.push(new NormalizedFormula(new LiteralOrderer(),startOffset,endOffset,decls,origin));
	}

	private void clean() {
		this.inRes = new IntermediateResult(/*new TermOrderer()*/);
	}
	
	public IContext getContext() {
		return context;
	}

	@Override
	public boolean enterNOT(UnaryPredicate pred) {
		isPositive = !isPositive;
		return true;
	}

	@Override
	public boolean exitNOT(UnaryPredicate pred) {
		isPositive = !isPositive;
		return true;
	}

	private void exitRelationalPredicate(RelationalPredicate pred, boolean arith) {
		if (pred.getLeft().getTag() != Formula.MAPSTO) {
			if (!arith) checkTag(pred.getLeft());
			TermSignature term = termBuilder.buildTerm(pred.getLeft());
			pushNewTerm(term);
		}
		if (!arith) checkTag(pred.getRight());
		TermSignature term = termBuilder.buildTerm(pred.getRight());
		pushNewTerm(term);
	}
	
	private void enterArithmetic(RelationalPredicate pred) {
		debugEnter(pred);
	}
	
	@Override
	public boolean enterGE(RelationalPredicate pred) {
		enterArithmetic(pred);
		return true;
	}

	@Override
	public boolean enterGT(RelationalPredicate pred) {
		enterArithmetic(pred);
		return true;
	}

	@Override
	public boolean enterLE(RelationalPredicate pred) {
		enterArithmetic(pred);
		return true;
	}

	@Override
	public boolean enterLT(RelationalPredicate pred) {
		enterArithmetic(pred);
		return true;
	}
	
	@Override
	public boolean exitGE(RelationalPredicate pred) {
		exitRelationalPredicate(pred,true);
		exitArithmetic(Type.LESS,! isPositive);
		return true;
	}

	@Override
	public boolean exitGT(RelationalPredicate pred) {
		exitRelationalPredicate(pred,true);
		exitArithmetic(Type.LESS_EQUAL,! isPositive);
		return true;
	}

	@Override
	public boolean exitLE(RelationalPredicate pred) {
		exitRelationalPredicate(pred,true);
		exitArithmetic(Type.LESS_EQUAL,isPositive);
		return true;
	}

	@Override
	public boolean exitLT(RelationalPredicate pred) {
		exitRelationalPredicate(pred,true);
		exitArithmetic(Type.LESS,isPositive);
		return true;
	}
	
	private void exitArithmetic(Type type, boolean sign) {
		exitArithmeticLiteral(type, sign);
		debugExit();
	}
	
	private void exitArithmeticLiteral(Type type, boolean sign) {
		assert inRes.getTerms().size() == 2;
		// TODO normalize arithmetic and order terms
		
		List<TermSignature> simpleTerms = new ArrayList<TermSignature>();
		List<TermSignature> terms = getSimpleTerms(inRes.getTerms(), simpleTerms);
		
		IntermediateResult interRes = new IntermediateResult(simpleTerms/*, new TermOrderer()*/);
		
		ArithmeticKey key = new ArithmeticKey(terms,type);
		ArithmeticDescriptor desc = updateDescriptor(key, context.getArithmeticTable(), interRes, "arithmetic");
		desc.addResult(interRes);
		ArithmeticFormula sig = new ArithmeticFormula(type,interRes.getTerms(),terms,desc);
		if (DEBUG) debug(prefix+"Adding terms to "+desc+": "+interRes);
		
		result.peek().addResult(new SignedFormula<ArithmeticDescriptor>(sig, sign),interRes);
		clean();
	}

	private List<TermSignature> getSimpleTerms(List<TermSignature> originalList, List<TermSignature> simpleTerms) {
		List<TermSignature> result = new ArrayList<TermSignature>();
		for (TermSignature signature : originalList) {
			result.add(signature.getSimpleTerm(simpleTerms));
		}
		return result;
	}
	
	@Override
	public boolean enterIN(RelationalPredicate pred) {
		debugEnter(pred);
		return true;
	}
	
	@Override
	public boolean exitIN(RelationalPredicate pred) {
		exitRelationalPredicate(pred,false);

		SymbolKey<PredicateDescriptor> key = new PredicateKey(new Sort(pred.getRight().getType()));
		
		PredicateDescriptor desc = updateDescriptor(key, context.getLiteralTable(), inRes, "predicate");
		PredicateFormula lit = new PredicateFormula(inRes.getTerms(), desc);		

		result.peek().addResult(new SignedFormula<PredicateDescriptor>(lit,isPositive),inRes);
		clean();
		debugExit();
		return true;
	}

	private void checkTag(Expression expr) {
		if (	expr.getTag()!=Formula.BOUND_IDENT && 
				expr.getTag()!=Formula.FREE_IDENT &&
				expr.getTag()!=Formula.INTLIT) {
			throw new IllegalArgumentException("Input not allowed at this position: "+expr);
		}
	}
	
	@Override
	public boolean enterMAPSTO(BinaryExpression expr) {
		if (expr.getLeft().getTag() != Formula.MAPSTO) {
			checkTag(expr.getLeft());
			TermSignature term = termBuilder.buildTerm(expr.getLeft());
			pushNewTerm(term);
		}
		return true;
	}

	@Override
	public boolean exitMAPSTO(BinaryExpression expr) {
		if (expr.getRight().getTag() != Formula.MAPSTO) {
			checkTag(expr.getRight());
			TermSignature term = termBuilder.buildTerm(expr.getRight());
			pushNewTerm(term);
		}
		return true;
	}
	
	public boolean exitEquality (RelationalPredicate pred, boolean sign) {
		exitRelationalPredicate(pred,true);
		// treat arithmetic equality as arithmetic literals
		if (pred.getRight().getType().equals(FormulaFactory.getDefault().makeIntegerType())) {
			exitArithmeticLiteral(Type.EQUAL, sign);
		}
		else {
			exitEqualityLiteral(new Sort(pred.getRight().getType()), sign);
		}
		debugExit();
		return true;
	}
	
	private void exitEqualityLiteral(Sort sort, boolean sign) {
		SymbolKey<EqualityDescriptor> key = new EqualityKey(sort);
		EqualityDescriptor desc = updateDescriptor(key, context.getEqualityTable(), inRes, "equality");
		
		AbstractFormula<EqualityDescriptor> sig;
		if (sort.equals(Sort.BOOLEAN)) {
			sig = new BooleanEqualityFormula(inRes.getTerms(), desc);
		}
		else {
			sig = new EqualityFormula(inRes.getTerms(), desc);
		}
		// TODO implement an ordering on terms
		// inRes.orderList();
		// from here indexes will be ordered
		result.peek().addResult(new SignedFormula<EqualityDescriptor>(sig, sign), inRes);
		clean();
	}
	
	private void enterEquality(RelationalPredicate pred) {
		debugEnter(pred);
	}
	
	@Override
	public boolean enterEQUAL(RelationalPredicate pred) {
		enterEquality(pred);
		return true;
	}
	
	@Override
	public boolean enterNOTEQUAL(RelationalPredicate pred) {
		enterEquality(pred);
		return true;
	}
	
	@Override
	public boolean exitNOTEQUAL(RelationalPredicate pred) {
		exitEquality(pred, !isPositive);
		return true;
	}

	@Override
	public boolean exitEQUAL(RelationalPredicate pred) {
		exitEquality(pred, isPositive);
		return true;
	}
	
	private void enterLogicalOperator() {
		pushNewList(NO_BIDS);
	}
	
	private StringBuilder prefix = new StringBuilder("");
	private void debugEnter(Predicate pred) {
		if (DEBUG) debug(prefix+"Entering "+pred);
		prefix.append("  ");
	}
	
	private void debugExit() {
		prefix.deleteCharAt(prefix.length()-1);
		prefix.deleteCharAt(prefix.length()-1);
	}
	
	private <T extends LiteralDescriptor> T updateDescriptor(SymbolKey<T> key, SymbolTable<T> table, IIntermediateResult res, String debug) {
		T desc = table.get(key);
		if (desc == null) {
			desc = key.newDescriptor(context);
			table.add(key, desc);
			if (DEBUG) debug(prefix+"New "+debug+" with "+key+", becomes: "+desc.toString());
		}
		desc.addResult(res);
		if (DEBUG) debug(prefix+"Adding terms to "+desc+": "+res);
		return desc;
	}
	
	private void exitLogicalOperator(boolean isEquivalence) {
		NormalizedFormula res = result.pop();

		// let us order the list
		res.orderList();
		if (isEquivalence) {
			// we order the list and put the negation in front before
			// the key is created, ensuring a correct factorization
			res.reduceNegations();
		}
		List<SignedFormula<?>> literals = res.getLiterals();
		IIntermediateResult iRes = res.getNewIntermediateResult();
		
		AbstractClause<?> sig;
		if (isEquivalence) {
			SymbolKey<EquivalenceClauseDescriptor> key = new EquivalenceClauseKey(literals);
			EquivalenceClauseDescriptor desc = updateDescriptor(key, context.getEqClauseTable(), iRes, "equivalence clause");
			sig = new EquivalenceClause(literals,iRes.getTerms(),desc);
		} else {
			SymbolKey<DisjunctiveClauseDescriptor> key = new DisjunctiveClauseKey(literals);
			DisjunctiveClauseDescriptor desc = updateDescriptor(key, context.getDisjClauseTable(), iRes, "disjunctive clause");
			sig = new DisjunctiveClause(literals,iRes.getTerms(),desc);
		}

		// we create the new signature
		@SuppressWarnings("unchecked")
		SignedFormula<?> lit = new SignedFormula(sig,res.isPositive());
		
		// we append the new literal to the result before
		result.peek().addResult(lit, iRes);
	}
	
	@Override
	public boolean enterLOR(AssociativePredicate pred) {
		debugEnter(pred);
		enterLogicalOperator();
		
		result.peek().setPositive(isPositive);
		isPositive = true;
		return true;
	}
	
	@Override
	public boolean continueLOR(AssociativePredicate pred) {
		return true;
	}
	
	@Override
	public boolean exitLOR(AssociativePredicate pred) {
		isPositive = result.peek().isPositive();
		exitLogicalOperator(false);
		debugExit();
		return true;
	}
	
	@Override
	public boolean enterLAND(AssociativePredicate pred) {
		debugEnter(pred);
		enterLogicalOperator();
		result.peek().setPositive(!isPositive);
		isPositive = false;
		return true;
	}
	
	@Override
	public boolean exitLAND(AssociativePredicate pred) {
		isPositive = !result.peek().isPositive();
		exitLogicalOperator(false);
		debugExit();
		return true;
	}

	@Override
	public boolean enterLIMP(BinaryPredicate pred) {
		debugEnter(pred);
		enterLogicalOperator();
		result.peek().setPositive(isPositive);
		isPositive = false;
		return true;
	}
	
	@Override
	public boolean continueLIMP(BinaryPredicate pred) {
		isPositive = true;
		return true;
	}
	
	@Override
	public boolean exitLIMP(BinaryPredicate pred) {
		isPositive = result.peek().isPositive();
		exitLogicalOperator(false);
		debugExit();
		return true;
	}
	
	@Override
	public boolean enterLEQV(BinaryPredicate pred) {
		debugEnter(pred);
		enterLogicalOperator();
		
		result.peek().setPositive(isPositive);
		isPositive = true;
		return true;
	}
	
	@Override
	public boolean continueLEQV(BinaryPredicate pred) {
		return true;
	}
	
	@Override
	public boolean exitLEQV(BinaryPredicate pred) {
		isPositive = result.peek().isPositive();
		exitLogicalOperator(true);
		debugExit();
		return true;
	}
	
	@Override
	public boolean enterEXISTS(QuantifiedPredicate pred) {
		enterQuantifiedPredicate(pred);
		return true;
	}

	@Override
	public boolean exitEXISTS(QuantifiedPredicate pred) {
		exitQuantifiedPredicate(pred, false);
		return true;
	}
	
	@Override
	public boolean enterFORALL(QuantifiedPredicate pred) {
		enterQuantifiedPredicate(pred);
		return true;
	}
	
	@Override
	public boolean exitFORALL(QuantifiedPredicate pred) {
		exitQuantifiedPredicate(pred, true);
		return true;
	}
	
	private void enterQuantifiedPredicate(QuantifiedPredicate pred) {
		debugEnter(pred);
		pushNewList(pred.getBoundIdentDecls());
	}
	
	private void exitQuantifiedPredicate(QuantifiedPredicate pred, boolean isForall) {
		isForall = isPositive?isForall:!isForall;
		
		termBuilder.popDecls(pred.getBoundIdentDecls());
		
		NormalizedFormula res = result.pop();
		SignedFormula<?> quantified = res.getLiterals().get(0);
		
		List<TermSignature> quantifiedTerms = new ArrayList<TermSignature>();		
		List<TermSignature> unquantifiedTerms = getUnquantifiedTerms(res.getTerms(), quantifiedTerms, res.getStartOffset(), res.getEndOffset());
		
		IntermediateResult interRes = new IntermediateResult(quantifiedTerms/*,new TermOrderer()*/);
		SymbolKey<QuantifiedDescriptor> key = new QuantifiedLiteralKey(quantified,unquantifiedTerms,isForall);
		
		QuantifiedDescriptor desc = updateDescriptor(key, context.getQuantifiedTable(), interRes, "quantified");
		QuantifiedFormula sig = new QuantifiedFormula(isForall,quantified,unquantifiedTerms,interRes.getTerms(),desc,res.getStartOffset(),res.getEndOffset());
		
		result.peek().addResult(new SignedFormula<QuantifiedDescriptor>(sig, true), interRes);
		clean();
		
		debugExit();
	}
	
	private List<TermSignature> getUnquantifiedTerms(List<TermSignature> terms, List<TermSignature> quantifiedTerms, int startOffset, int endOffset) {
		List<TermSignature> unquantifiedSignature = new ArrayList<TermSignature>();
		for (TermSignature term : terms) {
			unquantifiedSignature.add(term.getUnquantifiedTerm(startOffset, endOffset, quantifiedTerms));
		}
		return unquantifiedSignature;
	}
	
}
