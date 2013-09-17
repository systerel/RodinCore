/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.pog.state;

import org.eventb.core.EventBPlugin;
import org.eventb.core.ISCAxiom;
import org.eventb.core.pog.POGCore;
import org.eventb.core.tool.IStateType;

/**
 * Common protocol for accessing all axioms of a context.
 *
 * @author Stefan Hallerstede
 *
 * @since 1.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IContextAxiomTable extends IPredicateTable<ISCAxiom> {

	final static IStateType<IContextAxiomTable> STATE_TYPE = 
		POGCore.getToolStateType(EventBPlugin.PLUGIN_ID + ".contextAxiomTable");

}
