/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Systerel - Initial API and implementation
 *******************************************************************************/
package org.rodinp.core.emf.lightcore.adapters;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.NotificationImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.emf.lightcore.ImplicitElement;
import org.rodinp.core.emf.lightcore.LightElement;
import org.rodinp.core.emf.lightcore.LightcoreFactory;
import org.rodinp.core.emf.lightcore.LightcorePackage;
import org.rodinp.core.emf.lightcore.sync.SynchroManager;

/**
 * Processes Rodin Database deltas in order to refresh the implicit children for
 * the given root.
 */
public class ImplicitDeltaProcessor {

	private final LightElement root;
	private final IInternalElement rodinRoot;
	private final ImplicitDeltaRootAdapter owner;
	private IRodinElementDelta oldDelta = null;

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
		int kind = delta.getKind();
		final IRodinElement element = delta.getElement();

		if (kind == IRodinElementDelta.REMOVED) {
			if (element instanceof IRodinFile
					&& element.equals(rodinRoot.getRodinFile())) {
				// remove the machine from the model
				owner.finishListening();
				return;
			}
		}

		if (kind == IRodinElementDelta.ADDED) {
			if (element instanceof IRodinDB || element instanceof IRodinProject
					|| element instanceof IRodinFile) {
					//not enough modification to modify implicit children
					return;
			}
		}
		
		if (element instanceof IRodinDB || element instanceof IRodinProject
				|| element instanceof IRodinFile) {
			for (IRodinElementDelta d : delta.getAffectedChildren()) {
				processDelta(d);
			}
			return;
		}
		// if the delta does not concern a modification in the project we
		// return;
		final IRodinProject rodinProject = element.getRodinProject();
		if (rodinProject != null
				&& !(rodinProject.equals(rodinRoot.getRodinProject()))) {
			return;
		}
		recalculateImplicitChildren();
		if (delta != oldDelta) {
				final ImplicitElement implicitStub = LightcoreFactory.eINSTANCE.createImplicitElement();
				root.eNotify(new NotificationImpl(Notification.ADD, null, implicitStub));
				oldDelta = delta;
			}
		}

	private void recalculateImplicitChildren() {
		final EList<EObject> implicitChildren = root.getAllContained(
				LightcorePackage.Literals.IMPLICIT_ELEMENT, false);
		for (EObject child : implicitChildren) {
			if (child == null) {
				continue;
			}
			final EObject eContainer = child.eContainer();
			eContainer.eSetDeliver(false);
			if (child instanceof ImplicitElement)
				EcoreUtil.remove(child);
			eContainer.eSetDeliver(true);
		}
		recursiveImplicitLoadFromRoot();
	}

	private void recursiveImplicitLoadFromRoot() {
		try {
			recursiveImplicitLoad(root);
		} catch (RodinDBException e) {
			System.out
					.println("Could not create the implicit children of the UI"
							+ " model for the element " + rodinRoot.toString()
							+ " " + e.getMessage());
		}
	}

	private void recursiveImplicitLoad(LightElement element)
			throws RodinDBException {
		final Object rodinElement = element.getERodinElement();
		if (rodinElement instanceof IInternalElement) {
			final IInternalElement parent = (IInternalElement) rodinElement;
			SynchroManager.implicitLoad(element, parent);
		}
		for (LightElement eChild : element.getEChildren()) {
			if (!(eChild instanceof ImplicitElement)) {
				recursiveImplicitLoad(eChild);
			}
		}
	}

}
