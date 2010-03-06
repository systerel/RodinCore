/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.pp.sequent;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.internal.pp.PPCore;
import org.eventb.internal.pp.loader.predicate.AbstractContext;
import org.eventb.pp.PPProof;
import org.eventb.pptrans.Translator;

public class InputPredicate {

	private static final FormulaFactory ff = FormulaFactory.getDefault();

	private static Predicate translate(Predicate predicate) {
		assert predicate.isTypeChecked();

		Predicate newPredicate;
		newPredicate = Translator.decomposeIdentifiers(predicate, ff);
		newPredicate = Translator
				.reduceToPredicateCalulus(newPredicate, ff);
		newPredicate = Translator.simplifyPredicate(newPredicate, ff);
		newPredicate = newPredicate.flatten(ff);

		if (newPredicate.isTypeChecked()) {
			if (PPProof.DEBUG) {
				PPProof.debug("Translated: " + predicate + " to: " + newPredicate);
			}
			return newPredicate;
		}
		if (PPProof.DEBUG) {
			PPProof.debug("Translator generated untype-checked predicate: "
					+ newPredicate);
		}
		PPCore.log("Translator generetad untyped predicate "
						+ newPredicate);
		return null;
	}

	final boolean isGoal;
	final Predicate originalPredicate;
	private Predicate translatedPredicate;

	public InputPredicate(Predicate originalPredicate, boolean isGoal) {
		this.originalPredicate = originalPredicate;
		this.isGoal = isGoal;
	}

	public boolean loadPhaseOne(AbstractContext loadContext) {
		if (translatedPredicate == null) {
			throw new IllegalStateException(
					"Translator should be invoked first");
		}
		assert translatedPredicate.isTypeChecked();

		if (translatedPredicate.getTag() == Predicate.BTRUE) {
			return isGoal;
		}
		if (translatedPredicate.getTag() == Predicate.BFALSE) {
			return !isGoal;
		}
		loadContext.load(translatedPredicate, originalPredicate, isGoal);
		return false;
	}

	public void translate() {
		translatedPredicate = translate(originalPredicate);
	}

	public Predicate getTranslatedPredicate() {
		return translatedPredicate;
	}
}