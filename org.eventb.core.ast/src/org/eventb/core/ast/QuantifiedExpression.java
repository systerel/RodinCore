/*
 * Created on 11-may-2005
 *
 */
package org.eventb.core.ast;

import static org.eventb.core.ast.QuantifiedHelper.areEqualQuantifiers;
import static org.eventb.core.ast.QuantifiedHelper.catenateBoundIdentLists;
import static org.eventb.core.ast.QuantifiedHelper.getBoundIdentifiersString;
import static org.eventb.core.ast.QuantifiedHelper.getSyntaxTreeQuantifiers;
import static org.eventb.core.ast.QuantifiedHelper.resolveIdents;

import java.util.BitSet;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.internal.core.ast.LegibilityResult;
import org.eventb.internal.core.ast.Replacement;
import org.eventb.internal.core.typecheck.TypeCheckResult;
import org.eventb.internal.core.typecheck.TypeUnifier;
import org.eventb.internal.core.typecheck.TypeVariable;

/**
 * QuantifiedExpression is the class for all quantified expressions in an
 * event-B formula.
 * <p>
 * It can accept tags {QUNION, QINTER, CSET}. The list of quantifiers is
 * inherited from QuantifiedFormula.
 * </p>
 * 
 * @author François Terrier
 */
public class QuantifiedExpression extends Expression {
	
	// children + form
	private final BoundIdentDecl[] quantifiedIdentifiers;
	private final Expression expr;
	private final Predicate pred;
	private final Form form;
	
	/**
	 * Enumerations of the possible forms that a quantified expression can take.
	 * <p>
	 * There are several equivalent notations for quantified expressions. This
	 * enumerates all the possible forms it can take.
	 * </p>
	 */
	public static enum Form {
		/**
		 * Formula is a lambda abstraction.
		 */
		Lambda,
		/**
		 * Formula is in its implicit form (i.e. { E | P } where E is an
		 * expression and P is a predicate)
		 */
		Implicit,
		/**
		 * Formula is in its explicit for (i.e. { L \u22c5 P | E } where L
		 * is a list of identifier, E is an expression and P is a predicate.)
		 */
		Explicit
	}

	// offset of the tag interval in Formula
	protected final static int firstTag = FIRST_QUANTIFIED_EXPRESSION;
	protected final static String[] tags = {
		"\u22c3", // QUNION
		"\u22c2", // QINTER
		"CSET"    // CSET
	};
	// For testing purposes
	public static final int TAGS_LENGTH = tags.length;

	/**
	 * @param expr the expression in the quantified expression. Must not be <code>null</code>
	 * @param pred the predicate in the quxantified expression. Must not be <code>null</code>
	 * @param boundIdentifiers the identifiers that are bound to this specific quantified expression. Must not be <code>null</code>
	 * @param tag the associated tag
	 * @param location the location in the formula {@link org.eventb.core.ast.SourceLocation}
	 * @param form form of the quantified expression
	 */
	protected QuantifiedExpression(Expression expr, Predicate pred, BoundIdentDecl[] boundIdentifiers, int tag,
			SourceLocation location, Form form) {
		super(tag, location, combineHashCodes(boundIdentifiers.length, pred.hashCode(), expr.hashCode()));
		
		this.quantifiedIdentifiers = new BoundIdentDecl[boundIdentifiers.length];
		System.arraycopy(boundIdentifiers, 0, this.quantifiedIdentifiers, 0, boundIdentifiers.length);
		this.expr = expr;
		this.pred = pred;
		this.form = form;
	
		checkPreconditions();
	}
	
	protected QuantifiedExpression(Expression expr, Predicate pred, List<BoundIdentDecl> boundIdentifiers, int tag,
			SourceLocation location, Form form) {
		super(tag, location, combineHashCodes(boundIdentifiers.size(), pred.hashCode(), expr.hashCode()));

		BoundIdentDecl[] model = new BoundIdentDecl[boundIdentifiers.size()];
		this.quantifiedIdentifiers = boundIdentifiers.toArray(model);
		this.expr = expr;
		this.pred = pred;
		this.form = form;

		checkPreconditions();
	}
	
