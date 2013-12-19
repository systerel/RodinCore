/*******************************************************************************
 * Copyright (c) 2011, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core;

import static org.eclipse.core.runtime.SubMonitor.convert;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRefinementParticipant;
import org.rodinp.internal.core.RefinementRegistry.RefinementException;
import org.rodinp.internal.core.util.Util;

/**
 * @author Nicolas Beauger
 * 
 */
public class RefinementProcessor {

	private final IInternalElement sourceRoot;

	public RefinementProcessor(IInternalElement sourceRoot) {
		this.sourceRoot = sourceRoot;
	}

	public boolean refine(IInternalElement targetRoot, IProgressMonitor monitor)
			throws CoreException {
		final IInternalElementType<? extends IInternalElement> rootType = sourceRoot
				.getElementType();
		try {
			final List<IRefinementParticipant> participants = RefinementRegistry
					.getDefault().getRefinementParticipants(rootType);
			if (participants == null || participants.isEmpty()) {
				return false;
			}
			final IInternalElement refinedRoot = targetRoot;// makeRefinedRoot(targetRoot);
			final SubMonitor subMon = convert(monitor, participants.size());
			for (IRefinementParticipant participant : participants) {
				if (subMon.isCanceled())
					return false;
				participant
						.process(refinedRoot, sourceRoot, subMon.newChild(1));
			}
			return true;
		} catch (RefinementException e) {
			Util.log(e, "while refining "
					+ sourceRoot.getRodinFile().getElementName() + " to "
					+ targetRoot);
			return false;
		}
	}

}
