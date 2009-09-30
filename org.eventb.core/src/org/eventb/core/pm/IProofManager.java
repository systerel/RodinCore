/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.pm;

import org.eventb.core.IEventBRoot;

/**
 * The proof manager is the basis for manipulating event-B proofs. There is only
 * one manager per running Eclipse platform.
 * <p>
 * The proof manager provides clients with means for manipulating proofs in a
 * safe and consistent way. For each event-B component, the manager provides a
 * unique facade ({@link IProofComponent} to the three files which contain
 * proof related data, and allows to modify them in a consistent way (so that
 * proof statuses reported to the user always reflect the actual contents of the
 * PO and proof files).
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Laurent Voisin
 * @since 1.0
 */
public interface IProofManager {

	/**
	 * Returns an array of all proof attempts that have been created for some
	 * proof component and not disposed so far.
	 * 
	 * @return an array of all live proof attempts
	 */
	IProofAttempt[] getProofAttempts();

	/**
	 * Returns the proof component corresponding to the given event-B file.
	 * 
	 * @param file
	 *            an event-B file
	 * @return a proof component for the given file
	 */
	IProofComponent getProofComponent(IEventBRoot file);

}
