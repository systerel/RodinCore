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
package org.eventb.core.seqprover.transformer;

import static org.eventb.core.seqprover.eventbExtensions.DLib.mDLib;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IFormulaRewriter2;
import org.eventb.core.seqprover.eventbExtensions.DLib;
import org.eventb.core.seqprover.transformer.SimpleSequents.SimplificationOption;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.PredicateSimplifier;

/**
 * Common protocol for transforming predicates.
 * 
 * @author Laurent Voisin
 * @since 2.4
 * @noextend This class is not intended to be extended by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class PredicateTransformers {

	/**
	 * Returns a formula rewriter that can be used to simplify logically a
	 * predicate in a sound manner: The predicate obtained after rewriting is
	 * equivalent to the original predicate.
	 * <p>
	 * The rewriter performs its rewriting by applying repeatedly the following
	 * rules until reaching a fix-point:
	 * <ul>
	 * <li>SIMP_SPECIAL_AND_BTRUE</li>
	 * <li>SIMP_SPECIAL_AND_BFALSE</li>
	 * <li>SIMP_SPECIAL_OR_BTRUE</li>
	 * <li>SIMP_SPECIAL_OR_BFALSE</li>
	 * <li>SIMP_SPECIAL_IMP_BTRUE_R</li>
	 * <li>SIMP_SPECIAL_IMP_BTRUE_L</li>
	 * <li>SIMP_SPECIAL_IMP_BFALSE_R</li>
	 * <li>SIMP_SPECIAL_IMP_BFALSE_L</li>
	 * <li>SIMP_SPECIAL_NOT_BTRUE</li>
	 * <li>SIMP_SPECIAL_NOT_BFALSE</li>
	 * <li>SIMP_NOT_NOT</li>
	 * <li>SIMP_MULTI_EQV</li>
	 * <li>SIMP_SPECIAL_EQV_BTRUE</li>
	 * <li>SIMP_SPECIAL_EQV_BFALSE</li>
	 * <li>SIMP_FORALL</li>
	 * <li>SIMP_EXISTS</li>
	 * <li>SIMP_LIT_MINUS</li>
	 * </ul>
	 * Note: The last rule is present only for technical reason.
	 * </p>
	 * <p>
	 * Options can be passed to also apply some additional simplification rules.
	 * </p>
	 * 
	 * @param sequent
	 *            sequent to simplify
	 * @param options
	 *            simplification options
	 * @return a simplified sequent equivalent to the given one
	 */
	public static IFormulaRewriter2 makeSimplifier(FormulaFactory factory,
			SimplificationOption... options) {
		final DLib dLib = mDLib(factory);
		int flags = 0;
		for (SimplificationOption option : options) {
			flags |= option.flags;
		}
		return new PredicateSimplifier(dLib, flags, false, "SequentSimplifier");
	}

	private PredicateTransformers() {
		// singleton class
	}

}
