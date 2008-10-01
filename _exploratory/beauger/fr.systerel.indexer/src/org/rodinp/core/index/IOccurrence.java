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
	 * @see IRodinLocation
	 */
	IRodinLocation getLocation();

	/**
	 * Returns the file containing the location of the occurrence, if any. If
	 * the location is a Rodin file or occurs within a Rodin file, then this
	 * Rodin file is returned. Otherwise, <code>null</code> is returned.
	 * <p>
	 * Equivalent to <code>getLocation().getRodinFile()</code>.
	 * 
	 * @return the file containing the location or <code>null</code>
	 * @see IRodinLocation#getRodinFile()
	 */
	IRodinFile getRodinFile();

}