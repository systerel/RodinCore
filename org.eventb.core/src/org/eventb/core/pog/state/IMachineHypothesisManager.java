/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.pog.state;

import org.eventb.core.EventBPlugin;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.pog.POGCore;
import org.eventb.core.sc.state.IAccuracyInfo;
import org.eventb.core.tool.IStateType;

/**
 * This is the hypothesis manager associated with Event-B machines.
 * <p>
 * This state component also stores accuracy information for a machine.
 * This information is propagated from the static checker. 
 * </p> 
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see IAccuracyInfo
 * 		
 * @author Stefan Hallerstede
 *
 * @since 1.0
 */
public interface IMachineHypothesisManager extends IHypothesisManager {

	final static IStateType<IMachineHypothesisManager> STATE_TYPE = 
		POGCore.getToolStateType(EventBPlugin.PLUGIN_ID + ".machineHypothesisManager");

	/**
	 * Returns the hypothesis, i.e. the predicate set, that contains all predicates
	 * of the seen contexts.
	 * 
	 * @return the hypothesis that contains all predicates of the seen contexts
	 */
	IPOPredicateSet getContextHypothesis();

	/**
	 * Returns whether the machine is accurate.
	 * 
	 * @return whether the machine is accurate
	 */
	boolean machineIsAccurate();
	
}
