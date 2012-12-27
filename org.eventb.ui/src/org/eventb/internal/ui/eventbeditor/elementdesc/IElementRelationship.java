/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added methods to retrieve priority and implicitChildProvider
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.elementdesc;

import org.eventb.ui.IImplicitChildProvider;
import org.rodinp.core.IElementType;
import org.rodinp.core.IInternalElementType;

/**
 * @author htson
 *         <p>
 *         Proposed interface for element relationship. This is represented by a
 *         unique string id and denotes the the relationship from a parent
 *         element type to a child element type.
 *         </p>
 *         <p>
 *         This should be part of the org.rodinp.core package and subjected to
 *         changes.
 *         </p>
 */
public interface IElementRelationship extends Comparable<IElementRelationship> {

	/**
	 * Return the unique id which can be used to identify the relationship
	 * 
	 * @return The unique string id of the relationship
	 */
	public abstract String getID();

	/**
	 * Return the parent element type of the relationship.
	 * 
	 * @return an element type
	 */
	public abstract IElementType<?> getParentType();

	/**
	 * Return the child element type of the relationship.
	 * 
	 * @return an element type
	 */
	public abstract IInternalElementType<?> getChildType();

	
	/**
	 * Return the provider of implicit children elements of the relationship.
	 * 
	 * @return an implicit child provider
	 */
	public abstract IImplicitChildProvider getImplicitChildProvider();
	
	
	/**
	 * Return the priority associated to this relationship.
	 * 
	 * @return the priority of this relationship
	 */
	public abstract int getPriority();

}
