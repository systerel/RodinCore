/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Systerel - Initial API and implementation
 *******************************************************************************/
package org.rodinp.core.emf.api.itf;

import java.util.List;

import org.rodinp.core.IInternalElement;

/**
 * Interface used by contributors to define a way to retrieve implicit children
 * of a 'parent' element (i.e. container).
 * 
 * @author Thomas Muller
 */
public interface ICoreImplicitChildProvider {

	/**
	 * Gives all the implicit children for a given containing element (i.e.
	 * parent). Implementors should make this method return indirect elements
	 * which are inherited from abstraction.
	 * 
	 * @param parent
	 *            the parent element for which we want the implicit children
	 * @return the list of indirect children of the given element
	 */
	public List<? extends IInternalElement> getImplicitChildren(
			IInternalElement parent);

}
