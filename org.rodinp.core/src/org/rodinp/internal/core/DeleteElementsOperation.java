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
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.rodinp.internal.core;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.rodinp.core.IRegion;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;
import org.rodinp.internal.core.util.Messages;

/**
 * This operation deletes a collection of elements (and
 * all of their children).
 * If an element does not exist, it is ignored.
 *
 * <p>NOTE: This operation only deletes elements contained within leaf resources -
 * that is, elements within Rodin files. To delete a Rodin file or
 * a folder, etc (which have an actual resource), a DeleteResourcesOperation
 * should be used.
 */
public class DeleteElementsOperation extends MultiOperation {
	
	/**
	 * The elements this operation processes grouped by Rodin file
	 * @see #processElements()
	 * Keys are compilation units,
	 * values are <code>IRegion</code>s of elements to be processed in each
	 * compilation unit.
	 */ 
	protected Map<RodinFile, IRegion> childrenToRemove;

	/**
	 * When executed, this operation will delete the given elements. The elements
	 * to delete cannot be <code>null</code> or empty, and must be contained within a
	 * compilation unit.
	 */
	protected DeleteElementsOperation(IRodinElement[] elementsToDelete, boolean force) {
		super(elementsToDelete, force);
	}
	
	/**
	 * When executed, this operation will delete the given element. The element
	 * to delete cannot be <code>null</code> or empty, and must be contained within a
	 * compilation unit.
	 */
	public DeleteElementsOperation(IRodinElement elementToDelete, boolean force) {
		super(elementToDelete, force);
	}
	
	private void deleteElement(IRodinElement elementToRemove, RodinFile rodinFile) throws RodinDBException {
		RodinFileElementInfo fileInfo = (RodinFileElementInfo) rodinFile.getElementInfo();
		fileInfo.delete((InternalElement) elementToRemove);
	}

	/**
	 * @see MultiOperation
	 */
	@Override
	protected String getMainTaskName() {
		return Messages.operation_deleteElementProgress; 
	}
	
	@Override
	protected ISchedulingRule getSchedulingRule() {
		assert false;
		return null;
	}
	
	/**
	 * Groups the elements to be processed by their Rodin file.
	 * If parent/child combinations are present, children are
	 * discarded (only the parents are processed). Removes any
	 * duplicates specified in elements to be processed.
	 */
	protected void groupElements() throws RodinDBException {
		childrenToRemove = new LinkedHashMap<RodinFile, IRegion>(1);
		for (IRodinElement element: elementsToProcess) {
			if (! (element instanceof InternalElement)) {
				error(IRodinDBStatusConstants.INVALID_ELEMENT_TYPES, element);
			} else {
				RodinFile rf = ((InternalElement) element).getRodinFile();
				IRegion region = childrenToRemove.get(rf);
				if (region == null) {
					region = new Region();
					childrenToRemove.put(rf, region);
				}
				region.add(element);
			}
		}
		IRodinElement[] model = new IRodinElement[childrenToRemove.size()];
		elementsToProcess = childrenToRemove.keySet().toArray(model);
	}
	
	/**
	 * Deletes all requested element from the given Rodin file
	 * @see MultiOperation
	 */
	@Override
	protected void processElement(IRodinElement element) throws RodinDBException {
		RodinFile rf = (RodinFile) element;
	
		RodinElementDelta delta = newRodinElementDelta();
		IRodinElement[] rfElements = childrenToRemove.get(rf).getElements();
		for (int i = 0, length = rfElements.length; i < length; i++) {
			IRodinElement e = rfElements[i];
			if (e.exists()) {
				deleteElement(e, rf);
				delta.removed(e);
			}
		}
		addDelta(delta);
	}
	
	/**
	 * @see MultiOperation
	 * This method first group the elements by <code>RodinFile</code>,
	 * and then processes the <code>RodinFile</code>.
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
		for (IRodinElement child: childrenToRemove.get(element).getElements()) {
			if (child.getCorrespondingResource() != null)
				error(IRodinDBStatusConstants.INVALID_ELEMENT_TYPES, child);

			if (child.isReadOnly())
				error(IRodinDBStatusConstants.READ_ONLY, child);
			
			if (child.isRoot()) {
				error(IRodinDBStatusConstants.ROOT_ELEMENT, child);
			}
		}
	}
}
