/*
 * Created on 11-may-2005
 *
 */
package org.eventb.core.ast;

import static org.eventb.core.ast.QuantifiedHelper.*;

import java.util.BitSet;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.internal.core.ast.FormulaReplacement;
import org.eventb.internal.core.ast.LegibilityResult;
import org.eventb.internal.core.ast.Replacement;
import org.eventb.internal.core.ast.UnbindReplacement;
import org.eventb.internal.core.typecheck.TypeCheckResult;
import org.eventb.internal.core.typecheck.TypeUnifier;

/**
 * QuantifiedPredicate is the class for all quantified predicates in an event-B
 * formula.
 * <p>
 * It can accept tags {FORALL, EXISTS}. The list of quantifiers is inherited
 * from QuantifiedFormula.
 * </p>
 * 
 * @author François Terrier
 */
public class QuantifiedPredicate extends Predicate {
	
	// child
	private final BoundIdentDecl[] quantifiedIdentifiers;
	private final Predicate pred;
	
	// offset in the corresponding tag interval
	protected final static int firstTag = FIRST_QUANTIFIED_PREDICATE;
	protected final static String[] tags = {
		"\u2200", // FORALL
		"\u2203"  // EXISTS
	};
	// For testing purposes
	public static final int TAGS_LENGTH = tags.length;

	protected QuantifiedPredicate(Predicate pred, BoundIdentDecl[] boundIdentifiers, int tag,
			SourceLocation location) {
		super(tag, location, combineHashCodes(boundIdentifiers.length, pred.hashCode()));

		this.quantifiedIdentifiers = new BoundIdentDecl[boundIdentifiers.length];
		System.arraycopy(boundIdentifiers, 0, this.quantifiedIdentifiers, 0, boundIdentifiers.length);
		this.pred = pred;
		
		checkPreconditions();
	}

	protected QuantifiedPredicate(Predicate pred, List<BoundIdentDecl> boundIdentifiers, int tag,
			SourceLocation location) {
		super(tag, location, combineHashCodes(boundIdentifiers.size(), pred.hashCode()));
		
		BoundIdentDecl[] model = new BoundIdentDecl[boundIdentifiers.size()];
		this.quantifiedIdentifiers = boundIdentifiers.toArray(model);
		this.pred = pred;
		
		checkPreconditions();
	}

	private void checkPreconditions() {
		assert getTag() >= firstTag && getTag() < firstTag+tags.length;
		assert quantifiedIdentifiers != null;
		assert pred != null;
	}

