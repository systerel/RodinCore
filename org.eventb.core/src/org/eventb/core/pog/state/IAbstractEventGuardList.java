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
import org.eventb.core.ISCEvent;
import org.eventb.core.pog.POGCore;
import org.eventb.core.tool.IStateType;

/**
 * This class provides information on the guards of all abstract event
 * (being refined by some concrete event). In case of a merge refinement
 * there is more than one abstract event.
 * <p>
 * The information for each event is provided by class <code>IAbstractEventGuardTable</code>.
 * </p>
 * 
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Stefan Hallerstede
 *
 * @since 1.0
 */
public interface IAbstractEventGuardList extends IPOGState {

	final static IStateType<IAbstractEventGuardList> STATE_TYPE = 
		POGCore.getToolStateType(EventBPlugin.PLUGIN_ID + ".abstractEventGuardList");

	/**
	 * A new event is introduced, i.e. there is no abstraction.
	 */
	public final int INTRO = 0;
	
	/**
	 * Several abstract events are merged into one event, i.e. 
	 * there are several abstractions (more than one).
	 */
	public final int MERGE = 1;
	
	/**
	 * An abstraction is refined by at least one event, i.e.
	 * there is exacly one abstraction.
	 */
	public final int SPLIT = 2;
	
	/**
	 * Returns the list of SC abstract events corresponding to the guards
	 * 
	 * @return the list of SC abstract events corresponding to the guards
	 */
	List<ISCEvent> getAbstractEvents();
	
	/**
	 * Returns the first abstract event from the list corresponding to the guards, 
	 * or <code>null</code> if the list is empty.
	 * 
	 * @return the first abstract event from the list corresponding to the guards, 
	 * 		or <code>null</code> if the list is empty
	 */
	ISCEvent getFirstAbstractEvent();
	
	/**
	 * Returns the list of abstract event guard tables
	 * 
	 * @return the list of abstract event guard tables
	 */
	List<IAbstractEventGuardTable> getAbstractEventGuardTables();
	
	/**
	 * Returns the first abstract event guard table from the list corresponding to the guards, 
	 * or <code>null</code> if the list is empty.
	 * 
	 * @return the first abstract event guard table from the list corresponding to the guards, 
	 * 		or <code>null</code> if the list is empty
	 */
	IAbstractEventGuardTable getFirstAbstractEventGuardTable();
	
	/**
	 * Returns the type of refinement of the current event,
	 * one of <code>INTRO</code>, <code>MERGE</code>, <code>SPLIT</code>.
	 * 
	 * @return the type of refinement of the current event
	 */
	int getRefinementType();
	
}
