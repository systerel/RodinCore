/*******************************************************************************
 * Copyright (c) 2005, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added child indexes
 *******************************************************************************/
package org.eventb.core.ast;

import static org.eventb.internal.core.parser.AbstractGrammar._IDENT;
import static org.eventb.internal.core.parser.SubParsers.IDENT_SUBPARSER;

import java.util.Set;

import org.eventb.internal.core.ast.IntStack;
import org.eventb.internal.core.ast.Position;
import org.eventb.internal.core.ast.extension.IToStringMediator;
import org.eventb.internal.core.ast.extension.KindMediator;
import org.eventb.internal.core.parser.AbstractGrammar;

/**
 * This is the base class for all identifiers in an event-B formula.
 * 
 * @author François Terrier
 *
 * @since 1.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public abstract class Identifier extends Expression {
	
	/**
	 * @since 2.0
	 */
	public static void init(AbstractGrammar grammar) {
		grammar.addReservedSubParser(_IDENT, IDENT_SUBPARSER);
	}

	protected Identifier(int tag, SourceLocation location, int hashCode) {
		super(tag, location, hashCode);
	}

	@Override
	protected final void addGivenTypes(Set<GivenType> set) {
		// Already done at the global level, nothing to do locally
	}

	@Override
	public Formula<?> getChild(int index) {
		throw invalidIndex(index);
	}

	@Override
	public int getChildCount() {
		return 0;
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

	@Override
	protected final void toString(IToStringMediator mediator) {
		IDENT_SUBPARSER.toString(mediator, this);
	}

	@Override
	protected final int getKind(KindMediator mediator) {
		return _IDENT;
	}

}
