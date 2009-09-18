/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.provisional.ui.prover;

import org.eventb.core.seqprover.ITactic;

/**
 * Common protocol for providing tactic applications.
 * <p>
 * Implementors of this interface should also implement one of the
 * sub-interfaces ( {@link IPositionApplication}, {@link IPredicateApplication}
 * ), so that the way they are applied can be determined.
 * </p>
 * <p>
 * <strong>EXPERIMENTAL</strong>. This interface has been added as part of a
 * work in progress. There is no guarantee that this API will work or that it
 * will remain the same. Please do not use this API without consulting with the
 * Systerel team.
 * </p>
 * 
 * @author Nicolas Beauger
 * @since 1.1
 */
public interface ITacticApplication {

	/**
	 * Returns the tactic to be applied.
	 * 
	 * @param inputs
	 *            an array of Strings the user can type in for quantified
	 *            predicates
	 * @param globalInput
	 *            a String taken from the input area in the Proof Control View
	 * @return a tactic to apply
	 */
	ITactic getTactic(String[] inputs, String globalInput);

	/**
	 * Returns the ID of this tactic.
	 * <p>
	 * Must be the same as the one provided in the 'proofTactics' extension
	 * point.
	 * </p>
	 * 
	 * @return the ID String of this tactic
	 */
	String getTacticID();
	
}