	// Common initialization.
	private void checkPreconditions() {
		assert getTag() >= firstTag && getTag() < firstTag+tags.length;
		assert quantifiedIdentifiers != null;
		assert 1 <= quantifiedIdentifiers.length;
		assert pred != null;
		assert expr != null;

		if (form == Form.Lambda) {
			assert getTag() == Formula.CSET;
			assert expr.getTag() == Formula.MAPSTO;
		}
	}
	
	// indicates when the toString method should put parentheses
	private static BitSet noParenthesesMap;
	private static BitSet rightNoParenthesesMap;
	private static BitSet csetImplicitNoParenthesesMap;
	
	// fills the parentheses maps
	static {
		BitSet propagate = new BitSet();
		BitSet propagateRight = new BitSet();
		
		propagate.set(Formula.STARTTAG);

		propagate.set(Formula.CSET);
		propagate.set(Formula.QUNION);
		propagate.set(Formula.QINTER);
		propagate.set(Formula.SETEXT);
		propagate.set(Formula.KBOOL);
		propagate.set(Formula.KCARD);
		propagate.set(Formula.POW);
		propagate.set(Formula.POW1);
		propagate.set(Formula.KUNION);
		propagate.set(Formula.KFINITE);
		propagate.set(Formula.KINTER);
		propagate.set(Formula.KDOM);
		propagate.set(Formula.KRAN);
		propagate.set(Formula.KPRJ1);
		propagate.set(Formula.KPRJ2);
		propagateRight.set(Formula.FUNIMAGE);
		propagateRight.set(Formula.RELIMAGE);
		
		noParenthesesMap = (BitSet)propagate.clone();
		rightNoParenthesesMap = (BitSet)propagateRight.clone();
		
		propagate.set(Formula.EQUAL);
		propagate.set(Formula.NOTEQUAL);
		propagate.set(Formula.IN);
		propagate.set(Formula.NOTIN);
		propagate.set(Formula.SUBSET);
		propagate.set(Formula.NOTSUBSET);
		propagate.set(Formula.SUBSETEQ);
		propagate.set(Formula.NOTSUBSETEQ);
		propagate.set(Formula.LT);
		propagate.set(Formula.LE);
		propagate.set(Formula.GT);
		propagate.set(Formula.GE);
		propagate.set(Formula.FUNIMAGE);
		propagate.set(Formula.RELIMAGE);
		propagate.set(Formula.MAPSTO);
		propagate.set(Formula.REL);
		propagate.set(Formula.TREL);
		propagate.set(Formula.SREL);
		propagate.set(Formula.STREL);
		propagate.set(Formula.PFUN);
		propagate.set(Formula.TFUN);
		propagate.set(Formula.PINJ);
		propagate.set(Formula.TINJ);
		propagate.set(Formula.PSUR);
		propagate.set(Formula.TSUR);
		propagate.set(Formula.TBIJ);
		propagate.set(Formula.BUNION);
		propagate.set(Formula.BCOMP);
		propagate.set(Formula.OVR);
		propagate.set(Formula.CPROD);
		propagate.set(Formula.PPROD);
		propagate.set(Formula.SETMINUS);
		propagate.set(Formula.CPROD);
		propagate.set(Formula.FCOMP);
		propagate.set(Formula.BINTER);
		propagate.set(Formula.DOMRES);
		propagate.set(Formula.DOMSUB);
		propagate.set(Formula.RANRES);
		propagate.set(Formula.RANSUB);
		propagate.set(Formula.UPTO);
		propagate.set(Formula.PLUS);
		propagate.set(Formula.MINUS);
		propagate.set(Formula.UNMINUS);
		propagate.set(Formula.DIV);
		propagate.set(Formula.MOD);
		propagate.set(Formula.EXPN);
		
		csetImplicitNoParenthesesMap = (BitSet)propagate.clone();
	}

	/**
	 * Returns the list of the identifiers which are bound by this formula.
	 * 
	 * @return list of bound identifiers
	 */
	public BoundIdentDecl[] getBoundIdentifiers() {
		BoundIdentDecl[] idents = new BoundIdentDecl[quantifiedIdentifiers.length];
		System.arraycopy(quantifiedIdentifiers, 0, idents, 0, quantifiedIdentifiers.length);
		return idents;
	}
	
