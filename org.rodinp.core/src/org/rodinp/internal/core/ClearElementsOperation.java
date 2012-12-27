/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     ETH Zurich - adapted from org.eclipse.jdt.internal.core.DeleteElementsOperation
 *     Systerel - adapted from delete to clear
 *     Systerel - fixed flag for attribute change
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.rodinp.internal.core;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRegion;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;
import org.rodinp.internal.core.util.Messages;

/**
 * This operation clears a collection of elements. If an element does not exist,
 * it is ignored.
 * 
 * <p>
 * NOTE: This operation only clears elements that are Rodin files or internal.
 */
public class ClearElementsOperation extends MultiOperation {

	/**
	 * The elements this operation processes grouped by Rodin file
	 * 
	 * @see #processElements() Keys are Rodin files, values are
	 *      <code>IRegion</code>s of elements to be processed in each
	 *      compilation unit.
	 */
	protected Map<RodinFile, IRegion> childrenToClear;

	/**
	 * When executed, this operation will clear the given elements. The elements
	 * to clear cannot be <code>null</code> or empty, and must be contained
	 * within a Rodin file.
	 */
	protected ClearElementsOperation(IRodinElement[] elementsToClear,
			boolean force) {
		super(elementsToClear, force);
	}

	/**
	 * When executed, this operation will clear the given element. The element
	 * to clear cannot be <code>null</code> or empty, and must be contained
	 * within a Rodin file.
	 */
	public ClearElementsOperation(IRodinElement elementToClear, boolean force) {
		super(elementToClear, force);
	}

	/**
	 * @see MultiOperation
	 */
	@Override
	protected String getMainTaskName() {
		return Messages.operation_clearElementProgress;
	}

	@Override
	protected ISchedulingRule getSchedulingRule() {
		assert false;
		return null;
	}

	/**
	 * Groups the elements to be processed by their Rodin file. If parent/child
	 * combinations are present, children are discarded (only the parents are
	 * processed). Removes any duplicates specified in elements to be processed.
	 */
	protected void groupElements() throws RodinDBException {
		childrenToClear = new HashMap<RodinFile, IRegion>(1);
		for (IRodinElement element : elementsToProcess) {
			final RodinFile rf;
			if (element instanceof InternalElement) {
				rf = ((InternalElement) element).getRodinFile();
			} else if (element instanceof RodinFile) {
				rf = (RodinFile) element;
			} else {
				error(IRodinDBStatusConstants.INVALID_ELEMENT_TYPES, element);
				return;
			}
			IRegion region = childrenToClear.get(rf);
			if (region == null) {
				region = new Region();
				childrenToClear.put(rf, region);
			}
			region.add(element);
		}
		IRodinElement[] model = new IRodinElement[childrenToClear.size()];
		elementsToProcess = childrenToClear.keySet().toArray(model);
	}

	/**
	 * Clears all requested elements from the given Rodin file
	 * 
	 * @see MultiOperation
	 */
	@Override
	protected void processElement(IRodinElement element)
			throws RodinDBException {
		RodinFile rf = (RodinFile) element;

		RodinElementDelta delta = newRodinElementDelta();
		IRodinElement[] rfElements = childrenToClear.get(rf).getElements();
		for (int i = 0, length = rfElements.length; i < length; i++) {
			clearElement(rf, delta, (IInternalElement) rfElements[i]);
		}
		addDelta(delta);
	}

	private void clearElement(RodinFile rf, RodinElementDelta delta,
			IInternalElement e) throws RodinDBException {
		final RodinFileElementInfo fileInfo = (RodinFileElementInfo) rf
				.getElementInfo();
		clearElementAttributes(fileInfo, e, delta);
		clearElementChildren(fileInfo, e, delta);
	}

	private void clearElementAttributes(final RodinFileElementInfo fileInfo,
			IInternalElement e, RodinElementDelta delta) throws RodinDBException {
		final IAttributeType[] attrs = fileInfo.getAttributeTypes(e);
		for (final IAttributeType attr : attrs) {
			fileInfo.removeAttribute(e, attr);
		}
		if (attrs.length != 0) {
			delta.changed(e, RodinElementDelta.F_ATTRIBUTE);
		}
	}

	private void clearElementChildren(RodinFileElementInfo fileInfo,
			IInternalElement e, RodinElementDelta delta) throws RodinDBException {
		final IRodinElement[] children = fileInfo.clearChildren(e);
		for (final IRodinElement child : children) {
			delta.removed(child);
		}
	}

	/**
	 * @see MultiOperation This method first groups the elements by
	 *      <code>RodinFile</code>, and then processes the
	 *      <code>RodinFile</code>s.
	 */
	@Override
	protected void processElements() throws RodinDBException {
		groupElements();
		super.processElements();
	}

	/**
	 * @see MultiOperation
	 */
	@Override
	protected void verify(IRodinElement element) throws RodinDBException {
		for (IRodinElement child : childrenToClear.get(element).getElements()) {
			if (! (child instanceof IInternalElement))
				error(IRodinDBStatusConstants.INVALID_ELEMENT_TYPES, child);

			if (child.isReadOnly())
				error(IRodinDBStatusConstants.READ_ONLY, child);
		}
	}
}
