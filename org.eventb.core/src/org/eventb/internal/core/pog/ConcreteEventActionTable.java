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
package org.eventb.internal.core.pog;

import java.util.Collection;
import java.util.LinkedList;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ISCAction;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.pog.state.IConcreteEventActionTable;
import org.eventb.core.pog.state.IMachineVariableTable;
import org.eventb.core.tool.IStateType;

/**
 * @author Stefan Hallerstede
 *
 */
public class ConcreteEventActionTable extends EventActionTable implements
		IConcreteEventActionTable {

	private final BecomesEqualTo xiUnprime;
	private final BecomesEqualTo deltaPrime;
	
	public ConcreteEventActionTable(
			ISCAction[] actions, 
			ITypeEnvironment typeEnvironment, 
			IMachineVariableTable variables,
			FormulaFactory factory) throws CoreException {
		super(actions, typeEnvironment, factory);
		
		Collection<FreeIdentifier> assignedVariables = getAssignedVariables();
		
		LinkedList<FreeIdentifier> xiLeft = new LinkedList<FreeIdentifier>();
		LinkedList<Expression> xiRight = new LinkedList<Expression>();
		LinkedList<FreeIdentifier> deltaLeft = new LinkedList<FreeIdentifier>();
		LinkedList<Expression> deltaRight = new LinkedList<Expression>();
		
		for (FreeIdentifier variable : variables.getVariables()) {
			if (assignedVariables.contains(variable)) {
				deltaLeft.add(variable);
				deltaRight.add(variable.withPrime(factory));
			} else {
				xiLeft.add(variable.withPrime(factory));
				xiRight.add(variable);
			}
		}
		
		if (xiLeft.size() == 0) {
			xiUnprime = null;
		} else {
			xiUnprime = factory.makeBecomesEqualTo(xiLeft, xiRight, null);
			xiUnprime.typeCheck(typeEnvironment);
		}
		if (deltaLeft.size() == 0) {
			deltaPrime = null;
		} else {
			deltaPrime = factory.makeBecomesEqualTo(deltaLeft, deltaRight, null);
			deltaPrime.typeCheck(typeEnvironment);
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
	public BecomesEqualTo getXiUnprime() {
		return xiUnprime;
	}

	@Override
	public BecomesEqualTo getDeltaPrime() {
		return deltaPrime;
	}

}