	// indicates when the toString method should put parentheses
	private final static BitSet parenthesesMap = new BitSet();
	static {
		parenthesesMap.set(Formula.NOT);
		parenthesesMap.set(Formula.LIMP);
		parenthesesMap.set(Formula.LEQV);
		parenthesesMap.set(Formula.LAND);
		parenthesesMap.set(Formula.LOR);
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
	 * Returns the predicate which is quantified here.
	 * 
	 * @return the child predicate
	 */
	public Predicate getPredicate() {
		return pred;
	}
	
	@Override
	protected String toString(boolean isRightChild, int parentTag, String[] boundNames) {
		String[] localNames = resolveIdentsPred(boundNames);
		String[] newBoundNames = catenateBoundIdentLists(boundNames, localNames);

		// put parentheses when parent is : Formula.NOT
		StringBuffer str = new StringBuffer();
		str.append(tags[getTag()-firstTag]+getBoundIdentifiersString(localNames));
		str.append("\u22c5"+pred.toString(false, getTag(), newBoundNames));
		if (parenthesesMap.get(parentTag)) {
			return "("+str.toString()+")";
		}		
		return str.toString();
	}

	@Override
	protected String toStringFullyParenthesized(String[] boundNames) {
		// create new array with the new bound identifiers, to pass as parameter
		// to next toStringFullyParenthesized method
		// put the bound identifiers of the current quantified predicate object
		// first so that they are first on the array
		String[] localNames = resolveIdentsPred(boundNames);
		String[] newBoundNames = catenateBoundIdentLists(boundNames, localNames);
		
		// creating output string
		StringBuffer str = new StringBuffer();
		str.append(tags[getTag()-firstTag]+getBoundIdentifiersString(localNames));
		str.append("\u22c5"+"("+pred.toStringFullyParenthesized(newBoundNames)+")");
		return str.toString();
	}

	private String[] resolveIdentsPred(String[] boundNames) {
		HashSet<String> usedNames = new HashSet<String>();
		pred.collectNamesAbove(usedNames, boundNames, quantifiedIdentifiers.length);
		return resolveIdents(quantifiedIdentifiers, usedNames);
	}
	
	@Override
	protected String getSyntaxTree(String[] boundNames, String tabs) {
		String[] boundNamesBelow = catenateBoundIdentLists(boundNames, quantifiedIdentifiers);
		return tabs
				+ this.getClass().getSimpleName()
				+ " ["
				+ tags[getTag() - firstTag]
				+ "]\n"
				+ getSyntaxTreeQuantifiers(boundNamesBelow,tabs + "\t",quantifiedIdentifiers)
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
	}
	
	@Override
	protected boolean equals(Formula other, boolean withAlphaConversion) {
		QuantifiedPredicate temp = (QuantifiedPredicate) other;
		return areEqualQuantifiers(quantifiedIdentifiers,
				temp.quantifiedIdentifiers, withAlphaConversion)
				&& pred.equals(temp.pred, withAlphaConversion);
	}

	@Override
	public Predicate flatten(FormulaFactory factory) {
		Predicate newPred = pred.flatten(factory);
		if (newPred.getTag() == getTag()) {
			QuantifiedPredicate quantChild = (QuantifiedPredicate) newPred;
			BoundIdentDecl[] idents = catenateBoundIdentLists(quantifiedIdentifiers, quantChild.quantifiedIdentifiers);
			return factory.makeQuantifiedPredicate(getTag(), idents, quantChild.pred, getSourceLocation());
		}
		return factory.makeQuantifiedPredicate(getTag(),quantifiedIdentifiers,newPred,getSourceLocation());
	}

	@Override
	protected void typeCheck(TypeCheckResult result, BoundIdentDecl[] quantifiedIdents) {
		for (BoundIdentDecl ident : quantifiedIdentifiers) {
			ident.typeCheck(result, quantifiedIdentifiers);
		}
		BoundIdentDecl[] boundBelow = catenateBoundIdentLists(quantifiedIdents, quantifiedIdentifiers);
		pred.typeCheck(result, boundBelow);
	}
	
	@Override
	protected boolean solveType(TypeUnifier unifier) {
		boolean success = true;
		for (BoundIdentDecl ident: quantifiedIdentifiers) {
			success &= ident.solveType(unifier);
		}
		success &= pred.solveType(unifier);

		return finalizeTypeCheck(success);
	}

	@Override
	protected void collectFreeIdentifiers(LinkedHashSet<FreeIdentifier> freeIdents) {
		pred.collectFreeIdentifiers(freeIdents);
	}

	@Override
	protected void collectNamesAbove(Set<String> names, String[] boundNames, int offset) {
		final String[] newBoundNames = catenateBoundIdentLists(boundNames, quantifiedIdentifiers);
		final int newOffset = offset + quantifiedIdentifiers.length;
		pred.collectNamesAbove(names, newBoundNames, newOffset);
	}

	@Override
	protected Predicate bindTheseIdents(Map<String, Integer> binding, int offset, FormulaFactory factory) {
		final int newOffset = offset + quantifiedIdentifiers.length; 
		Predicate newPred = pred.bindTheseIdents(binding, newOffset, factory);
		if (newPred == pred) {
			return this;
		}
		return factory.makeQuantifiedPredicate(getTag(), quantifiedIdentifiers, newPred, getSourceLocation());
	}

	@Override
	public boolean accept(IVisitor visitor) {
		boolean goOn = true;

		switch (getTag()) {
		case FORALL: goOn = visitor.enterFORALL(this); break;
		case EXISTS: goOn = visitor.enterEXISTS(this); break;
		default:     assert false;
		}

		for (int i = 0; goOn && i < quantifiedIdentifiers.length; i++) {
			goOn = quantifiedIdentifiers[i].accept(visitor);
		}
		if (goOn) goOn = pred.accept(visitor);
		
		switch (getTag()) {
		case FORALL: return visitor.exitFORALL(this);
		case EXISTS: return visitor.exitEXISTS(this);
		default:     return true;
		}
	}

	@Override
	public Predicate getWDPredicateRaw(FormulaFactory formulaFactory) {
		return getWDSimplifyQ(formulaFactory, FORALL, quantifiedIdentifiers, pred.getWDPredicateRaw(formulaFactory));
	}

	@Override
	protected boolean isWellFormed(int noOfBoundVars) {
		int newNoOfBoundVars = noOfBoundVars + quantifiedIdentifiers.length;
		return pred.isWellFormed(newNoOfBoundVars);
	}

	@Override
	protected Predicate substituteAll(int noOfBoundVars, Replacement replacement, FormulaFactory formulaFactory) {
		if(replacement.getClass() == FormulaReplacement.class && ((FormulaReplacement) replacement).getPredicate() == this) {
			Map<Integer, Expression> map = ((FormulaReplacement) replacement).getMap();
			if(map.size() == 0)
				return this;
			UnbindReplacement unbindReplacement = new UnbindReplacement(map, quantifiedIdentifiers.length);
			Predicate newPred = pred.substituteAll(0, unbindReplacement, formulaFactory);
			int newSize = quantifiedIdentifiers.length - map.size();
			if(newSize == 0)
				return newPred;
			else {
				BoundIdentDecl[] newQuantifiedIdentifiers = new BoundIdentDecl[newSize];
				for(int i=0; i<quantifiedIdentifiers.length; i++) {
					int j = unbindReplacement.getDisplacement(i);
					if(j >= 0)
						newQuantifiedIdentifiers[j] = quantifiedIdentifiers[i];
				}
				return formulaFactory.makeQuantifiedPredicate(getTag(), newQuantifiedIdentifiers, newPred, getSourceLocation());
			}
		} else {
			Predicate newPred = pred.substituteAll(noOfBoundVars + quantifiedIdentifiers.length, replacement, formulaFactory);
			if(newPred == pred)
				return this;
			else
				return formulaFactory.makeQuantifiedPredicate(getTag(), quantifiedIdentifiers, newPred, getSourceLocation());
		}
	}

}
