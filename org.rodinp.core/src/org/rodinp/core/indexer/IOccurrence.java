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

import org.rodinp.core.IRodinFile;
import org.rodinp.core.location.IInternalLocation;

/**
 * Common protocol for occurrences in indexed files.
 * <p>
 * An occurrence has a kind {@link IOccurrenceKind} and a location
 * {@link IInternalLocation} inside a {@link IRodinFile}.
 * </p>
 * 
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 * 
 * @author Nicolas Beauger
 * @since 1.0
 */
public interface IOccurrence {

	/**
	 * Returns the kind of this occurrence.
	 * 
	 * @return the kind of this occurrence
	 */
	IOccurrenceKind getKind();

	/**
	 * Returns the location of this occurrence.
	 * 
	 * @return the location of this occurrence
	 */
	IInternalLocation getLocation();

	/**
	 * Returns the file containing the location of this occurrence. This is a
	 * shorthand method for
	 * 
	 * <pre>
	 * getLocation().getRodinFile()
	 * </pre>
	 * 
	 * @return the file containing the location of this occurrence
	 */
	IRodinFile getRodinFile();

	/**
	 * Returns the declaration of the element associated with this occurrence.
	 * 
	 * @return the declaration associated with this occurrence
	 */
	IDeclaration getDeclaration();

}