	/**
	 * Returns the expression of this node.
	 * 
	 * @return the expression of the quantified formula
	 */
	public Expression getExpression() {
		return expr;
	}
	
	/**
	 * Returns the predicate of this node.
	 * 
	 * @return the predicate of the quantified formula
	 */
	public Predicate getPredicate() {
		return pred;
	}
	
	@Override
	protected String toStringFullyParenthesized(String[] existingBoundIdents) {
		return toStringHelper(existingBoundIdents, true);
	}
	
	@Override
	protected String toString(boolean isRightChild, int parentTag, String[] boundNames) {
		// put parentheses if parent is QuantifiedExpr...
		if (noParenthesesMap.get(parentTag) ||
				(isRightChild && rightNoParenthesesMap.get(parentTag)) ||
				(form != Form.Lambda  && getTag() == Formula.CSET && csetImplicitNoParenthesesMap.get(parentTag))) {
			return toStringHelper(boundNames, false);
		}
		else return "("+toStringHelper(boundNames, false)+")";
	}
	
	/*
	 * avoid having to write almost twice the same for methods 
	 * toString and method toStringFully parenthesized
	 */ 
	private String toStringHelper(String[] boundNames, boolean parenthesized) {
		// Collect names used in subformulas and not locally bound
		HashSet<String> usedNames = new HashSet<String>();
		expr.collectNamesAbove(usedNames, boundNames, quantifiedIdentifiers.length);
		boolean exprIsClosed = usedNames.size() == 0;
		pred.collectNamesAbove(usedNames, boundNames, quantifiedIdentifiers.length);
		
		String[] localNames = resolveIdents(quantifiedIdentifiers, usedNames);
		String[] newBoundNames = catenateBoundIdentLists(boundNames, localNames);
		
		switch (form) {
		case Lambda: 
			return toStringLambda(parenthesized, newBoundNames);
		case Implicit:
			if (exprIsClosed) {
				// Still OK to use implicit form.
				return toStringImplicit(parenthesized, localNames, newBoundNames);
			}
			return toStringExplicit(parenthesized, localNames, newBoundNames);
		case Explicit:
			return toStringExplicit(parenthesized, localNames, newBoundNames);
		default:
			assert false;
			return null;
		}
	}

	private String toStringLambda(boolean parenthesized, String[] boundNames) {
		// Extract left and right subexpressions as Strings
		assert expr.getTag() == MAPSTO;
		BinaryExpression binExpr = (BinaryExpression) this.expr;

		String leftExprString;
		String rightExprString;
		if (parenthesized) {
			leftExprString = binExpr.getLeft().toStringFullyParenthesized(boundNames);
			rightExprString = binExpr.getRight().toStringFullyParenthesized(boundNames);
		} else {
			leftExprString = binExpr.getLeft().toString(false, MAPSTO, boundNames);
			rightExprString = binExpr.getRight().toString(true, MAPSTO, boundNames);
		}

		StringBuffer str = new StringBuffer();
		str.append("\u03bb");
		str.append(leftExprString);
		str.append("\u22c5");
		str.append(getPredString(parenthesized, boundNames));
		str.append(" \u2223 ");
		str.append(rightExprString);
		return str.toString();
	}

	private String toStringImplicit(boolean parenthesized, String[] localNames, String[] boundNames) {
		StringBuffer str = new StringBuffer();
		if (getTag() == Formula.CSET) {
			str.append("{");
		}
		else {
			str.append(tags[getTag()-firstTag]);
		}
		str.append(getExprString(parenthesized, boundNames));
		str.append(" \u2223 ");
		str.append(getPredString(parenthesized, boundNames));
		if (getTag() == Formula.CSET) {
			str.append("}");
		}
		return str.toString();
	}

	private String toStringExplicit(boolean parenthesized, String[] localNames, String[] boundNames) {
		StringBuffer str = new StringBuffer();
		if (getTag() == Formula.CSET) { 
			str.append("{");
		}
		else {
			str.append(tags[getTag()-firstTag]);
		}
		str.append(getBoundIdentifiersString(localNames));
		str.append("\u22c5");
		str.append(getPredString(parenthesized, boundNames));
		str.append(" \u2223 ");
		str.append(getExprString(parenthesized, boundNames));
		if (getTag() == Formula.CSET) {
			str.append("}");
		}
		return str.toString();
	}

