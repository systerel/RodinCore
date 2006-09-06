/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog;

import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ISCAction;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.pog.IAbstractEventActionTable;
import org.eventb.core.pog.IMachineVariableTable;

/**
 * @author Stefan Hallerstede
 *
 */
public class AbstractEventActionInfo extends EventActionInfo implements
		IAbstractEventActionTable {
	
	ArrayList<BecomesEqualTo> disappearingWitnesses;
	ArrayList<Assignment> simAssignments;
	ArrayList<ISCAction> simActions;

	public AbstractEventActionInfo(
			ISCAction[] actions, 
			ITypeEnvironment typeEnvironment, 
			IMachineVariableTable variables,
			FormulaFactory factory) throws CoreException {
		super(actions, typeEnvironment, factory);
		
		disappearingWitnesses = new ArrayList<BecomesEqualTo>(actions.length);
		simAssignments = new ArrayList<Assignment>(actions.length);
		simActions = new ArrayList<ISCAction>(actions.length);
		
		for (int i=0; i<assignments.length; i++) {
			if (isDisappearing(assignments[i], variables) 
					&& assignments[i] instanceof BecomesEqualTo) {
				disappearingWitnesses.add((BecomesEqualTo) assignments[i]);
			} else {
				simAssignments.add(assignments[i]);
				simActions.add(actions[i]);
			}
					
		}
		disappearingWitnesses.trimToSize();
		simAssignments.trimToSize();
		simActions.trimToSize();

	}
	
	private boolean isDisappearing(Assignment assignment, IMachineVariableTable variables) {
		boolean found = false;
		for (FreeIdentifier identifier : assignment.getAssignedIdentifiers())
			if (variables.contains(identifier)) {
				found = true;
			}
		return !found;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IState#getStateType()
	 */
	public String getStateType() {
		return STATE_TYPE;
	}

	public ArrayList<BecomesEqualTo> getDisappearingWitnesses() {
		return disappearingWitnesses;
	}

	public ArrayList<Assignment> getSimAssignments() {
		return simAssignments;
	}

	public ArrayList<ISCAction> getSimActions() {
		return simActions;
	}

}
