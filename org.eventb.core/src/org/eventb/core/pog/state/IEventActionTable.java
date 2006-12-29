/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pog.state;

import java.util.List;
import java.util.Set;

import org.eventb.core.ISCAction;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.FreeIdentifier;

/**
 * Common protocol for event actions (of abstract and concrete events).
 * 
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Stefan Hallerstede
 *
 */
public interface IEventActionTable extends IState {

	/**
	 * Returns the list of actions of the event.
	 * <p>
	 * The parsed and type-checked assignments corresponding to the actions
	 * can be retrieved via <code>getAssignments()</code>.
	 * </p>
	 * 
	 * @return the list of actions of the event
	 * 
	 * @see IEventActionTable#getAssignments()
	 */
	List<ISCAction> getActions();
	
	/**
	 * Returns the array of assignments corresponding to the actions of the event.
	 * <p>
	 * The indices of this list correspond to the indices of the list of
	 * actions returned by <code>getActions()</code>. 
	 * </p>
	 * 
	 * @return the array of assignments corresponding to the actions of the event
	 */
	List<Assignment> getAssignments();
	
	/**
	 * Returns the set of variables constituting the frame of the actions.
	 * 
	 * @return the set of variables constituting the frame of the actions
	 */
	Set<FreeIdentifier> getAssignedVariables();
	
	/**
	 * Returns the array of non-determistic actions of this event.
	 * <p>
	 * The parsed and type-checked non-determistic assignments corresponding to 
	 * the non-deterministic actions can be retrieved via <code>getNondetAssignments()</code>.
	 * </p>
	 * 
	 * @return the array of non-determistic actions of this event
	 */
	List<ISCAction> getNondetActions();
	
	/**
	 * Returns the array of non-deterministic assignments corresponding to the 
	 * non-deterministic actions of the event.
	 * <p>
	 * The indices of this list correspond to the indices of the list of
	 * non-deterministic actions returned by <code>getNondetActions()</code>. 
	 * </p>
	 * 
	 * @return the array of non-deterministic assignments corresponding to the 
	 * non-deterministic actions of the event
	 */
	List<Assignment> getNondetAssignments();
	
	/**
	 * Returns the array of deterministic actions of this event.
	 * <p>
	 * The parsed and type-checked deterministic assignments corresponding to 
	 * the deterministic actions can be retrieved via <code>getDetAssignments()</code>.
	 * </p>
	 * 
	 * @return the array of deterministic actions of this event
	 */
	List<ISCAction> getDetActions();
	
	/**
	 * Returns the array of deterministic assignments corresponding to the 
	 * deterministic actions of the event.
	 * <p>
	 * The indices of this list correspond to the indices of the list of
	 * deterministic actions returned by <code>getDetActions()</code>. 
	 * </p>
	 * 
	 * @return the array of deterministic assignments corresponding to the 
	 * deterministic actions of the event
	 */
	List<BecomesEqualTo> getDetAssignments();
	
	/**
	 * Returns the array of deterministic primed assignments corresponding to the 
	 * deterministic actions of the event, i.e. if "x := E" is in the array of
	 * deterministic assignement it appears in this array as "x' := E". Note, that
	 * the resulting primed assignments are not valid Event-B formulas, but can only
	 * be used as substitutions.
	 * <p>
	 * The indices of this list correspond to the indices of the list of
	 * deterministic actions returned by <code>getDetActions()</code>. 
	 * </p>
	 * 
	 * @return the array of deterministic primed assignments corresponding to the 
	 * deterministic actions of the event
	 */
	List<BecomesEqualTo> getPrimedDetAssignments();
	
}