	private String getPredString(boolean parenthesized, String[] boundNames) {
		if (parenthesized) {
			return "(" + pred.toStringFullyParenthesized(boundNames) + ")";
		}
		return pred.toString(false, getTag(), boundNames);
	}
	
	private String getExprString(boolean parenthesized, String[] boundNames) {
		if (parenthesized) {
			return "(" + expr.toStringFullyParenthesized(boundNames) + ")";
		}
		return expr.toString(true, getTag(), boundNames);
	}
	
	@Override
	protected String getSyntaxTree(String[] boundNames, String tabs) {
		final String typeName = getType()!=null?" [type: "+getType().toString()+"]":"";
		final String[] boundNamesBelow = catenateBoundIdentLists(boundNames, quantifiedIdentifiers);
		
		return tabs
				+ this.getClass().getSimpleName()
				+ " ["
				+ tags[getTag() - firstTag] 
				+ ", " + form.toString()
				+ "]" 
				+ typeName
				+ "\n"
				+ getSyntaxTreeQuantifiers(boundNames, tabs + "\t", quantifiedIdentifiers)
				+ expr.getSyntaxTree(boundNamesBelow,tabs + "\t")
				+ pred.getSyntaxTree(boundNamesBelow,tabs + "\t");
	}
	
	@Override
	protected void isLegible(LegibilityResult result, BoundIdentDecl[] boundAbove) {
		
		for (BoundIdentDecl decl: quantifiedIdentifiers) {
			decl.isLegible(result, boundAbove);
			if (! result.isSuccess()) {
				break;
			}
		}
		
		final BoundIdentDecl[] boundBelow = catenateBoundIdentLists(boundAbove, quantifiedIdentifiers);
		if (result.isSuccess()) {
			pred.isLegible(result, boundBelow);
		}
		if (result.isSuccess()) {
			expr.isLegible(result, boundBelow);
		}
	}
	
	@Override
	protected boolean equals(Formula other, boolean withAlphaConversion) {
		QuantifiedExpression temp = (QuantifiedExpression) other;
		return hasSameType(other)
				&& areEqualQuantifiers(quantifiedIdentifiers,
						temp.quantifiedIdentifiers, withAlphaConversion)
				&& expr.equals(temp.expr, withAlphaConversion)
				&& pred.equals(temp.pred, withAlphaConversion);
	}

	@Override
	public Expression flatten(FormulaFactory factory) {
		return factory.makeQuantifiedExpression(getTag(),quantifiedIdentifiers, pred.flatten(factory), expr.flatten(factory), getSourceLocation(), form);
	}

	@Override
	protected void typeCheck(TypeCheckResult result, BoundIdentDecl[] quantifiedIdents) {
		for (BoundIdentDecl decl: quantifiedIdentifiers) {
			decl.typeCheck(result, quantifiedIdents);
		}
		
		final BoundIdentDecl[] newQuantifiers = catenateBoundIdentLists(quantifiedIdents, quantifiedIdentifiers);
		pred.typeCheck(result,newQuantifiers);
		expr.typeCheck(result,newQuantifiers);

		Type resultType;
		switch (getTag()) {
		case Formula.QUNION:
		case Formula.QINTER:
			final TypeVariable alpha = result.newFreshVariable(null);
			resultType = result.makePowerSetType(alpha);
			result.unify(expr.getType(), resultType, getSourceLocation());
			break;
		case Formula.CSET:
			resultType = result.makePowerSetType(expr.getType());
			break;
		default:
			assert false;
			resultType = null;
		}
		setType(resultType, result);
	}
	
	@Override
	protected boolean solveType(TypeUnifier unifier) {
		boolean success = true;
		for (BoundIdentDecl ident: quantifiedIdentifiers) {
			success &= ident.solveType(unifier);
		}
		success &= expr.solveType(unifier);
		success &= pred.solveType(unifier);

		return finalizeType(success, unifier);
	}

