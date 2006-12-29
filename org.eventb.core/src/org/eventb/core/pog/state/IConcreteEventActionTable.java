/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pog.state;

import org.eventb.core.EventBPlugin;
import org.eventb.core.ast.BecomesEqualTo;

/**
 * Common protocol for accessing the actions of the concrete event.
 * There is always exacly one concrete event. In case of a split refinement
 * each split event is treated separately. 
 * 
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Stefan Hallerstede
 *
 */
public interface IConcreteEventActionTable extends IEventActionTable {

	final static String STATE_TYPE = EventBPlugin.PLUGIN_ID + ".concreteEventActionTable";

	/**
	 * Returns a substitution to rename all variables not in the frame of the actions
	 * of the event from primed to unprimed.
	 * 
	 * @return a substitution to rename all variables not in the frame of the actions
	 * of the event from primed to unprimed
	 */
	BecomesEqualTo getXiUnprime();
	
	/**
	 * Returns a substitution to rename all variables in the frame of the actions
	 * of the event from unprimed to primed.
	 * 
	 * @return a substitution to rename all variables in the frame of the actions
	 * of the event from unprimed to primed
	 */
	BecomesEqualTo getDeltaPrime();

}
