/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.Predicate;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for predicates in Event-B Proof Obligation (PO) files.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * <p>
 * A predicate has a name <code>getName</code> associated as its attribute 
 * and the predicate in string representation <code> getPredicate()</code> in the contents. 
 * </p>
 * @author Stefan Hallerstede
 *
 */
public interface IPOPredicate extends IInternalElement, ITraceableElement {
	public String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".poPredicate"; //$NON-NLS-1$
	
	public String getName();
	public String getPredicate() throws RodinDBException;
	
	public void setPredicate(Predicate predicate, IProgressMonitor monitor) throws RodinDBException;
	public void setPredicateString(String predicate, IProgressMonitor monitor) throws RodinDBException;
}
