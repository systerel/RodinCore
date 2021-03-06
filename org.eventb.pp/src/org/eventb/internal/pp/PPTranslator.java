/*******************************************************************************
 * Copyright (c) 2011, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.pp;

import static org.eventb.core.seqprover.transformer.SimpleSequents.simplify;
import static org.eventb.pptrans.Translator.decomposeIdentifiers;
import static org.eventb.pptrans.Translator.reduceToPredicateCalculus;

import org.eventb.core.seqprover.transformer.ISimpleSequent;
import org.eventb.core.seqprover.transformer.SimpleSequents;

/**
 * Translator for PP. The unique method of this class runs in turn the various
 * sequent transformers that translate from plain event-B to predicate calculus.
 * This code has been extracted here to facilitate tests.
 * 
 * @author Nicolas Beauger
 * 
 */
public class PPTranslator {

	public static ISimpleSequent translate(ISimpleSequent sequent,
			CancellationChecker cancellation) {
		sequent = SimpleSequents.filterLanguage(sequent);
		cancellation.check();
		sequent = decomposeIdentifiers(sequent);
		cancellation.check();
		sequent = reduceToPredicateCalculus(sequent);
		cancellation.check();
		sequent = simplify(sequent);

		return sequent;
	}

}
