/*******************************************************************************
 * Copyright (c) 2008, 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.core.emf.lightcore.adapters;

import static org.rodinp.core.emf.lightcore.LightCoreUtils.debug;
import static org.rodinp.core.emf.lightcore.adapters.dboperations.OperationProcessor.submit;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.emf.api.itf.ILElement;
import org.rodinp.core.emf.lightcore.LightElement;
import org.rodinp.core.emf.lightcore.adapters.dboperations.ElementOperation;

/**
 * Processes deltas from the database in order to update the EMF light core
 * model associated to the light root element.
 */
public class DeltaProcessor {

	public static boolean DEBUG;

	private final LightElement root;
	private final IRodinElement rodinRoot;
	private final DeltaRootAdapter owner;

	public DeltaProcessor(Adapter owner, LightElement root) {
		this.owner = (DeltaRootAdapter) owner;
		this.root = root;
		this.rodinRoot = (IRodinElement) root.getERodinElement();
	}

	/**
	 * Processes the delta recursively depending on the kind of the delta.
	 * 
	 * @param delta
	 *            The delta from the Rodin Database
	 */
	public void processDelta(final IRodinElementDelta delta) {
		int kind = delta.getKind();
		final IRodinElement element = delta.getElement();

		if (DEBUG)
			debug("Processing RodinDB delta for :" + element.getElementName()
					+ " :" + delta.toString());

		// if the delta does not concern the root handled by this processor
		if (element instanceof IInternalElement
				&& (!((IInternalElement) element).getRoot().equals(rodinRoot))) {
			return;
		}

		// if the element is from this root and doesn't exists in the EMF model
		if (kind == IRodinElementDelta.ADDED) {
			if (element instanceof IInternalElement) {
				submit(new ElementOperation.AddElementOperation(element, root));
			}
			return;
		}

		if (kind == IRodinElementDelta.REMOVED) {
			final Notifier target = owner.getTarget();
			if (target instanceof ILElement) {
				final IRodinFile rFile = ((ILElement) target).getElement()
						.getRodinFile();
				if (element instanceof IRodinFile && element.equals(rFile)) {
					owner.finishListening();
					// remove the machine from the model
					submit(new ElementOperation.RemoveElementOperation(rFile.getRoot(), root));
					return;
				}
			}
			if (element instanceof IInternalElement) {
				submit(new ElementOperation.RemoveElementOperation(element, root));
			}
			return;
		}

		int flags = delta.getFlags();

		if (kind == IRodinElementDelta.CHANGED) {

			if ((flags & IRodinElementDelta.F_CHILDREN) != 0) {
				final IRodinElementDelta[] deltas = delta.getAffectedChildren();
				for (IRodinElementDelta elems : deltas) {
					processDelta(elems);
				}
				return;
			}

			if ((flags & IRodinElementDelta.F_REORDERED) != 0) {
				submit(new ElementOperation.ReorderElementOperation(element, root));
				return;
			}

			if ((flags & IRodinElementDelta.F_CONTENT) != 0) {
				if (element instanceof IRodinFile) {
					final IRodinElementDelta[] deltas = delta
							.getAffectedChildren();
					for (IRodinElementDelta elems : deltas) {
						processDelta(elems);
					}
				}
				return;
			}

			if ((flags & IRodinElementDelta.F_ATTRIBUTE) != 0) {
				submit(new ElementOperation.ReloadAttributesElementOperation(element, root));
				return;
			}
		}
	}

}
