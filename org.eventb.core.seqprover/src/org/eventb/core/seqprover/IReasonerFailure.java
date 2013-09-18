/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover;

/**
 * Common interface for reasoner outputs when the reasoner fails.
 * 
 * <p>
 * Objects of this type should be generated using the factory methods provided.
 * </p>
 * 
 * @see ProverFactory
 * @author Farhad Mehta
 * @since 1.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IReasonerFailure extends IReasonerOutput{
	
	/**
	 * Returns the reason for reasoner failure as a <code>String</code>.
	 * 
	 * @return
	 * 		The reason for reasoner failure.
	 */
	String getReason();

}
