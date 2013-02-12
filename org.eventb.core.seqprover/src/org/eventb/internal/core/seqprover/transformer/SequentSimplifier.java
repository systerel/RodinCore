/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.transformer;

import static org.eventb.core.seqprover.transformer.PredicateTransformers.makeSimplifier;

import org.eventb.core.ast.IFormulaRewriter2;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.transformer.ISequentTransformer;
import org.eventb.core.seqprover.transformer.ITrackedPredicate;
import org.eventb.core.seqprover.transformer.SimpleSequents.SimplificationOption;

/**
 * Transformer that simplifies a sequent by applying logical simplification
 * rules,
 * 
 * @author Laurent Voisin
 */
public class SequentSimplifier implements ISequentTransformer {

	private final IFormulaRewriter2 simplifier;

	public SequentSimplifier(SimplificationOption... options) {
		simplifier = makeSimplifier(options);
	}

	@Override
	public Predicate transform(ITrackedPredicate predicate) {
		return predicate.getPredicate().rewrite(simplifier);
	}

}
