/*******************************************************************************
 * Copyright (c) 2008, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.proofSkeletonView.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eventb.internal.ui.proofSkeletonView.ProofSkeletonView;

/**
 * Switch Orientation Action. If master part and details part are currently laid
 * out side by side, they become laid out top to bottom, and conversely.
 * 
 * @author Nicolas Beauger
 * 
 */
public class SwitchOrientationHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final IWorkbenchPart part = HandlerUtil.getActivePart(event);
		if (part instanceof ProofSkeletonView) {
			((ProofSkeletonView) part).switchOrientation();
		}
		return null;
	}

}
