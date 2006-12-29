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

import org.eventb.core.EventBPlugin;
import org.eventb.core.ISCWitness;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;

/**
 * @author Stefan Hallerstede
 * 
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 *
 */
public interface IWitnessTable extends IState {

	final static String STATE_TYPE = EventBPlugin.PLUGIN_ID + ".witnessTable";

	List<ISCWitness> getWitnesses();

	List<ISCWitness> getMachineDetWitnesses();
	List<BecomesEqualTo> getMachineDetAssignments();
	List<BecomesEqualTo> getMachinePrimedDetAssignments();
	
	List<ISCWitness> getEventDetWitnesses();
	List<BecomesEqualTo> getEventDetAssignments();

	List<ISCWitness> getNondetWitnesses();
	List<Predicate> getNondetPredicates();

	/**
	 * Returns all witnesses variables. Machine variable witnesses are primed.
	 * 
	 * @return all witnesses variables
	 */
	Set<FreeIdentifier> getWitnessedVariables();

	BecomesEqualTo getPrimeSubstitution();

}
