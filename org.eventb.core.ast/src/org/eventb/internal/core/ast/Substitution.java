package org.eventb.internal.core.ast;

import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;

/**
 * Abstract super class for all kinds of substitutions operated on formulas.
 * 
 * @author Stefan Hallerstede
 */
public abstract class Substitution {

	protected final FormulaFactory ff;
	
	// Number of bound variables between current point and the root of the
	// formula to which this substitution is applied.
	protected int nbOfInternallyBound = 0;

	public Substitution(FormulaFactory ff) {
		this.ff = ff;
	}

	public FormulaFactory getFactory() {
		return this.ff;
	}

	/**
	 * Returns the expression to substitute for the given identifier.
	 * 
	 * @param ident
	 *            the identifier to substitute
	 * @return the replacement for this identifier.
	 */
	public abstract Expression getReplacement(FreeIdentifier ident);

	/**
	 * Returns the expression to substitute for the given identifier.
	 * 
	 * @param ident
	 *            the identifier to substitute
	 * @return the replacement for this identifier.
	 */
	public abstract Expression getReplacement(BoundIdentifier ident);

	/**
	 * Enter a locally quantified formula.
	 * 
	 * @param nbOfBoundIdentDecls
	 *      number of identifiers bound by the entered formula
	 */
	public void enter(int nbOfBoundIdentDecls) {
		nbOfInternallyBound += nbOfBoundIdentDecls;
	}
	
	/**
	 * Exit a locally quantified formula.
	 * 
	 * @param nbOfBoundIdentDecls
	 *      number of identifiers bound by the exited formula
	 */
	public void exit(int nbOfBoundIdentDecls) {
		nbOfInternallyBound -= nbOfBoundIdentDecls;
	}
	
}
