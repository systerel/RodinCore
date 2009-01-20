/*******************************************************************************
 * Copyright (c) 2008, 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.core.indexer;

/**
 * Common protocol for kinds of occurrences found while indexing files.
 * <p>
 * Clients should add their own kinds through the extension point mechanism.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Nicolas Beauger
 */
public interface IOccurrenceKind {

	/**
	 * Returns the identifier of the kind.
	 * 
	 * @return the identifier of the kind.
	 */
	String getId();

	/**
	 * Returns the name of the kind.
	 * 
	 * @return the name of the kind.
	 */
	String getName();

}
