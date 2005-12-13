/**
 * 
 */
package org.eventb.internal.core.ast;


/**
 * Auxiliary class to adjust indices of bound variables in free variable substitutions.
 * <p>
 * When inserting a subtree into an AST bound indices in that subtree change.
 * 
 * @author halstefa
 *
 */
public class AdjustFreeReplacement extends Replacement {
	
	private final int offset;
	
	public AdjustFreeReplacement(int offset) {
		this.offset = offset;
	}
	
	public int getOffset() {
		return offset;
	}

}
