/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pog.state;

import java.util.ArrayList;
import java.util.Set;

import org.eventb.core.EventBPlugin;
import org.eventb.core.ISCWitness;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;

/**
 * @author Stefan Hallerstede
 *
 */
public interface IWitnessTable extends IStatePOG {

	final static String STATE_TYPE = EventBPlugin.PLUGIN_ID + ".witnessTable";

	ISCWitness[] getWitnesses();

	ArrayList<ISCWitness> getMachineDetWitnesses();
	ArrayList<BecomesEqualTo> getMachineDetAssignments();
	ArrayList<BecomesEqualTo> getMachinePrimedDetAssignments();
	
	ArrayList<ISCWitness> getEventDetWitnesses();
	ArrayList<BecomesEqualTo> getEventDetAssignments();

	ArrayList<ISCWitness> getNondetWitnesses();
	ArrayList<FreeIdentifier> getNondetAssignedVariables();
	ArrayList<Predicate> getNondetPredicates();

	/**
	 * Returns all witnesses variables. Machine variable witnesses have a prime.
	 * 
	 * @return all witnesses variables
	 */
	Set<FreeIdentifier> getWitnessedVariables();

	BecomesEqualTo getPrimeSubstitution();

}
