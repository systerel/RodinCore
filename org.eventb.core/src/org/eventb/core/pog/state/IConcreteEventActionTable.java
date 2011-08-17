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
import org.eventb.core.pog.POGCore;
import org.eventb.core.tool.IStateType;

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
 * @since 1.0
 */
public interface IConcreteEventActionTable extends IEventActionTable {

	final static IStateType<IConcreteEventActionTable> STATE_TYPE = 
		POGCore.getToolStateType(EventBPlugin.PLUGIN_ID + ".concreteEventActionTable");

	/**
	 * Returns a substitution to rename all variables not in the frame of the actions
	 * of the event from primed to unprimed, or <code>null</code> if there are no variables to
	 * be renamed.
	 * 
	 * @return a substitution to rename all variables not in the frame of the actions
	 * of the event from primed to unprimed, or <code>null</code> if there are no variables to
	 * be renamed
	 */
	BecomesEqualTo getXiUnprime();
	
	/**
	 * Returns a substitution to rename all variables in the frame of the actions
	 * of the event from unprimed to primed, or <code>null</code> if there are no variables to
	 * be renamed.
	 * 
	 * @return a substitution to rename all variables in the frame of the actions
	 * of the event from unprimed to primed, or <code>null</code> if there are no variables to
	 * be renamed
	 */
	BecomesEqualTo getDeltaPrime();

}
