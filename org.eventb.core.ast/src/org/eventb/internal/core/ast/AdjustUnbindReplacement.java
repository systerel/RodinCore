/**
 * 
 */
package org.eventb.internal.core.ast;


/**
  * Auxiliary class to adjust indices of bound variables in a bound variable substitutions.
 * <p>
 * When inserting a subtree into an AST bound indices in that subtree change.
 * For bound variable substitutions this is slightly more complicated than for 
 * free variable substitutions, because some bound variables disappear and the the
 * indices in the inserted subtree must be adjusted relative to the disappearing
 * variables. 
 * 
 * @author halstefa
 *
 */
public class AdjustUnbindReplacement extends UnbindReplacement {

	private final int offset;
	
	public AdjustUnbindReplacement(int offset, UnbindReplacement replacement) {
		super(replacement);
		this.offset = offset;
	}
	
	public int getOffset() {
		return offset;
	}
	
}
