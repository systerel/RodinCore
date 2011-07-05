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
import static org.rodinp.core.emf.lightcore.sync.SynchroUtils.addParentEContentAdapter;
import static org.rodinp.core.emf.lightcore.sync.SynchroUtils.findElement;
import static org.rodinp.core.emf.lightcore.sync.SynchroUtils.getPositionAmongSiblings;

import org.eclipse.emf.common.notify.Adapter;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.emf.lightcore.InternalElement;
import org.rodinp.core.emf.lightcore.LightElement;
import org.rodinp.core.emf.lightcore.LightcoreFactory;
import org.rodinp.core.emf.lightcore.sync.SynchroUtils;

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
	public void processDelta(final IRodinElementDelta delta)
			throws RodinDBException {
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
				addElement(element);
			}
			return;
		}

		if (kind == IRodinElementDelta.REMOVED) {
			if (element instanceof IRodinFile) {
				final IRodinFile file = (IRodinFile) element;
				owner.finishListening();
				// remove the machine from the model
				removeElement(file.getRoot());
			} else if (element instanceof IInternalElement) {
				removeElement(element);
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
				reorderElement(element);
				return;
			}

			// FIXME not sure I should handle this
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
				reloadAttributes(element);
				return;
			}
		}
	}

	// recursive addition
	private void addElement(IRodinElement element) throws RodinDBException {
		if (!(element instanceof IInternalElement))
			return;
		final InternalElement e = LightcoreFactory.eINSTANCE
				.createInternalElement();
		e.setERodinElement(element);
		e.setReference(element.getElementName() + "["
				+ element.getElementType().getName() + "]");
		e.setEIsRoot(element.isRoot());
		e.setERoot(root);
		e.load();
		final IRodinElement parent = element.getParent();
		if (parent instanceof IInternalElement) {
			final LightElement eParent = findElement(parent, root);
			if (eParent != null) {
				final int pos = getPositionAmongSiblings(eParent,
						(IInternalElement) element);
				eParent.addElement(e, pos);
				addParentEContentAdapter(eParent, e);
			}
		}
		for (IRodinElement child : ((IInternalElement) element).getChildren()) {
			addElement(child);
		}
	}
	
	private void reorderElement(IRodinElement element) throws RodinDBException {
		final IRodinElement parent = element.getParent();
		final LightElement toMove = findElement(element, root);
		if (toMove == null)
			return;
		final LightElement eParent = toMove.getEParent();
		if (parent instanceof IInternalElement && eParent != null) {
			final int i = SynchroUtils.getPosFromNextSiblingPos(toMove, eParent);
			eParent.getEChildren().move(i, toMove);
		}
	}

	public void reloadElement(IRodinElement toRefresh) {
		final LightElement e = findElement(toRefresh, root);
		if (e != null)
			e.load();
	}

	public void reloadAttributes(IRodinElement toReload) {
		final LightElement found = findElement(toReload, root);
		if (toReload instanceof IInternalElement && found != null)
			SynchroUtils.reloadAttributes((IInternalElement) toReload, found);
	}

	public void removeElement(IRodinElement toRemove) {
		LightElement found = findElement(toRemove, root);
		if (found != null) {
			// removes the element from the children of its parent
			final LightElement parent = found.getEParent();
			if (parent != null) {
				parent.getEChildren().remove(found);
			}
		}
		found = null;
	}

}
