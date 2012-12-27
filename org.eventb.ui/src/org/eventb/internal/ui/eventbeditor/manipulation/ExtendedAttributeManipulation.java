/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - replaced inherited by extended
 *     Systerel - fully refactored the setValue() method
 *     Systerel - added history support
 *     Systerel - made IAttributeFactory generic
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.manipulation;

import static org.eventb.core.EventBAttributes.EXTENDED_ATTRIBUTE;
import static org.eventb.internal.ui.EventBUtils.getFreeChildName;
import static org.eventb.internal.ui.EventBUtils.getImplicitChildren;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IEvent;
import org.eventb.core.IRefinesEvent;
import org.eventb.internal.ui.utils.Messages;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         An implementation of {@link IAttributeManipulation} providing the factory
 *         methods for extended attribute of events.
 */
public class ExtendedAttributeManipulation extends
		AbstractAttributeManipulation {

	private static final class SetExtended implements IWorkspaceRunnable {

		private final IEvent event;

		public SetExtended(IEvent event) {
			this.event = event;
		}

		@Override
		public void run(IProgressMonitor pMonitor) throws RodinDBException {
			event.setExtended(true, pMonitor);
			final IInternalElement[] implicitChildren = getImplicitChildren(event);
			if (implicitChildren.length == 0) {
				return;
			}
			removeImplicitChildren(implicitChildren);
		}

		private void removeImplicitChildren(IInternalElement[] implicitChildren)
				throws RodinDBException {
			final IRodinElement[] children = event.getChildren();
			for (IRodinElement child : children) {
				removeIfDuplicate((IInternalElement) child, implicitChildren);
			}
		}

		private void removeIfDuplicate(IInternalElement child,
				IInternalElement[] implicitChildren) throws RodinDBException {
			for (IInternalElement implicit : implicitChildren) {
				if (child.getElementType() == implicit.getElementType()
						&& child.hasSameAttributes(implicit)
						&& child.hasSameChildren(implicit)) {
					child.delete(false, null);
				}
			}
		}

	}

	private static final class UnsetExtended implements IWorkspaceRunnable {

		private final IEvent event;

		public UnsetExtended(IEvent event) {
			this.event = event;
		}

		@Override
		public void run(IProgressMonitor pMonitor) throws RodinDBException {
			final IInternalElement[] implicitChildren = getImplicitChildren(event);
			event.setExtended(false, pMonitor);
			if (implicitChildren.length == 0) {
				return;
			}
			insertImplicitChildren(implicitChildren);
		}

		private void insertImplicitChildren(
				final IInternalElement[] implicitChildren)
				throws RodinDBException {
			final IRodinElement firstChild = getFirstChild();
			for (IInternalElement implicit : implicitChildren) {
				final String name = getFreshName(implicit);
				implicit.copy(event, firstChild, name, false, null);
			}
		}

		private IRodinElement getFirstChild() throws RodinDBException {
			for (IRodinElement child : event.getChildren()) {
				if (child.getElementType() != IRefinesEvent.ELEMENT_TYPE) {
					return child;
				}
			}
			return null;
		}

		private String getFreshName(IInternalElement implicit)
				throws RodinDBException {
			final IInternalElementType<?> type = implicit.getElementType();
			final String name = implicit.getElementName();
			if (event.getInternalElement(type, name).exists()) {
				return getFreeChildName(event, type, "internal"); //$NON-NLS-1$
			}
			return name;
		}

	}

	/**
	 * Constant string for TRUE (i.e. extended).
	 */
	private static final String TRUE = Messages.attributeManipulation_extended_true;

	/**
	 * Constant string for FALSE (i.e. non-extended).
	 */
	private static final String FALSE = Messages.attributeManipulation_extended_false;
	
	private IEvent asEvent(IRodinElement element) {
		assert element instanceof IEvent;
		return (IEvent) element;
	}
	
	@Override
	public String getValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		final IEvent event = asEvent(element);
		return (event.hasExtended() && event.isExtended()) ? TRUE : FALSE;
	}

	/**
	 * Sets the value of the extended attribute according to the input string
	 * representation. The new extended value is <code>true</code> if the
	 * input string is {@link #TRUE} and the value is <code>false</code>
	 * otherwise. The extended value is changed only if the extended attribute
	 * did not exist before or the old value is different from the new value. If
	 * the value is changed, the event also changes accordingly:
	 * <ul>
	 * <li>If the new value is <code>true</code>, i.e. the event becomes
	 * extended, then the parameters, guards and actions of the abstraction are
	 * removed from the extending event.</li>
	 * <li>If the new value is <code>false</code>, i.e. the event becomes
	 * non-extended, then the children of the abstract event(s) that are
	 * extended and which were implicitly there are copied within the given
	 * event.</li>
	 * </ul>
	 */
	@Override
	public void setValue(IRodinElement element, String newValue,
			IProgressMonitor monitor) throws RodinDBException {
		final boolean extended = newValue.equals(TRUE);
		final IEvent event = asEvent(element);
		if (extended) {
			final SetExtended setExtended = new SetExtended(event);
			setExtended.run(monitor);
		} else {
			final UnsetExtended unsetExtended = new UnsetExtended(event);
			unsetExtended.run(monitor);
		}
	}

	@Override
	public String[] getPossibleValues(IRodinElement element, IProgressMonitor monitor) {
		return new String[] { FALSE, TRUE };
	}

	@Override
	public void removeAttribute(IRodinElement element,
			IProgressMonitor monitor) throws RodinDBException {
		asEvent(element).removeAttribute(EXTENDED_ATTRIBUTE, monitor);
	}

	/**
	 * Default value for extended attribute is <code>false</code>, i.e.
	 * non-extended.
	 */
	@Override
	public void setDefaultValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		asEvent(element).setExtended(false, monitor);
	}

	@Override
	public boolean hasValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return asEvent(element).hasExtended();
	}
	
}
