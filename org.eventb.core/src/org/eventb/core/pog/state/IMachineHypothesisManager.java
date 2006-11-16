/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pog.state;

import org.eventb.core.EventBPlugin;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOPredicateSet;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public interface IMachineHypothesisManager extends IHypothesisManager {

	final static String STATE_TYPE = EventBPlugin.PLUGIN_ID + ".machineHypothesisManager";

	IPOPredicateSet getContextHypothesis(IPOFile target) throws RodinDBException;
	
	boolean isInitialMachine();
}
