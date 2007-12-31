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

import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
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
 * <p>
 * This class is package protected. Instances should be created only by a
 * context.
 * </p>
 * 
 * @see IContext
 * 
 * @author François Terrier
 */
class PredicateLoader {

	/**
	 * Debug flag for <code>LOADER_PHASE1_TRACE</code>
	 */
	public static boolean DEBUG = false;
	
	private static final LiteralOrderer literalOrderer = new LiteralOrderer();

	// these are persistent variables that are completed at each
	// iteration
	private final AbstractContext context;
	
	// The predicate to load
	private final Predicate predicate;
	
	// Origin of the predicate currently being built.
	private final IOrigin origin;
	
	private final boolean isGoal;
	
	private final TermBuilder termBuilder;
	
	// Result of the loading
	private NormalizedFormula formula;

	/**
	 * Creates a loader instance for the given predicate in the given context.
	 * Just creating this instance doesn't actually load the predicate and the
	 * context is not modified. The predicate will be loaded (and the context
	 * updated) only when the {@link load()} method is called.
	 * <p>
	 * If <code>isGoal</code> is <code>true</code>, the predicate will be
	 * loaded as a goal (negated).
	 * </p>
	 * 
	 * @param context
	 *            the context in which the loading will be performed
	 * @param predicate
	 *            the predicate to load
	 * @param originalPredicate
	 *            the original predicate (for origin tracking)
	 * @param isGoal
	 *            <code>true</code> iff the predicate should be loaded as a
	 *            goal
	 */
	public PredicateLoader(AbstractContext context, Predicate predicate,
			Predicate originalPredicate, boolean isGoal) {

		assert predicate.isTypeChecked() : "Untyped predicate";

		// TODO remove this static call
//		ArithmeticKey.resetCounter();
		this.context = context;
		this.predicate = predicate;
		this.origin = new PredicateOrigin(originalPredicate, isGoal);
		this.isGoal = isGoal;
		this.termBuilder = new TermBuilder(context);
	}
	
	/**
	 * Creates a loader instance for the given predicate in the given context.
	 * Just creating this instance doesn't actually load the predicate and the
	 * context is not modified. The predicate will be loaded (and the context
	 * updated) only when the {@link load()} method is called.
	 * <p>
	 * If <code>isGoal</code> is <code>true</code>, the predicate will be
	 * loaded as a goal (negated).
	 * </p>
	 * 
	 * @param context
	 *            the context in which the loading will be performed
	 * @param predicate
	 *            the predicate to load
	 * @param isGoal
	 *            <code>true</code> iff the predicate should be loaded as a
	 *            goal
	 */
	public PredicateLoader(AbstractContext context, Predicate predicate,
			boolean isGoal) {
		this(context, predicate, predicate, isGoal);
	}
	
	/**
	 * Actually loads the predicate in the context (these are arguments to the
	 * constructor). The context gets updated with all information gathered
	 * during the loading process.
	 */
	public void load() {
		if (formula != null) {
			throw new IllegalStateException("Predicate already loaded.");
		}
		
		if (DEBUG) {
			debug("========================================");
			debug("Loading " + (isGoal ? "goal" : "hypothesis") + ": "
					+ predicate);
		}
	
		formula = process(predicate, !isGoal, new NormalizedFormula(literalOrderer, origin));
	}

	/**
	 * Returns the formula computed during loading.
	 * <p>
	 * Method {@link #load()} must have been called prior to this method.
	 * </p>
	 * 
	 * @return the formula resulting from the loading
	 */
	public INormalizedFormula getResult() {
		return formula;
	}

	private NormalizedFormula process(Predicate pred, boolean isPositive,
			NormalizedFormula acc) {
		try {
			debugEnter(pred);
			if (pred instanceof AssociativePredicate) {
				final AssociativePredicate apred = (AssociativePredicate) pred;
				return processAssociativePredicate(apred, isPositive, acc);
			}
			if (pred instanceof BinaryPredicate) {
				final BinaryPredicate bpred = (BinaryPredicate) pred;
				return processBinaryPredicate(bpred, isPositive, acc);
			}
			if (pred instanceof UnaryPredicate) {
				final UnaryPredicate upred = (UnaryPredicate) pred;
				return processUnaryPredicate(upred, isPositive, acc);
			}
			if (pred instanceof QuantifiedPredicate) {
				final QuantifiedPredicate qpred = (QuantifiedPredicate) pred;
				return processQuantifiedPredicate(qpred, isPositive, acc);
			}
			if (pred instanceof RelationalPredicate) {
				final RelationalPredicate rpred = (RelationalPredicate) pred;
				return processRelationalPredicate(rpred, isPositive, acc);
			}
			throw invalidPredicate(pred);
		} finally {
			debugExit();
		}
	}

