/*******************************************************************************
 * Copyright (c) 2018 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.refinement;

import static org.eventb.core.EventBAttributes.GENERATED_ATTRIBUTE;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRefinementParticipant;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * Common code for the refine/extend implementations.
 *
 * @author Laurent Voisin
 */
public abstract class AbstractRefine implements IRefinementParticipant {

	protected static void removeGenerated(IInternalElement element, IProgressMonitor monitor) throws RodinDBException {
		element.removeAttribute(GENERATED_ATTRIBUTE, monitor);
		final IRodinElement[] children = element.getChildren();
		for (final IRodinElement child : children) {
			removeGenerated((IInternalElement) child, monitor);
		}
	}

}
