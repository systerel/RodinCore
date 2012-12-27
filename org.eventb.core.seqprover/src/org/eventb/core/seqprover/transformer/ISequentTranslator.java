/*******************************************************************************
 * Copyright (c) 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.transformer;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;

/**
 * Common protocol for translating simple sequents. This interface is an
 * extension of {@link ISequentTransformer} that allows the translator to define
 * the formula factory of the translated sequent, as well as to add some axioms
 * to it.
 * 
 * @since 2.6
 * @see ISimpleSequent#apply(ISequentTransformer)
 */
public interface ISequentTranslator extends ISequentTransformer {

	/**
	 * Returns the language used in the sequent after is has been translated.
	 * 
	 * @return the formula factory of the translated sequent
	 */
	FormulaFactory getTargetFormulaFactory();

	/**
	 * Returns the axioms that, when added to the transformed sequent, make it
	 * equivalent to the original sequent.
	 * <p>
	 * The axioms are put before all hypotheses of the resulting sequent. This
	 * method is called after all predicates have been transformed.
	 * </p>
	 * 
	 * @return an array of axioms
	 */
	Predicate[] getAxioms();

}
