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
package org.rodinp.core.emf.lightcore.adapters;

import static org.rodinp.core.emf.lightcore.adapters.dboperations.OperationProcessor.submit;

import org.eclipse.emf.common.notify.Adapter;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.emf.lightcore.LightElement;
import org.rodinp.core.emf.lightcore.adapters.dboperations.ElementOperation;

/**
 * Processes Rodin Database deltas in order to refresh the implicit children for
 * the given root.
 */
public class ImplicitDeltaProcessor {

	private final LightElement root;
	private final IInternalElement rodinRoot;
	private final ImplicitDeltaRootAdapter owner;

	public ImplicitDeltaProcessor(Adapter owner, LightElement root) {
		this.owner = (ImplicitDeltaRootAdapter) owner;
		this.root = root;
		this.rodinRoot = (IInternalElement) root.getERodinElement();
	}

	/**
	 * Processes the delta recursively depending on the kind of the delta.
	 * 
	 * @param delta
	 *            The delta from the Rodin Database
	 */
	public void processDelta(final IRodinElementDelta delta)
			throws RodinDBException {
		if (traverseDelta(delta)) {
			submit(
					new ElementOperation.RecalculateImplicitElementOperation(
							delta.getElement(), root));
		}
	}
	
	public boolean traverseDelta(final IRodinElementDelta delta) {
		int kind = delta.getKind();
		final IRodinElement element = delta.getElement();
		// if the delta does not concern a modification in the project we
		// return;
		final IRodinProject rodinProject = element.getRodinProject();
		if (rodinProject != null
				&& !(rodinProject.equals(rodinRoot.getRodinProject()))) {
			return false;
		}
		if (kind == IRodinElementDelta.REMOVED) {
			if (element instanceof IRodinFile
					&& element.equals(rodinRoot.getRodinFile())) {
				// remove the machine from the model
				owner.finishListening();
				return false;
			}
		}
		if (kind == IRodinElementDelta.ADDED) {
			if (element instanceof IRodinDB || element instanceof IRodinProject
					|| element instanceof IRodinFile) {
				// not enough modification to modify implicit children
				return false;
			}
		}
		if (element instanceof IRodinDB || element instanceof IRodinProject) {
			boolean needRecalculate = false;
			for (IRodinElementDelta d : delta.getAffectedChildren()) {
				needRecalculate |= traverseDelta(d);
				if (needRecalculate)
					break;
			}
			return needRecalculate;
		}
		if (element instanceof IRodinFile) {
			// Can not filter delta at a finer grain
			return true;
		}
		return false;
	}

}
