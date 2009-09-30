/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pog.state;

import org.eventb.core.EventBPlugin;
import org.eventb.core.pog.POGCore;
import org.eventb.core.sc.state.IAccuracyInfo;
import org.eventb.core.tool.IStateType;

/**
 * Common protocol for accessing and managing the hypothesis sets of a context.
 * <p>
 * This state component also stores accuracy information for a context.
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
public interface IContextHypothesisManager extends IHypothesisManager {
	
	final static IStateType<IContextHypothesisManager> STATE_TYPE = 
		POGCore.getToolStateType(EventBPlugin.PLUGIN_ID + ".contextHypothesisManager");

	/**
	 * Returns whether the context is accurate.
	 * 
	 * @return whether the context is accurate
	 */
	boolean contextIsAccurate();
	
}
