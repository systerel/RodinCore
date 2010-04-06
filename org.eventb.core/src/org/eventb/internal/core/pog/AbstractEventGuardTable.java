/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog;

import org.eventb.core.ISCGuard;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pog.state.IAbstractEventGuardTable;
import org.eventb.core.pog.state.IConcreteEventGuardTable;
import org.eventb.core.tool.IStateType;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class AbstractEventGuardTable extends EventGuardTable 
implements IAbstractEventGuardTable {
	
	private final Correspondence<Predicate> correspondence;
	
	public AbstractEventGuardTable(
			ISCGuard[] guards, 
			ITypeEnvironment typeEnvironment, 
			IConcreteEventGuardTable concreteTable,
			FormulaFactory factory) throws RodinDBException {
		super(guards, typeEnvironment, factory);
		
		correspondence = new Correspondence<Predicate>(concreteTable.getPredicates(), predicates);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IState#getStateType()
	 */
	public IStateType<?> getStateType() {
		return STATE_TYPE;
	}

	public int getIndexOfCorrespondingAbstract(int index) {
		return correspondence.getIndexOfCorrespondingAbstract(index);
	}

	public int getIndexOfCorrespondingConcrete(int index) {
		return correspondence.getIndexOfCorrespondingConcrete(index);
	}

}
