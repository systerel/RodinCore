/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pog.state;

import java.util.List;

import org.eventb.core.EventBPlugin;
import org.eventb.core.ISCAction;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.pog.POGCore;
import org.eventb.core.tool.IStateType;

/**
 * This class provides information on the actions of the abstract event
 * (being refined by some concrete event).
 * 
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Stefan Hallerstede
 *
 * @since 1.0
 */
public interface IAbstractEventActionTable extends IEventActionTable, ICorrespondence {

	final static IStateType<IAbstractEventActionTable> STATE_TYPE = 
		POGCore.getToolStateType(EventBPlugin.PLUGIN_ID + ".abstractEventActionTable");
	
	/**
	 * Returns the list of deterministic abstract actions that should be used
	 * as witnesses for disappearing variables.
	 * 
	 * @return the list of deterministic abstract actions that should be used
	 * as witnesses for disappearing variables
	 */
	List<BecomesEqualTo> getDisappearingWitnesses();
	
	/**
	 * Returns the array of abstract actions that need to be simluated 
	 * in a refinement of the (abstract) event.
	 * <p>
	 * The parsed and type-checked assignements corresponding to the actions
	 * can be retrieved via <code>getSimAssignments()</code>.
	 * </p>
	 * 
	 * @return the array of abstract actions that need to be simluated
	 * 
	 * @see IAbstractEventActionTable#getSimAssignments()
	 */
	List<ISCAction> getSimActions();
	
	/**
	 * Returns the array of abstract (non-deterministic) assignments
	 * that need to be simluated in a refinement of the (abstract) event.
	 * <p>
	 * The indices of this list correspond to the indices of the list of
	 * actions returned by <code>getSimActions()</code>.
	 * </p>
	 * 
	 * @return array of abstract assignments that need to be simluated
	 */
	List<Assignment> getSimAssignments();
	
}
