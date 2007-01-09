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
import org.eventb.core.ISCWitness;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;

/**
 * This state component provides information on witnesses associated with refined events.
 * 
 * @author Stefan Hallerstede
 * 
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 *
 */
public interface IWitnessTable extends IState {

	final static String STATE_TYPE = EventBPlugin.PLUGIN_ID + ".witnessTable";

	/**
	 * Returns the array of witnesses.
	 * The indices of the returned array correspond to those
	 * returned by <code>getVariables()</code> and
	 * <code>getPredicates()</code>.
	 * 
	 * @see IWitnessTable#getVariables()
	 * @see IWitnessTable#getPredicates()
	 * 
	 * @return the array of witnesses
	 */
	ISCWitness[] getWitnesses();

	/**
	 * Returns the array of witnessed variables. Machine variable witnesses are primed.
	 * <p>
	 * The indices of this list correspond to the indices of the list of
	 * witnesses returned by <code>getWitnesses()</code>.
	 * </p>
	 * 
	 * @see IWitnessTable#getWitnesses()
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
	 * @see IWitnessTable#getWitnesses()
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
	 * @see IWitnessTable#getWitnesses()
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
	 * @see IWitnessTable#getMachineDetAssignments()
	 * @see IWitnessTable#getMachinePrimedDetAssignments()
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
	 * @see IWitnessTable#getMachineDetWitnesses()
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
	 * @see IWitnessTable#getMachineDetWitnesses()
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
	 * @see IWitnessTable#getEventDetAssignments()
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
	 * @see IWitnessTable#getEventDetWitnesses()
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
	 * @see IWitnessTable#getNondetPredicates()
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
	 * @see IWitnessTable#getNondetWitnesses()
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
	 * @see IWitnessTable#getNondetWitnesses()
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
