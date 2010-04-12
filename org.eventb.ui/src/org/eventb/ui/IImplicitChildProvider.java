/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.ui;

import java.util.List;

import org.rodinp.core.IInternalElement;

/**
 * Interface used by contributors to define a way to retrieve implicit children
 * of an element.
 * 
 * @author Thomas Muller
 * @since 1.3
 */
public interface IImplicitChildProvider {

	/**
	 * Method to retrieve all implicit children of a given element. Implementors
	 * should make this method return indirect elements which are inherited from
	 * abstraction.
	 * 
	 * @param parent
	 *            the parent element for which we want the implicit children
	 * @return the list of indirect children of the given element
	 */
	public List<? extends IInternalElement> getImplicitChildren(
			IInternalElement parent);

}
