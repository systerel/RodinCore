/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core;

import org.rodinp.core.IUnnamedInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for Hypotheses in Event-B Proof Obligation (PO) files.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * <p>
 * A Hypothesis is a pair (GOBAL_HYP, LOCAL_HYP)
 * <ul>
 * <li>The contents of the hypothesis is a reference to a predicate set (GLOBAL_HYP).</li>
 * <li>The children of the hypothesis are the local hypotheses (LOCAL_HYP).</li>
 * <li>The children are of either of type POPredicate or POModifiedPredicate.</li>
 * </ul>
 *
 * @author Stefan Hallerstede
 *
 */
public interface IPOHypothesis extends IUnnamedInternalElement {
	public String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".poHypothesis"; //$NON-NLS-1$
	
	public IPOPredicateSet getGlobalHypothesis() throws RodinDBException;
	public IPOAnyPredicate[] getLocalHypothesis() throws RodinDBException;
}
