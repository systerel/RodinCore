/*******************************************************************************
 * Copyright (c) 2008, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.core.indexer;

import org.rodinp.core.RodinCore;

/**
 * Common protocol for kinds of occurrences found while indexing files.
 * <p>
 * Clients should contribute their own kinds through the extension point
 * <code>org.rodinp.core.occurrenceKinds</code>.
 * </p>
 * <p>
 * Occurrence kinds are guaranteed to be unique. Hence, two occurrence kinds can
 * be compared directly using identity (<code>==</code>). Instances can be
 * obtained using the static factory method
 * {@link RodinCore#getOccurrenceKind(String)}.
 * </p>
 * 
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 * 
 * @author Nicolas Beauger
 * @since 1.0
 */
public interface IOccurrenceKind {

	/**
	 * Returns the unique identifier of this occurrence kind.
	 * 
	 * @return the identifier of this occurrence kind.
	 */
	String getId();

	/**
	 * Returns the human-readable name of this occurrence kind.
	 * 
	 * @return the name of this occurrence kind.
	 */
	String getName();

}
