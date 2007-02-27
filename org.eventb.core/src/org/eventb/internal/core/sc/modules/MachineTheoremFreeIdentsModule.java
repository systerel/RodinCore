/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import org.eventb.core.EventBAttributes;
import org.eventb.core.EventBPlugin;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.SCCore;
import org.eventb.core.tool.IModuleType;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IRodinProblem;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineTheoremFreeIdentsModule extends
		MachineFormulaFreeIdentsModule {

	public static final IModuleType<MachineTheoremFreeIdentsModule> MODULE_TYPE = 
		SCCore.getModuleType(EventBPlugin.PLUGIN_ID + ".machineTheoremFreeIdentsModule"); //$NON-NLS-1$
	
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.core.sc.modules.FormulaFreeIdentsModule#declaredFreeIdentifierErrorMessage()
	 */
	@Override
	protected IRodinProblem declaredFreeIdentifierError() {
		return GraphProblem.TheoremFreeIdentifierError;
	}

	@Override
	protected IAttributeType.String getAttributeType() {
		return EventBAttributes.PREDICATE_ATTRIBUTE;
	}

}
