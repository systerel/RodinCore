/*******************************************************************************
 * Copyright (c) 2007, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - refactored to use AbstractHandler
 *******************************************************************************/
package org.eventb.internal.ui.prooftreeui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.IPage;
import org.eventb.internal.ui.prooftreeui.ProofTreeUI;
import org.eventb.internal.ui.prooftreeui.ProofTreeUIPage;

/**
 * Implementation of the collapse all action in the proof tree
 * 
 * @author htson
 */
public class CollaspeAllHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final IWorkbenchPart part = HandlerUtil.getActivePart(event);
		if (part instanceof ProofTreeUI) {
			final IPage currentPage = ((ProofTreeUI) part).getCurrentPage();
			if (!(currentPage instanceof ProofTreeUIPage)) {
				return null;
			}
			((ProofTreeUIPage) currentPage).getViewer().collapseAll();
		}
		return null;
	}

}
