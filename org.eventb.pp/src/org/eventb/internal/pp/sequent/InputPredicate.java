/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added sequent normalization
 *******************************************************************************/
package org.eventb.internal.pp.sequent;

import java.util.Map;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.internal.pp.loader.predicate.AbstractContext;
import org.eventb.pp.PPCore;
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

	private final boolean isGoal;
	private final Predicate originalPredicate;
	private Predicate normalizedPredicate;
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
		if (normalizedPredicate == null) {
			throw new IllegalStateException(
					"Substitution should be invoked first");
		}
		try {
			translatedPredicate = translate(normalizedPredicate);
		} catch (UnsupportedOperationException e) {
			if (isGoal) {
				translatedPredicate = ff.makeLiteralPredicate(Formula.BFALSE, null); 
			} else {
				translatedPredicate = ff.makeLiteralPredicate(Formula.BTRUE, null);
			}
		}
	}

	public void normalize(Map<FreeIdentifier, Expression> subst) {
		normalizedPredicate = originalPredicate.substituteFreeIdents(subst, ff);
	}
	
	public Predicate translatedPredicate() {
		return translatedPredicate;
	}
	
	public Predicate normalizedPredicate() {
		return normalizedPredicate;
	}

	public Predicate originalPredicate() {
		return originalPredicate;
	}
	
	public boolean isGoal() {
		return isGoal;
	}
	
	public FreeIdentifier[] freeIdentifiers() {
		return originalPredicate.getFreeIdentifiers();
	}

}