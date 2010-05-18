/*******************************************************************************
 * Copyright (c) 2005, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
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
 * @noextend This class is not intended to be subclassed by clients.
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