	private RuntimeException invalidPredicate(Predicate pred) {
		return new IllegalArgumentException("Unexpected predicate " + pred);
	}

	private NormalizedFormula processAssociativePredicate(
			AssociativePredicate pred, boolean isPositive, NormalizedFormula acc) {
		final int tag = pred.getTag();
		final boolean negated;
		switch (tag) {
		case Predicate.LAND:
			negated = true;
			break;
		case Predicate.LOR:
			negated = false;
			break;
		default:
			throw invalidPredicate(pred);
		}

		final NormalizedFormula result = new NormalizedFormula(literalOrderer, origin);
		for (Predicate child : pred.getChildren()) {
			process(child, !negated, result);
		}
		exitLogicalOperator(tag, isPositive ^ negated, result, acc);
		return acc;
	}

	private NormalizedFormula processBinaryPredicate(BinaryPredicate pred,
			boolean isPositive,
			NormalizedFormula acc) {
		final int tag = pred.getTag();
		final boolean leftIsPositive;
		switch (tag) {
		case Predicate.LIMP:
			leftIsPositive = false;
			break;
		case Predicate.LEQV:
			leftIsPositive = true;
			break;
		default:
			throw invalidPredicate(pred);
		}

		final NormalizedFormula result = new NormalizedFormula(literalOrderer, origin);
		process(pred.getLeft(), leftIsPositive, result);
		process(pred.getRight(), true, result);
		exitLogicalOperator(tag, isPositive, result, acc);
		return acc;
	}

	private NormalizedFormula processUnaryPredicate(UnaryPredicate pred,
			boolean isPositive, NormalizedFormula acc) {
		process(pred.getChild(), !isPositive, acc);
		return acc;
	}

	private NormalizedFormula processQuantifiedPredicate(
			QuantifiedPredicate pred, boolean isPositive, NormalizedFormula acc) {

		final BoundIdentDecl[] decls = pred.getBoundIdentDecls();
		final int startOffset = termBuilder.getNumberOfDecls();
		termBuilder.pushDecls(decls);
		final int endOffset = termBuilder.getNumberOfDecls() - 1;
		final NormalizedFormula result = new NormalizedFormula(literalOrderer,
				origin);
		process(pred.getPredicate(), isPositive, result);
		switch (pred.getTag()) {
		case Predicate.EXISTS:
			isPositive = !isPositive;
			break;
		case Predicate.FORALL:
			break;
		default:
			throw invalidPredicate(pred);
		}
		exitQuantifiedPredicate(pred, isPositive, result, acc, startOffset,
				endOffset);
		return acc;
	}

	private NormalizedFormula processRelationalPredicate(
			RelationalPredicate pred, boolean isPositive, NormalizedFormula acc) {
		final Sort sort = new Sort(pred.getRight().getType());
		final boolean arithmetic = sort.equals(Sort.NATURAL);
		final List<TermSignature> terms = getChildrenTerms(pred, arithmetic);
		switch (pred.getTag()) {
		case Predicate.GE:
			exitArithmeticLiteral(terms, Type.LESS, !isPositive, acc);
			return acc;
		case Predicate.GT:
			exitArithmeticLiteral(terms, Type.LESS_EQUAL, !isPositive, acc);
			return acc;
		case Predicate.LE:
			exitArithmeticLiteral(terms, Type.LESS_EQUAL, isPositive, acc);
			return acc;
		case Predicate.LT:
			exitArithmeticLiteral(terms, Type.LESS, isPositive, acc);
			return acc;
		case Predicate.EQUAL:
			exitEquality(terms, isPositive, sort, acc);
			return acc;
		case Predicate.NOTEQUAL:
			exitEquality(terms, !isPositive, sort, acc);
			return acc;
		case Predicate.IN:
			final SymbolKey<PredicateDescriptor> key = new PredicateKey(sort);
			final IntermediateResult inRes = new IntermediateResult(terms);
			final PredicateDescriptor desc = updateDescriptor(key, context
					.getLiteralTable(), inRes, "predicate");
			final PredicateFormula lit = new PredicateFormula(terms, desc);
			final SignedFormula<?> sf = new SignedFormula<PredicateDescriptor>(
					lit, isPositive);
			acc.addResult(sf, inRes);
			return acc;
		default:
			throw invalidPredicate(pred);
		}
	}

