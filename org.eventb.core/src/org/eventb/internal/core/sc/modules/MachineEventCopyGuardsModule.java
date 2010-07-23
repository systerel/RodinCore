/*******************************************************************************
 * Copyright (c) 2008, 2010 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University of Southampton - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import org.eventb.core.EventBAttributes;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IEvent;
import org.eventb.core.ILabeledElement;
import org.eventb.core.ISCEvent;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.ILabelSymbolInfo;
import org.eventb.core.sc.state.SymbolFactory;
import org.eventb.core.tool.IModuleType;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 * 
 */
public class MachineEventCopyGuardsModule extends
		MachineEventCopyLabeledElementsModule {

	public static final IModuleType<MachineEventCopyGuardsModule> MODULE_TYPE = SCCore
			.getModuleType(EventBPlugin.PLUGIN_ID
					+ ".machineEventCopyGuardsModule"); //$NON-NLS-1$

	@Override
	protected ILabeledElement[] getSCElements(ISCEvent scEvent)
			throws RodinDBException {
		ILabeledElement[] scElements = scEvent.getSCGuards();
		return scElements;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.core.tool.types.IModule#getModuleType()
	 */
	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	@Override
	protected boolean copyNeeded() {
		return !concreteEventInfo.isInitialisation();
	}

	@Override
	protected ILabelSymbolInfo makeLabelSymbolInfo(String label, IEvent event,
			String component) {
		return SymbolFactory.getInstance().makeImportedGuard(label, true,
				event,
				EventBAttributes.EXTENDED_ATTRIBUTE, component);
	}

}
