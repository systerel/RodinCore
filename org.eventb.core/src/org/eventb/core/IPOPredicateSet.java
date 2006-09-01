/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for predicate sets in Event-B Proof Obligation (PO) files.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * <p>
 * A predicate set consists of predicates <code>getPredicates()</code> and perhaps a predicate set 
 * <code>getPredicateSet</code> whose name is stored in the contents field.
 * If the contents equals the empty string there is no contained predicate set.
 * </p>
 * @author Stefan Hallerstede
 *
 */
public interface IPOPredicateSet extends IInternalElement {
	public String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".poPredicateSet"; //$NON-NLS-1$
	
	public IPOPredicate[] getPredicates() throws RodinDBException;
	public IPOPredicateSet getPredicateSet() throws RodinDBException;
	public void setParentPredicateSet(String name, IProgressMonitor monitor) throws RodinDBException;
}
