/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pog;

import java.util.ArrayList;
import java.util.Set;

import org.eventb.core.ISCAction;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.sc.IState;

/**
 * @author Stefan Hallerstede
 *
 */
public interface IEventActionTable extends IState {

	ISCAction[] getActions();
	Assignment[] getAssignments();
	
	ArrayList<Assignment> getNondetAssignments();
	ArrayList<BecomesEqualTo> getDetAssignments();
	ArrayList<BecomesEqualTo> getPrimedDetAssignments();
	
	Set<FreeIdentifier> getAssignedVariables();
	
	ArrayList<ISCAction> getNondetActions();
	ArrayList<ISCAction> getDetActions();
}
