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

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRefinementParticipant;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;
import org.rodinp.internal.core.RefinementRegistry.RefinementException;

/**
 * @author Nicolas Beauger
 * 
 */
public class RefinementProcessor {

	private final IInternalElement sourceRoot;

	public RefinementProcessor(IInternalElement sourceRoot) {
		this.sourceRoot = sourceRoot;
	}

	public IInternalElement refine(String refinedName, IProgressMonitor monitor) {
		final IInternalElementType<? extends IInternalElement> rootType = sourceRoot
				.getElementType();
		try {
			final List<IRefinementParticipant> participants = RefinementRegistry
					.getDefault().getRefinementParticipants(rootType);
			if (participants == null) {
				return null;
			}
			final IInternalElement refinedRoot = makeRefinedRoot(refinedName);
			for (IRefinementParticipant participant : participants) {
				try {
					participant.process(refinedRoot, sourceRoot);
				} catch(Exception e) {
					// TODO log
					e.printStackTrace();
					return null;
				}
			}
			refinedRoot.getRodinFile().save(monitor, false);
			return refinedRoot;
		} catch (RefinementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private IInternalElement makeRefinedRoot(String refinedName) {
		final IRodinProject project = sourceRoot.getRodinProject();
		final IInternalElement refinedRoot = project.getRodinFile(
				refinedName).getRoot();
		if (refinedRoot.exists()) {
			throw new IllegalArgumentException(
					"refinement target already exists: " + refinedName);
		}
		return refinedRoot;
	}
}
