/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.sc.state;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IEvent;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.sc.SCCore;
import org.eventb.core.tool.IStateType;

/**
 * State component for refinement information associated with events.
 * <p>
 * For the initialisation event <code>getRefinesClauses()</code> yields an empty
 * list, but <code>getAbstractEventInfos()</code> returns a list with one
 * element. For all other events, the list returned by
 * <code>getAbstractEventInfos()</code> may be shorter than the list returned by
 * <code>getRefinesClauses()</code>. This is because some refines clauses may
 * refer to non-existing abstract events.
 * </p>
 * 
 * @author Stefan Hallerstede
 * @since 1.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IConcreteEventInfo extends IAccuracyInfo {

	final static IStateType<IConcreteEventInfo> STATE_TYPE = SCCore
			.getToolStateType(EventBPlugin.PLUGIN_ID + ".concreteEventInfo");

	/**
	 * Returns whether the current event is new (i.e. does not refine an
	 * abstract event) or not.
	 * 
	 * @return whether the current event is new or not
	 * @throws CoreException
	 *             if this state component is mutable
	 */
	boolean eventIsNew() throws CoreException;

	/**
	 * Returns the infos for the abstract events that are refined by the current
	 * event.
	 * 
	 * @return the infos for the abstract events that are refined by the current
	 *         event
	 * @throws CoreException
	 *             if this state component is mutable
	 */
	List<IAbstractEventInfo> getAbstractEventInfos() throws CoreException;

	/**
	 * Returns the refines clauses of the current event.
	 * 
	 * @return the refines clauses of the current event
	 * @throws CoreException
	 *             if this state component is mutable
	 */
	List<IRefinesEvent> getRefinesClauses() throws CoreException;

	/**
	 * Returns the symbol info of the event to which this refine info belongs.
	 * 
	 * @return the symbol info of the event to which this refine info belongs
	 */
	ILabelSymbolInfo getSymbolInfo();

	/**
	 * Returns whether the event is the initialisation.
	 * 
	 * @return whether the event is the initialisation
	 */
	boolean isInitialisation();

	/**
	 * Returns the event.
	 * 
	 * @return the event
	 */
	IEvent getEvent();

	/**
	 * Returns the label of the event.
	 * 
	 * @return the label of the event
	 */
	String getEventLabel();

}
