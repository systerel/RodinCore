package org.eventb.core.pm;

import org.eventb.core.ast.Expression;


/**
 * <p>Common protocol for an expression matcher.</p>
 * 
 * <p> An expression matcher matches a formula and a pattern and if successful augments a binding
 * with suitable mappings.
 * 
 * <p> This interface is not intended to be implemented by clients.
 * 
 * @author maamria
 */
public interface IExpressionMatcher{
	
	/**
	 * <p>Augments the given <code>existingBinding</code> with new binding as it traverses <code>pattern</code> and <code>form</code>.</p>
	 * <p> Callers should ensure that at runtime <code>form</code> and <code>pattern</code> are of the same class.<p>
	 * @param form the original formula
	 * @param pattern
	 * @param existingBinding
	 * @return whether the matching succeeded
	 */
	public  boolean match(Expression form, Expression pattern, IBinding existingBinding);

}
