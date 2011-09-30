package org.eventb.core.pm;

import org.eventb.core.ast.Formula;

/**
 * Common protocol for a matching complement of an associative formula.
 * 
 * <p> Complements correspond to sub-formulae that were not matched in an associative formula.
 * Suppose we have a pattern (<code>a op b</code>) where <code>a, b</code> are constants and <code>op</code>
 * is an associative operator. When trying to match the pattern with the formula (<code>a op b op c</code>),
 * a partial match is obtained where <code>c</code> is left out. In this case, <code>c</code> should be
 * considered as an associative complement, and should be appended to the resultant formula.
 * 
 * <p> This interface is not intended to be implemented by clients.
 * @author maamria
 * @since 1.0
 *
 * @param <F> the class of the formula
 */
public interface IAssociativeComplement<F extends Formula<F>> {

	/**
	 * Returns the tag of the associative formula.
	 * @return the tag
	 */
	public int getTag();
	
	/**
	 * Returns the formula to append.
	 * @return the formula to append
	 */
	public F getToAppend();
	
	/**
	 * Returns the formula to prepend.
	 * @return the formula to prepend
	 */
	public F getToPrepend();
	
}
