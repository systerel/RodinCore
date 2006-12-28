/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pog.state;

import java.util.List;
import java.util.Set;

import org.eventb.core.ISCAction;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.FreeIdentifier;

/**
 * @author Stefan Hallerstede
 *
 */
public interface IEventActionTable extends IPOGState {

	List<ISCAction> getActions();
	List<Assignment> getAssignments();
	
	List<Assignment> getNondetAssignments();
	List<BecomesEqualTo> getDetAssignments();
	List<BecomesEqualTo> getPrimedDetAssignments();
	
	Set<FreeIdentifier> getAssignedVariables();
	
	List<ISCAction> getNondetActions();
	List<ISCAction> getDetActions();
}
