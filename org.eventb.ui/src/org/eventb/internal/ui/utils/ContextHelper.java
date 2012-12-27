/*******************************************************************************
 * Copyright (c) 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.utils;

import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;

/**
 * Helper class to manage editor contexts.
 * 
 * @author Thomas Muller
 */
public class ContextHelper {

	private final IContextService service;
	private final String contextId;
	private IContextActivation activation;

	private ContextHelper(IWorkbenchSite site, String contextId) {
		this.service = (IContextService) site.getService(IContextService.class);
		this.contextId = contextId;
	}

	public static ContextHelper activateContext(IWorkbenchSite site,
			String contextId) {
		final ContextHelper instance = new ContextHelper(site, contextId);
		instance.activateContext();
		return instance;
	}

	public void activateContext() {
		activation = service.activateContext(contextId);
	}

	public void disableContext() {
		service.deactivateContext(activation);
	}

}