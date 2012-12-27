/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.xprover;

/**
 * Common protocol for cancellable tasks.
 * 
 * @author Laurent Voisin
 * @since 1.0
 */
public interface Cancellable {

	/**
	 * Tells whether this task has been cancelled.
	 * 
	 * @return <code>true</code> iff this task has been cancelled.
	 */
	boolean isCancelled();
	
}
