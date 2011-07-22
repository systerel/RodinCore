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
package org.eventb.core.seqprover;

/**
 * Common protocol for reasoners that are able to repair their broken input.
 * 
 * @author Nicolas Beauger
 * @since 2.2 (.2)
 * 
 */
public interface IRepairableInputReasoner extends IReasoner {

	/**
	 * Tries to repair an input after a {@link SerializeException} has occurred.
	 * 
	 * @param reader
	 *            an input reader
	 * @return the repaired input, or <code>null</code> in case of failure
	 */
	IReasonerInput repair(IReasonerInputReader reader);

}
