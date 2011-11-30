/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
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
import static org.eventb.pptrans.Translator.reduceToPredicateCalulus;

import org.eventb.core.seqprover.transformer.ISimpleSequent;
import org.eventb.core.seqprover.transformer.SimpleSequents;

/**
 * Translator for PP.
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
		sequent = reduceToPredicateCalulus(sequent);
		cancellation.check();
		sequent = simplify(sequent);

		return sequent;
	}

}
