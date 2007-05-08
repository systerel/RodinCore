/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
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
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.internal.pp.core.elements.Sort;
import org.eventb.internal.pp.loader.formula.AbstractClause;
import org.eventb.internal.pp.loader.formula.ArithmeticLiteral;
import org.eventb.internal.pp.loader.formula.BooleanEqualityLiteral;
import org.eventb.internal.pp.loader.formula.DisjunctiveClause;
import org.eventb.internal.pp.loader.formula.EqualityLiteral;
import org.eventb.internal.pp.loader.formula.EquivalenceClause;
import org.eventb.internal.pp.loader.formula.ISignedFormula;
import org.eventb.internal.pp.loader.formula.ISubFormula;
import org.eventb.internal.pp.loader.formula.PredicateLiteral;
import org.eventb.internal.pp.loader.formula.QuantifiedLiteral;
import org.eventb.internal.pp.loader.formula.SignedFormula;
import org.eventb.internal.pp.loader.formula.ArithmeticLiteral.Type;
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
import org.eventb.internal.pp.loader.ordering.TermOrderer;

/**
 * This class is used to build predicate literals for the PP prover. Each time
 * a predicate is created, the builder checks whether a predicate with the same
 * name and corresponding types already exists. If it is the case, that name is
 * used. If not, a new name is created. 
 * 
 * If the predicate is a quantifier ... 
 *
 * @author François Terrier
 *
 */
public class PredicateBuilder extends DefaultVisitor implements ILiteralBuilder {

	/**
	 * Debug flag for <code>LOADER_PHASE1_TRACE</code>
	 */
	public static boolean DEBUG;
	
