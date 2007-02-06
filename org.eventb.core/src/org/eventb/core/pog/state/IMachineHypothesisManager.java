/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pog.state;

import org.eventb.core.EventBPlugin;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.pog.POGCore;
import org.eventb.core.tool.state.IToolStateType;

/**
 * This is the hypothesis manager associated with Event-B machines.
 * 
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Stefan Hallerstede
 *
 */
public interface IMachineHypothesisManager extends IHypothesisManager {

	final static IToolStateType<IMachineHypothesisManager> STATE_TYPE = 
		POGCore.getToolStateType(EventBPlugin.PLUGIN_ID + ".machineHypothesisManager");

	/**
	 * Returns the hypothesis, i.e. the predicate set, that contains all predicates
	 * of the seen contexts.
	 * 
	 * @return the hypothesis that contains all predicates of the seen contexts
	 */
	IPOPredicateSet getContextHypothesis();
	
	/**
	 * Returns whether the machine managed by this manager is initial, i.e., it does not refine
	 * another machine.
	 * 
	 * @return whether the machine managed by this manager is initial
	 */
	boolean isInitialMachine();
}
