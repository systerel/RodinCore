/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ISCAction;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.pog.state.IEventActionTable;
import org.eventb.internal.core.tool.state.State;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class EventActionTable extends State implements IEventActionTable {
	
	protected final List<ISCAction> actions;
	protected final ArrayList<Assignment> assignments;
	protected final ArrayList<Assignment> nondeterm;
	protected final ArrayList<BecomesEqualTo> determist;
	protected final ArrayList<BecomesEqualTo> primedDetermist;

	protected final ArrayList<ISCAction> nondetActions;
	protected final ArrayList<ISCAction> detActions;
	
	protected final HashSet<FreeIdentifier> assignedVars;

	public EventActionTable(
			ISCAction[] actions, 
			ITypeEnvironment typeEnvironment, 
			FormulaFactory factory) throws CoreException {
		nondeterm = 
			new ArrayList<Assignment>(actions.length);
		assignedVars = 
			new HashSet<FreeIdentifier>(actions.length * 15);
		assignments = new ArrayList<Assignment>(actions.length);		
		determist = 
			new ArrayList<BecomesEqualTo>(actions.length);
		primedDetermist =
			new ArrayList<BecomesEqualTo>(actions.length);
		this.actions = Arrays.asList(actions);
		nondetActions = new ArrayList<ISCAction>(actions.length);
		detActions = new ArrayList<ISCAction>(actions.length);
		
		for (ISCAction action : actions) {
			
			Assignment assignment = action.getAssignment(factory, typeEnvironment);
			
			assignments.add(assignment);
			
			fetchAssignedIdentifiers(assignedVars, assignment);
			
			if (assignment instanceof BecomesEqualTo) {
				determist.add((BecomesEqualTo) assignment);
				detActions.add(action);
			} else {
				nondeterm.add(assignment);
				nondetActions.add(action);
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

	public List<Assignment> getAssignments() {
		return assignments;
	}

	public List<BecomesEqualTo> getDetAssignments() {
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
	public List<BecomesEqualTo> getPrimedDetAssignments() {
		return primedDetermist;
	}
	
	protected void fetchAssignedIdentifiers(
			HashSet<FreeIdentifier> assignedIdents, 
			Assignment assignment) {
		FreeIdentifier[] freeIdentifiers = assignment.getAssignedIdentifiers();
		for (FreeIdentifier identifier : freeIdentifiers)
			assignedIdents.add(identifier);
	}

	public List<Assignment> getNondetAssignments() {
		return nondeterm;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pog.IEventActionTable#getActions()
	 */
	public List<ISCAction> getActions() {
		return actions;
	}
	
	public List<ISCAction> getNondetActions() {
		return nondetActions;
	}
	
	public List<ISCAction> getDetActions() {
		return detActions;
	}

}