	private List<TermSignature> getChildrenTerms(RelationalPredicate pred,
			boolean arith) {
		final List<TermSignature> terms = new ArrayList<TermSignature>();
		appendToTermList(terms, pred.getLeft(), arith);
		addToTermList(terms, pred.getRight(), arith);
		return terms;
	}

	// Adds several terms to the given list, checking their form.
	private void appendToTermList(List<TermSignature> terms, Expression expr,
			boolean arith) {
		if (expr.getTag() == Expression.MAPSTO) {
			final BinaryExpression bin = (BinaryExpression) expr;
			appendToTermList(terms, bin.getLeft(), arith);
			appendToTermList(terms, bin.getRight(), arith);
		} else {
			addToTermList(terms, expr, arith);
		}
	}

	// Adds a single term to the given list, checking its form.
	private void addToTermList(List<TermSignature> terms, Expression expr,
			boolean arith) {
		if (!arith) {
			checkTag(expr);
		}
		final TermSignature term = termBuilder.buildTerm(expr);
		terms.add(term);
	}

	private void exitArithmeticLiteral(List<TermSignature> terms, Type type,
			boolean sign, NormalizedFormula acc) {
		assert terms.size() == 2;
		// TODO normalize arithmetic and order terms
		
		List<TermSignature> simpleTerms = new ArrayList<TermSignature>();
		List<TermSignature> otherTerms = getSimpleTerms(terms, simpleTerms);
		
		IntermediateResult interRes = new IntermediateResult(simpleTerms/*, new TermOrderer()*/);
		
		ArithmeticKey key = new ArithmeticKey(otherTerms,type);
		ArithmeticDescriptor desc = updateDescriptor(key, context.getArithmeticTable(), interRes, "arithmetic");
		desc.addResult(interRes);
		ArithmeticFormula sig = new ArithmeticFormula(type,interRes.getTerms(),otherTerms,desc);
		if (DEBUG)
			debug("Adding terms to " + desc + ": " + interRes);
		
		acc.addResult(new SignedFormula<ArithmeticDescriptor>(sig, sign),interRes);
	}

	private List<TermSignature> getSimpleTerms(List<TermSignature> originalList, List<TermSignature> simpleTerms) {
		List<TermSignature> result = new ArrayList<TermSignature>();
		for (TermSignature signature : originalList) {
			result.add(signature.getSimpleTerm(simpleTerms));
		}
		return result;
	}
	
	private void checkTag(Expression expr) {
		switch (expr.getTag()) {
		case Expression.BOUND_IDENT:
		case Expression.FREE_IDENT:
		case Expression.INTLIT:
		case Expression.TRUE:
			// OK
			break;
		default:
			throw new IllegalArgumentException("Invalid term: " + expr);
		}
	}
	
	private boolean exitEquality(List<TermSignature> terms, boolean sign,
			Sort sort, NormalizedFormula acc) {
		// treat arithmetic equality as arithmetic literals
		if (sort.equals(Sort.NATURAL)) {
			exitArithmeticLiteral(terms, Type.EQUAL, sign, acc);
		} else {
			exitEqualityLiteral(terms, sort, sign, acc);
		}
		return true;
	}
	
	private void exitEqualityLiteral(List<TermSignature> terms, Sort sort,
			boolean sign, NormalizedFormula acc) {
		SymbolKey<EqualityDescriptor> key = new EqualityKey(sort);
		IntermediateResult inRes = new IntermediateResult(terms);
		EqualityDescriptor desc = updateDescriptor(key, context.getEqualityTable(), inRes, "equality");
		
		AbstractFormula<EqualityDescriptor> sig;
		if (sort.equals(Sort.BOOLEAN)) {
			sig = new BooleanEqualityFormula(terms, desc);
		}
		else {
			sig = new EqualityFormula(terms, desc);
		}
		// TODO implement an ordering on terms
		// inRes.orderList();
		// from here indexes will be ordered
		acc.addResult(new SignedFormula<EqualityDescriptor>(sig, sign), inRes);
	}

