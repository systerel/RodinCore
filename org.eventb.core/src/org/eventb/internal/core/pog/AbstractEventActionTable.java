/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ISCAction;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.pog.state.IAbstractEventActionTable;
import org.eventb.core.pog.state.IConcreteEventActionTable;
import org.eventb.core.pog.state.IMachineVariableTable;
import org.eventb.core.tool.IStateType;

/**
 * @author Stefan Hallerstede
 *
 */
public class AbstractEventActionTable extends EventActionTable implements
		IAbstractEventActionTable {
	
	@Override
	public void makeImmutable() {
		super.makeImmutable();
		disappearingWitnesses = Collections.unmodifiableList(disappearingWitnesses);
		simAssignments = Collections.unmodifiableList(simAssignments);
		simActions = Collections.unmodifiableList(simActions);
	}

	private List<BecomesEqualTo> disappearingWitnesses;
	private List<Assignment> simAssignments;
	private List<ISCAction> simActions;
	
	private final Correspondence<Assignment> correspondence;

	public AbstractEventActionTable(
			ISCAction[] abstractActions, 
			ITypeEnvironment typeEnvironment, 
			IMachineVariableTable variables,
			IConcreteEventActionTable concreteTable,
			FormulaFactory factory) throws CoreException {
		super(abstractActions, typeEnvironment, factory);
		
		correspondence = new Correspondence<Assignment>(concreteTable.getAssignments(), assignments);
		
		disappearingWitnesses = new ArrayList<BecomesEqualTo>(abstractActions.length);
		simAssignments = new ArrayList<Assignment>(abstractActions.length);
		simActions = new ArrayList<ISCAction>(abstractActions.length);
		
		for (int i=0; i<assignments.size(); i++) {
			Assignment assignment = assignments.get(i);
			
			categorise(assignment, abstractActions[i], variables, factory);
					
		}

	}
	
	private void categorise(
			Assignment assignment, 
			ISCAction action, 
			IMachineVariableTable variables, 
			FormulaFactory factory) throws CoreException {
		if (assignment instanceof BecomesEqualTo) {
			BecomesEqualTo bet = (BecomesEqualTo) assignment;
			
			// analyse deterministic assignment
			FreeIdentifier[] lhs = bet.getAssignedIdentifiers();
			boolean[] preserved = new boolean[lhs.length];
			int pcount = 0;
			for (int k=0; k<lhs.length; k++) {
				if (variables.contains(lhs[k])) {
					pcount++;
					preserved[k] = true;
				}
			}
			
			if (pcount == lhs.length) {
				simAssignments.add(assignment);
				simActions.add(action);
			} else if (pcount == 0) {
				disappearingWitnesses.add(bet);
			} else {
			
				Expression[] rhs = bet.getExpressions();
			
				// split assignment
				FreeIdentifier[] lhsP = new FreeIdentifier[pcount];
				Expression[] rhsP = new Expression[pcount];
			
				final int dCount = lhs.length - pcount;
				FreeIdentifier[] lhsD = new FreeIdentifier[dCount];
				Expression[] rhsD = new Expression[dCount];
			
				int p = 0;
				int d = 0;
			
				for (int k=0; k<lhs.length; k++) {
					if (preserved[k]) {
						lhsP[p] = lhs[k];
						rhsP[p] = rhs[k];
						p++;
					} else {
						lhsD[d] = lhs[k];
						rhsD[d] = rhs[k];
						d++;
					}
				}
			
				simAssignments.add(factory.makeBecomesEqualTo(lhsP, rhsP, null));
				simActions.add(action);
				
				disappearingWitnesses.add(factory.makeBecomesEqualTo(lhsD, rhsD, null));
			
			}
			
		} else {
			simAssignments.add(assignment);
			simActions.add(action);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IState#getStateType()
	 */
	@Override
	public IStateType<?> getStateType() {
		return STATE_TYPE;
	}

	@Override
	public List<BecomesEqualTo> getDisappearingWitnesses() {
		return disappearingWitnesses;
	}

	@Override
	public List<Assignment> getSimAssignments() {
		return simAssignments;
	}

	@Override
	public List<ISCAction> getSimActions() {
		return simActions;
	}

	@Override
	public int getIndexOfCorrespondingAbstract(int index) {
		return correspondence.getIndexOfCorrespondingAbstract(index);
	}

	@Override
	public int getIndexOfCorrespondingConcrete(int index) {
		return correspondence.getIndexOfCorrespondingConcrete(index);
	}

}
