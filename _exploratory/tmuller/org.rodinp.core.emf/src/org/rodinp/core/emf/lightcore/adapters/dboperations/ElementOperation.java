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
package org.rodinp.core.emf.lightcore.adapters.dboperations;

import static org.rodinp.core.emf.lightcore.sync.SynchroUtils.addParentEContentAdapter;
import static org.rodinp.core.emf.lightcore.sync.SynchroUtils.findElement;
import static org.rodinp.core.emf.lightcore.sync.SynchroUtils.getPositionAmongSiblings;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.NotificationImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.emf.lightcore.ImplicitElement;
import org.rodinp.core.emf.lightcore.InternalElement;
import org.rodinp.core.emf.lightcore.LightElement;
import org.rodinp.core.emf.lightcore.LightcoreFactory;
import org.rodinp.core.emf.lightcore.LightcorePackage;
import org.rodinp.core.emf.lightcore.sync.SynchroManager;
import org.rodinp.core.emf.lightcore.sync.SynchroUtils;

public abstract class ElementOperation {

	public static enum ElementOperationType {

		ADDITION(Messages.elementOperationType_addition_type), //
		REORDER(Messages.elementOperationType_reorder_type), //
		RELOAD_ATTRIBUTES(Messages.elementOperationType_reload_type), //
		REMOVE(Messages.elementOperationType_remove_type), //
		RECALCULATE_IMPLICIT_CHILDREN(
				Messages.elementOperationType_recalculateImplicit_type), //
		;

		private final String description;

		ElementOperationType(String description) {
			this.description = description;
		}

		public String getKind() {
			return description;
		}

	}

	/**
	 * Performs the recursive addition.
	 */
	public static class AddElementOperation extends ElementOperation {

		public AddElementOperation(IRodinElement element, LightElement root) {
			super(ElementOperationType.ADDITION, element, root);
		}

		@Override
		public void perform() {
			if (!(element instanceof IInternalElement))
				return;
			final InternalElement el = LightcoreFactory.eINSTANCE
					.createInternalElement();
			el.setERodinElement(element);
			el.setReference(element.getElementName() + "["
					+ element.getElementType().getName() + "]");
			el.setEIsRoot(element.isRoot());
			el.setERoot(root);
			el.load();
			final IRodinElement parent = element.getParent();
			try {
				if (parent instanceof IInternalElement) {
					final LightElement eParent = findElement(parent, root);
					if (eParent != null) {
						int pos;
						pos = getPositionAmongSiblings(eParent,
								(IInternalElement) element);
						eParent.addElement(el, pos);
						addParentEContentAdapter(eParent, el);
					}
				}
				for (IRodinElement child : ((IInternalElement) element)
						.getChildren()) {
					new AddElementOperation(child, root).perform();
				}
			} catch (RodinDBException e) {
				Messages.dbOperationError(e.getMessage());
			}
		}

	}

	public static class ReorderElementOperation extends ElementOperation {

		public ReorderElementOperation(IRodinElement element, LightElement root) {
			super(ElementOperationType.REORDER, element, root);
		}

		public void perform() {
			final IRodinElement parent = element.getParent();
			final LightElement toMove = findElement(element, root);
			if (toMove == null)
				return;
			final LightElement eParent = toMove.getEParent();
			if (parent instanceof IInternalElement && eParent != null) {
				final int i = SynchroUtils.getPosFromNextSiblingPos(toMove,
						eParent);
				eParent.getEChildren().move(i, toMove);
			}
		}

	}

	public static class ReloadAttributesElementOperation extends
			ElementOperation {

		public ReloadAttributesElementOperation(IRodinElement element,
				LightElement root) {
			super(ElementOperationType.RELOAD_ATTRIBUTES, element, root);
		}

		@Override
		public void perform() {
			final LightElement found = findElement(element, root);
			if (element instanceof IInternalElement && found != null)
				SynchroUtils
						.reloadAttributes((IInternalElement) element, found);
		}

	}

	public static class RemoveElementOperation extends ElementOperation {

		public RemoveElementOperation(IRodinElement element, LightElement root) {
			super(ElementOperationType.REMOVE, element, root);
		}

		@Override
		public void perform() {
			LightElement found = findElement(element, root);
			if (found != null) {
				if (found.isEIsRoot()) {
					found.delete();
					return;
				}
				// removes the element from the children of its parent
				final LightElement parent = found.getEParent();
				if (parent != null) {
					parent.getEChildren().remove(found);
				}
			}
			found = null;
		}
	}

	public static class RecalculateImplicitElementOperation extends
			ElementOperation {

		public RecalculateImplicitElementOperation(IRodinElement element,
				LightElement root) {
			super(ElementOperationType.RECALCULATE_IMPLICIT_CHILDREN, element,
					root);
		}

		public void perform() {
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
			// send a notification to update the root
			final ImplicitElement implicitStub = LightcoreFactory.eINSTANCE
					.createImplicitElement();
			root.eNotify(new NotificationImpl(Notification.ADD, null,
					implicitStub));
		}

		private void recursiveImplicitLoadFromRoot() {
			try {
				recursiveImplicitLoad(root);
			} catch (RodinDBException e) {
				Messages.implicitCreationOperationError(root.getElement()
						.toString(), e.getMessage());
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

	protected final LightElement root;
	protected final IRodinElement element;
	private final ElementOperationType type;

	public ElementOperation(ElementOperationType type, IRodinElement element,
			LightElement root) {
		this.root = root;
		this.element = element;
		this.type = type;
	}

	public abstract void perform();

	public String getType() {
		return type.getKind();
	}

	public IRodinElement getElement() {
		return element;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((element == null) ? 0 : element.hashCode());
		result = prime * result + ((root == null) ? 0 : root.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ElementOperation other = (ElementOperation) obj;
		if (element == null) {
			if (other.element != null)
				return false;
		} else if (!element.equals(other.element))
			return false;
		if (root == null) {
			if (other.root != null)
				return false;
		} else if (!root.equals(other.root))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

}