	private <T extends LiteralDescriptor> T updateDescriptor(SymbolKey<T> key, SymbolTable<T> table, IIntermediateResult res, String debug) {
		T desc = table.get(key);
		if (desc == null) {
			desc = key.newDescriptor(context);
			table.add(key, desc);
			if (DEBUG)
				debug("New " + debug + " with " + key + ", becomes: " + desc);
		}
		desc.addResult(res);
		if (DEBUG)
			debug("Adding terms to " + desc + ": " + res);
		return desc;
	}
	
	private void exitLogicalOperator(int tag, boolean isPositive,
			NormalizedFormula res, NormalizedFormula acc) {
		// let us order the list
		res.orderList();
		if (tag == Predicate.LEQV) {
			// we order the list and put the negation in front before
			// the key is created, ensuring a correct factorization
			res.reduceNegations();
		}
		final List<SignedFormula<?>> literals = res.getLiterals();
		final IIntermediateResult iRes = res.getNewIntermediateResult();
		
		final AbstractClause<?> sig;
		if (tag == Predicate.LEQV) {
			final SymbolKey<EquivalenceClauseDescriptor> key = new EquivalenceClauseKey(literals);
			final EquivalenceClauseDescriptor desc = updateDescriptor(key, context.getEqClauseTable(), iRes, "equivalence clause");
			sig = new EquivalenceClause(literals,iRes.getTerms(),desc);
		} else {
			final SymbolKey<DisjunctiveClauseDescriptor> key = new DisjunctiveClauseKey(literals);
			final DisjunctiveClauseDescriptor desc = updateDescriptor(key, context.getDisjClauseTable(), iRes, "disjunctive clause");
			sig = new DisjunctiveClause(literals,iRes.getTerms(),desc);
		}

		// we create the new signature
		@SuppressWarnings("unchecked")
		final SignedFormula<?> lit = new SignedFormula(sig, isPositive);
		
		// we append the new literal to the result before
		acc.addResult(lit, iRes);
	}
	
	private void exitQuantifiedPredicate(QuantifiedPredicate pred,
			boolean isForall, NormalizedFormula res, NormalizedFormula acc,
			int startOffset, int endOffset) {
		termBuilder.popDecls(pred.getBoundIdentDecls());
		SignedFormula<?> quantified = res.getSignature();

		List<TermSignature> quantifiedTerms = new ArrayList<TermSignature>();
		List<TermSignature> unquantifiedTerms = getUnquantifiedTerms(res
				.getTerms(), quantifiedTerms, startOffset, endOffset);

		IntermediateResult interRes = new IntermediateResult(quantifiedTerms);
		SymbolKey<QuantifiedDescriptor> key = new QuantifiedLiteralKey(
				quantified, unquantifiedTerms, isForall);

		QuantifiedDescriptor desc = updateDescriptor(key, context
				.getQuantifiedTable(), interRes, "quantified");
		QuantifiedFormula sig = new QuantifiedFormula(isForall, quantified,
				unquantifiedTerms, interRes.getTerms(), desc, startOffset,
				endOffset);

		acc.addResult(new SignedFormula<QuantifiedDescriptor>(sig, true),
				interRes);
	}
	
	private List<TermSignature> getUnquantifiedTerms(List<TermSignature> terms, List<TermSignature> quantifiedTerms, int startOffset, int endOffset) {
		List<TermSignature> unquantifiedSignature = new ArrayList<TermSignature>();
		for (TermSignature term : terms) {
			unquantifiedSignature.add(term.getUnquantifiedTerm(startOffset, endOffset, quantifiedTerms));
		}
		return unquantifiedSignature;
	}

	//-----------------------------
	//  Debugging support methods
	//-----------------------------

	private StringBuilder indentationPrefix = new StringBuilder();

	private void debug(String message) {
		System.out.println(indentationPrefix + message);
	}
	
	private void debugEnter(Predicate pred) {
		if (DEBUG) {
			debug("Entering " + pred);
			indentationPrefix.append("  ");
		}
	}

	private void debugExit() {
		if (DEBUG) {
			indentationPrefix.setLength(indentationPrefix.length() - 2);
		}
	}
	
}
