/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.tool;

import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eventb.internal.core.tool.types.IModule;
import org.eventb.internal.core.tool.types.IProcessorModule;
import org.rodinp.core.IFileElementType;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;

/**
 * @author Stefan Hallerstede
 *
 */
public class RootModuleDesc<T extends IProcessorModule> extends ProcessorModuleDesc<T> {

	@Override
	public void addToModuleFactory(
			ModuleFactory factory, 
			Map<String, ModuleDesc<? extends IModule>> modules) {
		factory.addRootToFactory(getElementType(), this);
	}

	private final IFileElementType<? extends IRodinFile> fileElementType;
	
	public RootModuleDesc(IConfigurationElement configElement) {
		super(configElement);
		String fetId = configElement.getAttribute("input");
		fileElementType = RodinCore.getFileElementType(fetId);
	}
	
	public IFileElementType<? extends IRodinFile> getElementType() {
		return fileElementType;
	}

}
