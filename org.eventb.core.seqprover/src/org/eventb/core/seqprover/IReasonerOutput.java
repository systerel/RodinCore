/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added generatedByDesc()
 *******************************************************************************/
package org.eventb.core.seqprover;


/**
 * Common interface for the output of a reasoner.
 * 
 * <p>
 * This interface is not intended to be implemented by clients. Objects of this
 * type should be generated using the factor methods provided for their subclasses.
 * </p>
 * 
 * @see ProverFactory
 * @see IReasonerFailure
 * @see IProofRule
 * 
 * @author Farhad Mehta
 *
 * @since 1.0
 */
public interface IReasonerOutput {

	/**
	 * Returns the reasoner that was used to generate this reasoner output
	 * 
	 * @return
	 *	 	the reasoner that was used to generate this reasoner output
	 *
	 */
	IReasoner generatedBy();

	/**
	 * Returns a descriptor of the reasoner that was used to generate this
	 * reasoner output.
	 * 
	 * @return a descriptor of the reasoner that was used to generate this
	 *         reasoner output
	 */
	IReasonerDesc getReasonerDesc();

	/**
	 * Returns the reasoner input that was used to generate this reasoner output
	 * 
	 * @return
	 * 		the reasoner input that was used to generate this reasoner output
	 */
	IReasonerInput generatedUsing();

}