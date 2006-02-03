/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core;

import org.rodinp.core.RodinDBException;

/**
 * Common protocol for predicates preceded by a substitution in Event-B Proof Obligation (PO) files.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * <p>
 * A predicate form is a predicate to be substituted.
 * It is described by a pair (SUBST, PRED).
 * It consists of a substitution SUBST <code>getSubstitution()</code> and
 * a predicate or predicate form PRED <code>getPredicate()</code>.
 * SUBST is stored in the contents and PRED is the only child.
 * </p>
 * @author Stefan Hallerstede
 *
 */
public interface IPOModifiedPredicate extends IPOAnyPredicate {
	public String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".poModifiedPredicate"; //$NON-NLS-1$
	
	public String getSubstitution() throws RodinDBException;
	public IPOAnyPredicate getPredicate() throws RodinDBException;
}
