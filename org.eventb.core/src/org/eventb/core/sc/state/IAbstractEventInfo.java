/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.sc.state;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.EventBPlugin;
import org.eventb.core.ISCEvent;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.sc.SCCore;
import org.eventb.core.tool.IStateType;

/**
 * This state (sub-) component provides access to information about an abstract event.
 * It is only accessible by way of {@link IAbstractEventTable}.
 * <p>
 * In comparisons only event labels are considered, i.e., two 
 * abstract event infos are considered equal if their labels are equal.
 * </P>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see IAbstractEventTable
 * 
 * @author Stefan Hallerstede
 *
 * @since 1.0
 */
public interface IAbstractEventInfo extends ISCState, IConvergenceInfo,
		Comparable<IAbstractEventInfo> {
	
	final static IStateType<IAbstractEventInfo> STATE_TYPE = 
		SCCore.getToolStateType(EventBPlugin.PLUGIN_ID + ".abstractEventInfo");

	/**
	 * Returns a handle to the SC event corresponding to this abstract event info.
	 * 
	 * @return a handle to the SC event corresponding to this abstract event info
	 */
	ISCEvent getEvent();
	
	/**
	 * Returns the label of the SC event corresponding to this abstract event info.
	 * <p>
	 * Calling this method should be prefered over
	 * <code>getEvent().getLabel()</code> because it is more efficient.
	 * </p>
	 * 
	 * @return the label of the SC event corresponding to this abstract event info
	 */
	String getEventLabel();
	
	/**
	 * Returns a typed free parameter with the specified name contained in this 
	 * abstract event info, or <code>null</code> if no parameter with this name
	 * is contained in this abstract event info.
	 * 
	 * @param name the name of the free identifier to look up
	 * @return the corresponding typed free identifier, or <code>null</code> if none
	 * @throws CoreException if state is not immutable
	 */
	FreeIdentifier getParameter(String name) throws CoreException;
	
	/**
	 * Returns the array of typed free parameters contained in this abstract event info.
	 * <p>
	 * The free identifiers correspond to event parameters.
	 * </p>
	 * 
	 * @return the array of typed free identifiers contained in this abstract event info
	 * @throws CoreException if state is not immutable
	 */
	List<FreeIdentifier> getParameters() throws CoreException;
	
	/**
	 * Returns the array of parsed and type-checked predicates corresponding to the guards
	 * of the corresponding event.
	 * 
	 * @return the array of parsed and type-checked predicates corresponding to the guards
	 * @throws CoreException if state is not immutable
	 * of the corresponding event
	 */
	List<Predicate> getGuards() throws CoreException;
	
	/**
	 * Returns the array of parsed and type-checked assignments corresponding to the actions
	 * of the corresponding event.
	 * 
	 * @return the array of parsed and type-checked assignments corresponding to the actions
	 * @throws CoreException if state is not immutable
	 * of the corresponding event
	 */
	List<Assignment> getActions() throws CoreException;
	
	/**
	 * Returns the list events that are supposed to merge this abstract event with another.
	 * 
	 * @return the list events that are supposed to merge this abstract event with another
	 */
	List<IConcreteEventInfo> getMergers();
	
	/**
	 * Returns the list events that are supposed to split this abstract event.
	 *
	 * @return the list events that are supposed to merge this abstract event
	 */
	List<IConcreteEventInfo> getSplitters();
}
