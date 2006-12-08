/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pog.state;

import java.util.List;

import org.eventb.core.EventBPlugin;
import org.eventb.core.ISCAction;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BecomesEqualTo;

/**
 * @author Stefan Hallerstede
 *
 */
public interface IAbstractEventActionTable extends IEventActionTable, ICorrespondence {

	final static String STATE_TYPE = EventBPlugin.PLUGIN_ID + ".abstractEventActionTable";
	
	List<BecomesEqualTo> getDisappearingWitnesses();
	List<Assignment> getSimAssignments();
	List<ISCAction> getSimActions();
	
}
