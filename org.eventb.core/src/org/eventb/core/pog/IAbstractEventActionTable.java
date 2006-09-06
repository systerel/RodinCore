/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pog;

import java.util.ArrayList;

import org.eventb.core.EventBPlugin;
import org.eventb.core.ISCAction;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BecomesEqualTo;

/**
 * @author Stefan Hallerstede
 *
 */
public interface IAbstractEventActionTable extends IEventActionTable {

	final static String STATE_TYPE = EventBPlugin.PLUGIN_ID + ".abstractEventActionInfo";
	
	ArrayList<BecomesEqualTo> getDisappearingWitnesses();
	ArrayList<Assignment> getSimAssignments();
	ArrayList<ISCAction> getSimActions();

}
