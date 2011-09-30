package org.eventb.core.pm;

import org.eventb.core.ast.Predicate;

/**
 * <p>Common protocol for a predicate matcher.</p>
 * 
 * <p> A predicate matcher matches a formula and a pattern and if successful augments a binding
 * with suitable mappings.
 * 
 * <p> This interface is not intended to be implemented by clients.
 * @author maamria
 */
public interface IPredicateMatcher{

	/**
	 * <p>Augments the given <code>existingBinding</code> with new binding as it traverses <code>pattern</code> and <code>form</code>.</p>
	 * @param form the original formula
	 * @param pattern
	 * @param existingBinding
	 * @return whether the matching succeeded
	 */
	public  boolean match(Predicate form, Predicate pattern, IBinding existingBinding);
	
}
