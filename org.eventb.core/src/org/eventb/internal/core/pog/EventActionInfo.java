/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ISCAction;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.pog.state.IEventActionTable;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class EventActionInfo implements IEventActionTable {
	
	protected final ISCAction[] actions;
	protected final Assignment[] assignments;
	protected final ArrayList<Assignment> nondeterm;
	protected final ArrayList<BecomesEqualTo> determist;
	protected final ArrayList<BecomesEqualTo> primedDetermist;

	protected final ArrayList<ISCAction> nondetActions;
	protected final ArrayList<ISCAction> detActions;
	
	protected final HashSet<FreeIdentifier> assignedVars;

	public EventActionInfo(
			ISCAction[] actions, 
			ITypeEnvironment typeEnvironment, 
			FormulaFactory factory) throws CoreException {
		nondeterm = 
			new ArrayList<Assignment>(actions.length);
		assignedVars = 
			new HashSet<FreeIdentifier>(actions.length * 15);
		assignments = new Assignment[actions.length];		
		determist = 
			new ArrayList<BecomesEqualTo>(actions.length);
		primedDetermist =
			new ArrayList<BecomesEqualTo>(actions.length);
		this.actions = actions;
		nondetActions = new ArrayList<ISCAction>(actions.length);
		detActions = new ArrayList<ISCAction>(actions.length);
		
		for (int i=0; i<actions.length; i++) {
			
			assignments[i] = actions[i].getAssignment(factory, typeEnvironment, null);
			
			fetchAssignedIdentifiers(assignedVars, assignments[i]);
			
			if (assignments[i] instanceof BecomesEqualTo) {
				determist.add((BecomesEqualTo) assignments[i]);
				detActions.add(actions[i]);
			} else {
				nondeterm.add(assignments[i]);
				nondetActions.add(actions[i]);
			}
		}
		makePrimedDetermist(factory);
		determist.trimToSize();
		nondeterm.trimToSize();
		nondetActions.trimToSize();
		detActions.trimToSize();
		primedDetermist.trimToSize();
	}
	
	public Set<FreeIdentifier> getAssignedVariables() {
		return assignedVars;
	}

	public Assignment[] getAssignments() {
		return assignments;
	}

	public ArrayList<BecomesEqualTo> getDetAssignments() {
		return determist;
	}

	private void makePrimedDetermist(FormulaFactory factory) {
		for (BecomesEqualTo becomesEqualTo : determist) {
			FreeIdentifier[] unprimedLeft = becomesEqualTo.getAssignedIdentifiers();
			FreeIdentifier[] primedLeft = new FreeIdentifier[unprimedLeft.length];
			for (int i=0; i<unprimedLeft.length; i++)
				primedLeft[i] = unprimedLeft[i].withPrime(factory);
			BecomesEqualTo primed = 
				factory.makeBecomesEqualTo(primedLeft, becomesEqualTo.getExpressions(), null);
			primedDetermist.add(primed);
		}
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pog.IAssignmentTable#getPrimedDetAssignments()
	 */
	public ArrayList<BecomesEqualTo> getPrimedDetAssignments() {
		return primedDetermist;
	}
	
	protected void fetchAssignedIdentifiers(
			HashSet<FreeIdentifier> assignedIdents, 
			Assignment assignment) {
		FreeIdentifier[] freeIdentifiers = assignment.getAssignedIdentifiers();
		for (FreeIdentifier identifier : freeIdentifiers)
			assignedIdents.add(identifier);
	}

	public ArrayList<Assignment> getNondetAssignments() {
		return nondeterm;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pog.IEventActionTable#getActions()
	 */
	public ISCAction[] getActions() {
		return actions;
	}
	
	public ArrayList<ISCAction> getNondetActions() {
		return nondetActions;
	}
	
	public ArrayList<ISCAction> getDetActions() {
		return detActions;
	}

}
