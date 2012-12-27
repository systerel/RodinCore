/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.internal.core.tool;

import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.tool.types.IFilterModule;
import org.eventb.internal.core.tool.types.IModule;
import org.eventb.internal.core.tool.types.IProcessorModule;
import org.rodinp.core.IInternalElementType;

/**
 * @author Stefan Hallerstede
 *
 */
public interface IModuleFactory {
	
	IFilterModule[] getFilterModules(IModuleType<? extends IModule> parentType);
	
	IProcessorModule[] getProcessorModules(IModuleType<? extends IModule> parentType);
	
	IProcessorModule getRootModule(IInternalElementType<?> type);
	
	String printModuleTree(IInternalElementType<?> type);

}
