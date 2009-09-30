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

import org.rodinp.core.IInternalElement;

/**
 * Common protocol for indexed element declaration.
 * <p>
 * A IDeclaration has an element and an associated name, that is the name given
 * by an indexer while indexing the file that declares the element.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * @noimplement
 * @author Nicolas Beauger
 * 
 * @since 1.0
 */
public interface IDeclaration {

	/**
	 * Returns the declared element.
	 * 
	 * @return the declared element.
	 */
	IInternalElement getElement();

	/**
	 * Returns the declared name.
	 * 
	 * @return the declared name.
	 */
	String getName();

}
