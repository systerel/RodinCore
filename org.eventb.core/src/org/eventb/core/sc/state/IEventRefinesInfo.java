/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.sc.state;

import java.util.List;

import org.eventb.core.EventBPlugin;
import org.eventb.core.IRefinesEvent;

/**
 * State component for refinement information associated with events.
 * If <code>getRefinesClauses()</code> yields an empty list, but 
 * <code>currentEventIsRefined()</code> yields <code>true</code>, then
 * the current event is implicitly refined, e.g., it is inherited or
 * an initialisation in a refined machine. In that case <code>getAbstractEventInfos()</code>
 * returns a list with one element.
 * 
 * @author Stefan Hallerstede
 *
 */
public interface IEventRefinesInfo extends IState {
	
	final static String STATE_TYPE = EventBPlugin.PLUGIN_ID + ".eventRefinesInfo";
	
	/**
	 * Returns whether the current event is refined or not.
	 * 
	 * @return whether the current event is refined or not
	 */
	boolean currentEventIsRefined();
	
	/**
	 * Returns the infos for the abstract events that are refined by the current event.
	 * 
	 * @return the infos for the abstract events that are refined by the current event
	 */
	List<IAbstractEventInfo> getAbstractEventInfos();
	
	/**
	 * Returns the refines clauses of the current event.
	 * 
	 * @return the refines clauses of the current event
	 */
	List<IRefinesEvent> getRefinesClauses();
	
}
