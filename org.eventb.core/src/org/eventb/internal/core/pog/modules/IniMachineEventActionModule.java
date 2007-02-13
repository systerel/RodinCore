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
import org.eventb.core.pog.util.POGPredicate;

/**
 * @author Stefan Hallerstede
 *
 */
public class IniMachineEventActionModule extends MachineEventActionModule {
	
	@Override
	protected boolean abstractHasNotSameAction(int k) {
		return true;
	}

	@Override
	protected List<POGPredicate> makeAbstractActionHypothesis(Predicate baPredicate) {
		
		return emptyPredicates;
	}

	@Override
	protected boolean isApplicable() {
		return machineInfo.isInitialMachine();
	}

}
