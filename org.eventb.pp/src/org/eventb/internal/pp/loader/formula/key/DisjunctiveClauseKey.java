/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.pp.loader.formula.key;

import java.util.List;

import org.eventb.internal.pp.loader.formula.SignedFormula;
import org.eventb.internal.pp.loader.formula.descriptor.DisjunctiveClauseDescriptor;
import org.eventb.internal.pp.loader.predicate.IContext;

/**
 * Implementation of {@link ClauseKey} for disjunctive clauses.
 *
 * @author François Terrier
 *
 */
public class DisjunctiveClauseKey extends ClauseKey<DisjunctiveClauseDescriptor> {

	public DisjunctiveClauseKey(List<SignedFormula<?>> signatures) {
		super(signatures);
	}

	@Override
	public DisjunctiveClauseDescriptor newDescriptor(IContext context) {
		return new DisjunctiveClauseDescriptor(context, context.getNextLiteralIdentifier());
	}

}