	@Override
	protected void collectFreeIdentifiers(LinkedHashSet<FreeIdentifier> freeIdents) {
		// Take care to go from left to right
		switch (form) {
		case Lambda:
		case Explicit:
			pred.collectFreeIdentifiers(freeIdents);
			expr.collectFreeIdentifiers(freeIdents);
			break;

		case Implicit:
			expr.collectFreeIdentifiers(freeIdents);
			pred.collectFreeIdentifiers(freeIdents);
			break;

		default:
			assert false;
		}
	}

	@Override
	protected void collectNamesAbove(Set<String> names, String[] boundNames, int offset) {
		final String[] newBoundNames = catenateBoundIdentLists(boundNames, quantifiedIdentifiers);
		final int newOffset = offset + quantifiedIdentifiers.length;
		pred.collectNamesAbove(names, newBoundNames, newOffset);
		expr.collectNamesAbove(names, newBoundNames, newOffset);
	}

	@Override
	protected Expression bindTheseIdents(Map<String, Integer> binding, int offset, FormulaFactory factory) {
		final int newOffset = offset + quantifiedIdentifiers.length; 
		Predicate newPred = pred.bindTheseIdents(binding, newOffset, factory);
		Expression newExpr = expr.bindTheseIdents(binding, newOffset, factory);
		if (newExpr == expr && newPred == pred) {
			return this;
		}
		return factory.makeQuantifiedExpression(getTag(), quantifiedIdentifiers, newPred, newExpr, getSourceLocation(), form);
	}

	@Override
	public boolean accept(IVisitor visitor) {
		boolean goOn = true;

		switch (getTag()) {
		case QUNION: goOn = visitor.enterQUNION(this); break;
		case QINTER: goOn = visitor.enterQINTER(this); break;
		case CSET:   goOn = visitor.enterCSET(this);   break;
		default:     assert false;
		}

		for (int i = 0; goOn && i < quantifiedIdentifiers.length; i++) {
			goOn = quantifiedIdentifiers[i].accept(visitor);
		}
		if (goOn) goOn = pred.accept(visitor);
		if (goOn) goOn = expr.accept(visitor);
		
		switch (getTag()) {
		case QUNION: return visitor.exitQUNION(this);
		case QINTER: return visitor.exitQINTER(this);
		case CSET:   return visitor.exitCSET(this);
		default:     return true;
		}
	}
	
	private Predicate getWDPredicateQINTER(FormulaFactory formulaFactory) {
		Predicate conj0 = getWDPredicateQUNION(formulaFactory);
		Predicate conj1 = getWDSimplifyQ(formulaFactory, EXISTS, quantifiedIdentifiers, pred);
		return getWDSimplifyC(formulaFactory, conj0, conj1);
	}

	private Predicate getWDPredicateQUNION(FormulaFactory formulaFactory) {
		Predicate conj0 = pred.getWDPredicateRaw(formulaFactory);
		Predicate conj1 = getWDSimplifyI(formulaFactory, pred, expr.getWDPredicateRaw(formulaFactory));
		Predicate inner = getWDSimplifyC(formulaFactory, conj0, conj1);
		return getWDSimplifyQ(formulaFactory, FORALL, quantifiedIdentifiers, inner);
	}

	@Override
	public Predicate getWDPredicateRaw(FormulaFactory formulaFactory) {
		if(getTag() == QINTER) {
			return getWDPredicateQINTER(formulaFactory);
		} else {
			return getWDPredicateQUNION(formulaFactory);
		}
	}

	@Override
	protected boolean isWellFormed(int noOfBoundVars) {
		int newNoOfBoundVars = noOfBoundVars + quantifiedIdentifiers.length;
		return pred.isWellFormed(newNoOfBoundVars) && expr.isWellFormed(newNoOfBoundVars);
	}

	@Override
	protected Expression substituteAll(int noOfBoundVars, Replacement replacement, FormulaFactory formulaFactory) {
		Expression newExpr = expr.substituteAll(noOfBoundVars + quantifiedIdentifiers.length, replacement, formulaFactory);
		Predicate newPred = pred.substituteAll(noOfBoundVars + quantifiedIdentifiers.length, replacement, formulaFactory);
		if(newPred == pred && newExpr == expr)
			return this;
		else
			return formulaFactory.makeQuantifiedExpression(getTag(), quantifiedIdentifiers, newPred, newExpr, getSourceLocation(), form);
	}

}