	public static void debug(String message){
		if (DEBUG)
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

	private Predicate originalPredicate;
	private boolean isGoal;
	
	public PredicateBuilder() {
		// TODO remove this static call
		ArithmeticKey.resetCounter();
		this.context = new AbstractContext();
	}
	
	public void build(Predicate predicate, Predicate originalPredicate, boolean isGoal) {
		this.originalPredicate = originalPredicate;
		build(predicate, isGoal,false);
	}
	
	public void build(Predicate predicate, boolean isGoal) {
		this.originalPredicate = predicate;
		build(predicate, isGoal,false);
	}
	
	private void build(Predicate predicate, boolean isGoal, boolean dummy) {
		assert predicate != null && (predicate.getTag() == Formula.IN
			|| predicate.getTag() == Formula.EXISTS || predicate.getTag() == Formula.FORALL
			|| predicate.getTag() == Formula.LOR || predicate.getTag() == Formula.EQUAL
			|| predicate.getTag() == Formula.NOT || predicate.getTag() == Formula.LAND
			|| predicate.getTag() == Formula.LIMP || predicate.getTag() == Formula.NOTEQUAL
			|| predicate.getTag() == Formula.LEQV || predicate.getTag() == Formula.LE
			|| predicate.getTag() == Formula.LT || predicate.getTag() == Formula.GE
			|| predicate.getTag() == Formula.GT) : "Unexpected operator: "+predicate+"";
		assert predicate.isTypeChecked();
		
		debug("========================================");
		debug("Loading "+(isGoal?"goal":"hypothese")+": " + predicate);
		
	
		this.result = new Stack<NormalizedFormula>();
		this.termBuilder = new TermBuilder(result);
		this.inRes = new IntermediateResult(new TermOrderer());
		this.isPositive = true;
		this.isGoal = isGoal;
		
		pushNewList(new BoundIdentDecl[0]);
		predicate.accept(this);

		NormalizedFormula res = result.peek();
		assert result.size() == 1;
		
		if (isGoal) {
			// we inverse the sign
			res.getSignature().switchSign();
		}
		context.addResult(res);
	}
	
	
	
	private void pushNewTerm(TermSignature term) {
		inRes.addResult(term);
	}
	
	private void pushNewList(BoundIdentDecl[] decls) {
		int startOffset = context.getQuantifierOffset();
		context.incrementQuantifierOffset(decls.length);
		int startAbsolute = context.getNumberOfVariables();
		context.incrementNumberOfVariables(decls.length);
		int endOffset = context.getQuantifierOffset()-1;
		result.push(new NormalizedFormula(new LiteralOrderer(),startAbsolute,startOffset,endOffset,decls,originalPredicate,isGoal));
	}

	private void clean() {
		this.inRes = new IntermediateResult(new TermOrderer());
	}
	
	public IContext getContext() {
		return context;
	}
	
//	/**
//	 * Returns the list of terms on the current level. Each term occurs
//	 * uniquely in this list, according to the equals method, if two terms are
//	 * equal in the current level, it appears only once in this list.
//	 * 
//	 * @return the list of terms processed in the current level
//	 */
//	public List<TermSignature> getCurrentTerms() {
//		return inRes.getTerms();
//	}

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

	private void exitRelationalPredicate(RelationalPredicate pred) {
		if (pred.getLeft().getTag() != Formula.MAPSTO) {
			TermSignature term = termBuilder.buildTerm(pred.getLeft());
			pushNewTerm(term);
		}
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
		exitRelationalPredicate(pred);
		exitArithmetic(pred,Type.LESS,true);
		return true;
	}

	@Override
	public boolean exitGT(RelationalPredicate pred) {
		exitRelationalPredicate(pred);
		exitArithmetic(pred,Type.LESS_EQUAL,true);
		return true;
	}

	@Override
	public boolean exitLE(RelationalPredicate pred) {
		exitRelationalPredicate(pred);
		exitArithmetic(pred,Type.LESS_EQUAL,false);
		return true;
	}

	@Override
	public boolean exitLT(RelationalPredicate pred) {
		exitRelationalPredicate(pred);
		exitArithmetic(pred,Type.LESS,false);
		return true;
	}
	
	private void exitArithmetic(RelationalPredicate pred, Type type, boolean inverseSign) {
		exitArithmeticLiteral(type, inverseSign);
		debugExit();
	}
	
	private void exitArithmeticLiteral(Type type, boolean inverseSign) {
		// TODO think about normalization - not here, but in clause builder
		ArithmeticDescriptor desc = new ArithmeticDescriptor(context);
		desc.addResult(inRes);
		ArithmeticLiteral sig = new ArithmeticLiteral(type,inRes.getTerms(),desc);
		debug(prefix+"Adding terms to "+desc+": "+inRes);
		
		result.peek().addResult(new SignedFormula<ArithmeticDescriptor>(sig,inverseSign?!isPositive:isPositive),inRes);
		clean();
	}

	@Override
	public boolean enterIN(RelationalPredicate pred) {
		debugEnter(pred);
		return true;
	}
	
	@Override
	public boolean exitIN(RelationalPredicate pred) {
		exitRelationalPredicate(pred);
		
		// construct literal
		SymbolKey<PredicateDescriptor> key = new PredicateKey(new Sort(pred.getRight().getType()));
		
		PredicateDescriptor desc = updateDescriptor(key, context.getLiteralTable(), inRes, "predicate");
		PredicateLiteral lit = new PredicateLiteral(inRes.getTerms(), desc);		

		result.peek().addResult(new SignedFormula<PredicateDescriptor>(lit,isPositive),inRes);
		clean();
		debugExit();
		return true;
	}

	@Override
	public boolean enterMAPSTO(BinaryExpression expr) {
		if (expr.getLeft().getTag() != Formula.MAPSTO) {
			TermSignature term = termBuilder.buildTerm(expr.getLeft());
			pushNewTerm(term);
		}
		return true;
	}

	@Override
	public boolean exitMAPSTO(BinaryExpression expr) {
		if (expr.getRight().getTag() != Formula.MAPSTO) {
			TermSignature term = termBuilder.buildTerm(expr.getRight());
			pushNewTerm(term);
		}
		return true;
	}
	
	public boolean exitEquality (RelationalPredicate pred, boolean negative) {
		exitRelationalPredicate(pred);
		// treat arithmetic equality as arithmetic literals
		if (pred.getRight().getType().equals(FormulaFactory.getDefault().makeIntegerType())) {
			exitArithmeticLiteral(Type.EQUAL, negative);
		}
		else {
			exitEqualityLiteral(new Sort(pred.getRight().getType()), termBuilder.invertSign()?!negative:negative);
		}
		debugExit();
		return true;
	}
	
	private void exitEqualityLiteral(Sort sort, boolean negative) {
		SymbolKey<EqualityDescriptor> key = new EqualityKey(sort);
		EqualityDescriptor desc = updateDescriptor(key, context.getEqualityTable(), inRes, "equality");
		
		ISubFormula<EqualityDescriptor> sig;
		if (sort.equals(Sort.BOOLEAN)) {
			sig = new BooleanEqualityLiteral(inRes.getTerms(), desc);
		}
		else {
			sig = new EqualityLiteral(inRes.getTerms(), desc);
		}
		// TODO implement an ordering on terms
		// inRes.orderList();
		// from here indexes will be ordered
		result.peek().addResult(new SignedFormula<EqualityDescriptor>(sig,negative?!isPositive:isPositive), inRes);
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
		exitEquality(pred, true);
		return true;
	}

	@Override
	public boolean exitEQUAL(RelationalPredicate pred) {
		exitEquality(pred, false);
		return true;
	}
	
	private void enterLogicalOperator() {
		pushNewList(new BoundIdentDecl[0]);
	}
	
	private StringBuilder prefix = new StringBuilder("");
	private void debugEnter(Predicate pred) {
		debug(prefix+"Entering "+pred);
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
			debug(prefix+"New "+debug+" with "+key+", becomes: "+desc.toString());
		}
		desc.addResult(res);
		debug(prefix+"Adding terms to "+desc+": "+res);
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
		List<ISignedFormula> literals = res.getLiterals();
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
		SignedFormula lit = new SignedFormula(sig,res.isPositive());
		
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
	public boolean continueLAND(AssociativePredicate pred) {
//		isPositive = false;
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
		// take the top of the stack
		// a literal signature
		// wrap it in a quantifiedpredicate wrapper, which transforms it
		// into 
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
		
		context.decrementQuantifierOffset(pred.getBoundIdentDecls().length);
		
		NormalizedFormula res = result.pop();
		ISignedFormula quantified = res.getLiterals().get(0);
		
		List<TermSignature> quantifiedTerms = new ArrayList<TermSignature>();		
		List<TermSignature> unquantifiedTerms = getUnquantifiedTerms(res.getTerms(), quantifiedTerms, res.getStartOffset(), res.getEndOffset());
		
		IntermediateResult interRes = new IntermediateResult(quantifiedTerms,new TermOrderer());
		SymbolKey<QuantifiedDescriptor> key = new QuantifiedLiteralKey(quantified,unquantifiedTerms,isForall);
		
		QuantifiedDescriptor desc = updateDescriptor(key, context.getQuantifiedTable(), interRes, "quantified");
		QuantifiedLiteral sig = new QuantifiedLiteral(isForall,quantified,unquantifiedTerms,interRes.getTerms(),desc,res.getStartOffset(),res.getEndOffset());
		
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
