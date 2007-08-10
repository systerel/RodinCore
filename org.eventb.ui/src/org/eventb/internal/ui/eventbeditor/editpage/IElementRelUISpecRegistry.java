/*******************************************************************************
 * Copyright (c) 2007 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.eventbeditor.editpage;

import java.util.List;

import org.eventb.internal.ui.elementSpecs.IElementRelationship;
import org.eventb.ui.EventBUIPlugin;
import org.rodinp.core.IElementType;

/**
 * @author htson
 *         <p>
 *         The interface for Element relationship UI specification registry.
 *         This registry stores information how elements are
 *         displayed and/or edited.
 *         </p>
 *         <p>
 *         This interface is NOT intended to be implemented by clients. Instead,
 *         clients should get the default instance of this through
 *         {@link EventBUIPlugin}.
 *         </p>
 */
public interface IElementRelUISpecRegistry {

	/**
	 * Return a list of element relationship corresponding to a parent element
	 * type.
	 * 
	 * @param parentType
	 *            an element type.
	 * @return a list of element relationship. This must not be
	 *         <code>null</code>. In the case where there is no relationship,
	 *         an empty list is returned.
	 */
	public abstract List<IElementRelationship> getElementRelationships(
			IElementType<?> parentType);

	/**
	 * Get the prefix for the section corresponding to a relationship.
	 * 
	 * @param rel
	 *            an element relationship {@link IElementRelationship}.
	 * @return the string prefix corresponding to the input relationship or
	 *         <code>null</code> if the relationship has no prefix.
	 */
	public abstract String getPrefix(IElementRelationship rel);

	/**
	 * Get the postfix for the section corresponding to a relationship.
	 * 
	 * @param rel
	 *            an element relationship {@link IElementRelationship}.
	 * @return the string postfix corresponding to the input relationship or
	 *         <code>null</code> if the relationship has no postfix.
	 */
	public abstract String getPostfix(IElementRelationship rel);

}