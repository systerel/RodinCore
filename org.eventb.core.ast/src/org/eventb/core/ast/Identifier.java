/*
 * Created on 11-may-2005
 *
 */
package org.eventb.core.ast;

import java.util.Set;

import org.eventb.internal.core.ast.IntStack;
import org.eventb.internal.core.ast.Position;




/**
 * This is the base class for all identifiers in an event-B formula.
 * 
 * @author François Terrier
 *
 * @since 1.0
 */
public abstract class Identifier extends Expression {
	
	protected Identifier(int tag, SourceLocation location, int hashCode) {
		super(tag, location, hashCode);
	}

	@Override
	protected final void addGivenTypes(Set<GivenType> set) {
		// Already done at the global level, nothing to do locally
	}

	@Override
	protected final Formula<?> getChild(int index) {
		return null;
	}

	@Override
	protected final IPosition getDescendantPos(SourceLocation sloc,
			IntStack indexes) {

		return new Position(indexes);
	}

	@Override
	protected Identifier rewriteChild(int index, SingleRewriter rewriter) {
		throw new IllegalArgumentException("Position is outside the formula");
	}

}
