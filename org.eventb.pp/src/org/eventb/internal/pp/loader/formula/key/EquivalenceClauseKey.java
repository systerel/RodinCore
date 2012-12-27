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
import org.eventb.internal.pp.loader.formula.descriptor.EquivalenceClauseDescriptor;
import org.eventb.internal.pp.loader.predicate.IContext;

/**
 * Implementation of {@link ClauseKey} for equivalence clauses.
 *
 * @author François Terrier
 *
 */
public class EquivalenceClauseKey extends ClauseKey<EquivalenceClauseDescriptor> {

	public EquivalenceClauseKey(List<SignedFormula<?>> signatures) {
		super(signatures);
	}

	@Override
	public EquivalenceClauseDescriptor newDescriptor(IContext context) {
		return new EquivalenceClauseDescriptor(context, context.getNextLiteralIdentifier());
	}

}
