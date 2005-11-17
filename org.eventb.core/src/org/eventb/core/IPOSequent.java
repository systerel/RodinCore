/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * @author halstefa
 *
 */
public interface IPOSequent extends IInternalElement {
	String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".poSequent";
	
	public String getName();
	public IPOIdentifier[] getIdentifiers() throws RodinDBException;
	public IPOHypothesis getHypothesis() throws RodinDBException;
	public IPOAnyPredicate getGoal() throws RodinDBException;
	public String getHint(String hintName) throws RodinDBException;
}
