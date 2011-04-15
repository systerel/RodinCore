/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRefinementParticipant;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;
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

	public IInternalElement refine(String refinedName, IProgressMonitor monitor)
			throws RodinDBException {
		final IInternalElementType<? extends IInternalElement> rootType = sourceRoot
				.getElementType();
		try {
			final List<IRefinementParticipant> participants = RefinementRegistry
					.getDefault().getRefinementParticipants(rootType);
			if (participants == null || participants.isEmpty()) {
				return null;
			}
			final IInternalElement refinedRoot = makeRefinedRoot(refinedName);
			final SubMonitor subMon = convert(monitor, participants.size());
			for (IRefinementParticipant participant : participants) {
				if (subMon.isCanceled())
					return null;
				participant
						.process(refinedRoot, sourceRoot, subMon.newChild(1));
			}
			refinedRoot.getRodinFile().save(monitor, false);
			return refinedRoot;
		} catch (RefinementException e) {
			Util.log(e, "while refining "
					+ sourceRoot.getRodinFile().getElementName() + " to "
					+ refinedName);
			return null;
		}
	}

	private IInternalElement makeRefinedRoot(String refinedName) {
		final IRodinProject project = sourceRoot.getRodinProject();
		final IRodinFile rodinFile = project.getRodinFile(refinedName);
		if (rodinFile == null) {
			throw new IllegalArgumentException(
					"Illegal file name, possibly wrong extension: "
							+ refinedName);
		}
		if (rodinFile.exists()) {
			throw new IllegalArgumentException(
					"refinement target already exists: " + refinedName);
		}
		return rodinFile.getRoot();
	}
}
