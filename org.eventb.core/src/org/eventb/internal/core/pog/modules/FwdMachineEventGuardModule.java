/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog.modules;

import java.util.List;

import org.eventb.core.ast.Predicate;
import org.eventb.core.pog.state.IAbstractEventGuardTable;

/**
 * @author Stefan Hallerstede
 *
 */
public class FwdMachineEventGuardModule extends MachineEventGuardModule {
	
	@Override
	protected boolean isRedundantWDProofObligation(Predicate predicate, int index) {
		
		List<IAbstractEventGuardTable> abstractEventGuardTables = 
			abstractEventGuardList.getAbstractEventGuardTables();
		
		for (IAbstractEventGuardTable abstractEventGuardTable : abstractEventGuardTables) {
		
			if (isFreshPOForAbstractGuard(predicate, index, abstractEventGuardTable))
				continue;
			
			return true;
		}
		return false;
	}

	private boolean isFreshPOForAbstractGuard(Predicate predicate, int index, IAbstractEventGuardTable abstractEventGuardTable) {
		int absIndex = abstractEventGuardTable.indexOfPredicate(predicate);

		if (absIndex == -1)
			return true;

		for (int k=0; k<absIndex; k++) {
		
			int indexOfConcrete = abstractEventGuardTable.getIndexOfCorrespondingConcrete(k);
		
			if (indexOfConcrete != -1 && indexOfConcrete < index)
				continue;
		
			return true;
		}
		return false;
	}

	@Override
	protected boolean isApplicable() {
		return !machineInfo.isInitialMachine();
	}


}
