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
package org.rodinp.core.index;

import org.rodinp.core.IRodinFile;

/**
 * Common protocol for occurrences in indexed files.
 * <p>
 * An occurrence has a kind {@link IOccurrenceKind} and a location
 * {@link IInternalLocation} inside a {@link IRodinFile}.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Nicolas Beauger
 * 
 */
public interface IOccurrence {

	/**
	 * Returns the kind of the occurrence.
	 * 
	 * @return the kind of the occurrence.
	 * @see IOccurrenceKind
	 */
	IOccurrenceKind getKind();

	/**
	 * Returns the location of the occurrence.
	 * 
	 * @return the location of the occurrence.
	 * @see IInternalLocation
	 */
	IInternalLocation getLocation();

	/**
	 * Returns the file containing the location of the occurrence, if any. If
	 * the location is a Rodin file or occurs within a Rodin file, then this
	 * Rodin file is returned. Otherwise, <code>null</code> is returned.
	 * <p>
	 * Equivalent to <code>getLocation().getRodinFile()</code>.
	 * </p>
	 * 
	 * @return the file containing the location or <code>null</code>
	 * @see IInternalLocation#getRodinFile()
	 */
	IRodinFile getRodinFile();

	/**
	 * Returns the declaration of the element associated with this occurrence.
	 * 
	 * @return the declaration associated with this occurrence.
	 * @see IDeclaration
	 */
	IDeclaration getDeclaration();
}