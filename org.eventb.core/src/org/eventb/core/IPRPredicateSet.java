/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core;

import java.util.Set;

import org.eventb.core.ast.Predicate;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Farhad Mehta
 *
 */
public interface IPRPredicateSet extends IInternalElement {
	public String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".prPredicateSet"; //$NON-NLS-1$

	public Set<Predicate> getPredicateSet() throws RodinDBException;
	public void setPredicateSet(Set<Predicate> p) throws RodinDBException;
}
