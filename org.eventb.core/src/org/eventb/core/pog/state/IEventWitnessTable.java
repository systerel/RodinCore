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

import java.util.List;

import org.eventb.core.EventBPlugin;
import org.eventb.core.ISCWitness;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pog.POGCore;
import org.eventb.core.tool.IStateType;

/**
 * This state component provides information on witnesses associated with refined events.
 * 
 * @author Stefan Hallerstede
 * 
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 *
 * @since 1.0
 */
public interface IEventWitnessTable extends IPOGState {

	final static IStateType<IEventWitnessTable> STATE_TYPE = 
		POGCore.getToolStateType(EventBPlugin.PLUGIN_ID + ".eventWitnessTable");

	/**
	 * Returns the array of witnesses.
	 * The indices of the returned array correspond to those
	 * returned by <code>getVariables()</code> and
	 * <code>getPredicates()</code>.
	 * 
	 * @see IEventWitnessTable#getVariables()
	 * @see IEventWitnessTable#getPredicates()
	 * 
	 * @return the array of witnesses
	 */
	List<ISCWitness> getWitnesses();

	/**
	 * Returns the array of witnessed variables. Machine variable witnesses are primed.
	 * <p>
	 * The indices of this list correspond to the indices of the list of
	 * witnesses returned by <code>getWitnesses()</code>.
	 * </p>
	 * 
	 * @see IEventWitnessTable#getWitnesses()
	 * 
	 * @return array of witnessed variables
	 */
	List<FreeIdentifier> getVariables();
	
	/**
	 * Returns the array of parsed and type-checked predicates
	 * corresponding to the witnesses for event and machine variables.
	 * <p>
	 * The indices of this list correspond to the indices of the list of
	 * witnesses returned by <code>getWitnesses()</code>.
	 * </p>
	 * 
	 * @see IEventWitnessTable#getWitnesses()
	 * 
	 * @return the array of parsed and type-checked predicates
	 * corresponding to the witnesses for event and machine variables
	 */
	List<Predicate> getPredicates();
	
	/**
	 * Returns whether the witness at the specified index is deterministic.
	 * <p>
	 * The indices correspond to the indices of the list of
	 * witnesses returned by <code>getWitnesses()</code>.
	 * </p>
	 * 
	 * @see IEventWitnessTable#getWitnesses()
	 * 
	 * @return whether the witness at the specified index is deterministic
	 */
	boolean isDeterministic(int index);

	/**
	 * Returns the array of deterministic witnesses for <b>machine variables</b>.
	 * The indices of the returned array correspond to those
	 * returned by <code>getMachineDetAssignments()</code> and
	 * <code>getMachinePrimedDetAssignments()</code>.
	 * 
	 * @see IEventWitnessTable#getMachineDetAssignments()
	 * @see IEventWitnessTable#getMachinePrimedDetAssignments()
	 * 
	 * @return the array of deterministic witnesses
	 */
	List<ISCWitness> getMachineDetWitnesses();
	
	/**
	 * Returns the array of parsed and type-checked assignments
	 * corresponding to the deterministic witnesses for machine variables.
	 * <p>
	 * The indices of this list correspond to the indices of the list of
	 * witnesses returned by <code>getMachineDetWitnesses()</code>.
	 * </p>
	 * 
	 * @see IEventWitnessTable#getMachineDetWitnesses()
	 * 
	 * @return the array of parsed and type-checked assignments
	 * corresponding to the deterministic witnesses for machine variables
	 */
	List<BecomesEqualTo> getMachineDetAssignments();
	
	/**
	 * Returns the array of parsed and type-checked assignments <b>to primed variables</b>
	 * corresponding to the deterministic witnesses for machine variables. 
	 * The returned assignment can only by used as a logical substitution. 
	 * It is not a well-formed assignment in the mathematical notation of Event-B. 
	 * <p>
	 * The indices of this list correspond to the indices of the list of
	 * witnesses returned by <code>getMachineDetWitnesses()</code>.
	 * </p>
	 * 
	 * @see IEventWitnessTable#getMachineDetWitnesses()
	 * 
	 * @return the array of parsed and type-checked assignments to primed variables
	 * corresponding to the deterministic witnesses for machine variables
	 */
	List<BecomesEqualTo> getMachinePrimedDetAssignments();
	
	/**
	 * Returns the array of deterministic witnesses for <b>event variables</b>.
	 * The indices of the returned array correspond to those
	 * returned by <code>getEventDetAssignments()</code>.
	 * 
	 * @see IEventWitnessTable#getEventDetAssignments()
	 * 
	 * @return the array of deterministic witnesses for event variables
	 */
	List<ISCWitness> getEventDetWitnesses();
	
	/**
	 * Returns the array of parsed and type-checked assignments
	 * corresponding to the deterministic witnesses for event variables.
	 * <p>
	 * The indices of this list correspond to the indices of the list of
	 * witnesses returned by <code>getEventDetWitnesses()</code>.
	 * </p>
	 * 
	 * @see IEventWitnessTable#getEventDetWitnesses()
	 * 
	 * @return the array of parsed and type-checked assignments
	 * corresponding to the deterministic witnesses for event variables
	 */
	List<BecomesEqualTo> getEventDetAssignments();

	/**
	 * Returns the array of non-deterministic witnesses for event and machine variables.
	 * The indices of the returned array correspond to those
	 * returned by <code>getNondetPredicates()</code>.
	 * 
	 * @see IEventWitnessTable#getNondetPredicates()
	 * 
	 * @return the array of non-deterministic witnesses for event and machine variables
	 */
	List<ISCWitness> getNondetWitnesses();
	
	/**
	 * Returns the array of type-checked identifiers corresponding to the non-deterministic 
	 * witnesses for event and machine variables. Machine variable witnesses have a 
	 * prime attached, event variables not.
	 * <p>
	 * The indices of this list correspond to the indices of the list of
	 * witnesses returned by <code>getNondetWitnesses()</code>.
	 * </p>
	 * 
	 * @see IEventWitnessTable#getNondetWitnesses()
	 * 
	 * @return the array of parsed and type-checked predicates
	 * corresponding to the non-deterministic witnesses for event and machine variables
	 */
	List<FreeIdentifier> getNondetVariables();
	
	/**
	 * Returns the array of parsed and type-checked predicates
	 * corresponding to the non-deterministic witnesses for event and machine variables.
	 * <p>
	 * The indices of this list correspond to the indices of the list of
	 * witnesses returned by <code>getNondetWitnesses()</code>.
	 * </p>
	 * 
	 * @see IEventWitnessTable#getNondetWitnesses()
	 * 
	 * @return the array of parsed and type-checked predicates
	 * corresponding to the non-deterministic witnesses for event and machine variables
	 */
	List<Predicate> getNondetPredicates();

	/**
	 * Returns a substitution that renames machine variables witnessed non-deterministically
	 * into primed variables.
	 * 
	 * @return a substitution that renames machine variables witnessed non-deterministically
	 * into primed variables
	 */
	BecomesEqualTo getPrimeSubstitution();

}
