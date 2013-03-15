/*******************************************************************************
 * Copyright (c) 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     ETH Zurich - adapted from org.eclipse.jdt.core.ICompilationUnit
 *     Systerel - removed unnamed internal elements
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.rodinp.internal.core;

import static org.rodinp.core.IRodinDBStatusConstants.INVALID_CHILD_TYPE;

import java.util.HashMap;
import java.util.Map;

import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinDBStatus;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

/**
 * This class is used to perform operations on multiple <code>IRodinElement</code>.
 * It is responsible for running each operation in turn, collecting
 * the errors and merging the corresponding <code>RodinElementDelta</code>s.
 * <p>
 * If several errors occurred, they are collected in a multi-status
 * <code>RodinDBStatus</code>. Otherwise, a simple <code>RodinDBStatus</code>
 * is thrown.
 */
public abstract class MultiOperation extends RodinDBOperation {
	/**
	 * Table specifying insertion positions for elements being 
	 * copied/moved/renamed. Keyed by elements being processed, and
	 * values are the corresponding insertion point.
	 * @see #processElements()
	 */
	protected Map<IRodinElement, IRodinElement> insertBeforeElements =
		new HashMap<IRodinElement, IRodinElement>(1);

	/**
	 * Table specifying the new parent for elements being 
	 * copied/moved/renamed.
	 * Keyed by elements being processed, and
	 * values are the corresponding destination parent.
	 */
	private Map<IRodinElement, IRodinElement> newParents;
	
	/**
	 * This table presents the data in <code>renamingList</code> in a more
	 * convenient way.
	 */
	protected Map<IRodinElement, String> renamings;
	
	/**
	 * The list of renamings supplied to the operation
	 */
	protected String[] renamingsList = null;
	
	/**
	 * Creates a new <code>MultiOperation</code> on <code>elementsToProcess</code>.
	 */
	protected MultiOperation(IRodinElement[] elementsToProcess, boolean force) {
		super(elementsToProcess, force);
	}
	
	/**
	 * Creates a new <code>MultiOperation</code> on <code>elementToProcess</code>.
	 */
	protected MultiOperation(IRodinElement elementToProcess, boolean force) {
		super(elementToProcess, force);
	}
	
	/**
	 * Creates a new <code>MultiOperation</code>.
	 */
	protected MultiOperation(IRodinElement[] elementsToProcess, IRodinElement[] parentElements, boolean force) {
		super(elementsToProcess, parentElements, force);
		this.newParents = new HashMap<IRodinElement, IRodinElement>(elementsToProcess.length);
		if (elementsToProcess.length == parentElements.length) {
			for (int i = 0; i < elementsToProcess.length; i++) {
				this.newParents.put(elementsToProcess[i], parentElements[i]);
			}
		} else { //same destination for all elements to be moved/copied/renamed
			for (int i = 0; i < elementsToProcess.length; i++) {
				this.newParents.put(elementsToProcess[i], parentElements[0]);
			}
		}
	}

	public MultiOperation(IRodinElement elementToProcess, IRodinElement parentElement, boolean force) {
		super(elementToProcess, force);
		this.newParents = new HashMap<IRodinElement, IRodinElement>(1);
		this.newParents.put(elementToProcess, parentElement);
	}

	/**
	 * Convenience method to create a <code>RodinDBException</code>
	 * embedding a <code>RodinDBStatus</code>.
	 */
	protected void error(int code, IRodinElement element) throws RodinDBException {
		throw new RodinDBException(new RodinDBStatus(code, element));
	}

	/**
	 * Executes the operation.
	 *
	 * @exception RodinDBException if one or several errors occurred during the operation.
	 * If multiple errors occurred, the corresponding <code>RodinDBStatus</code> is a
	 * multi-status. Otherwise, it is a simple one.
	 */
	@Override
	protected void executeOperation() throws RodinDBException {
		processElements();
	}
	
	/**
	 * Returns the parent of the element being copied/moved/renamed.
	 */
	protected IRodinElement getDestinationParent(IRodinElement child) {
		if (this.newParents == null)
			return child.getParent();
		return this.newParents.get(child);
	}
	
	/**
	 * Returns the name to be used by the progress monitor.
	 */
	protected abstract String getMainTaskName();
	
	/**
	 * Returns the new name for <code>element</code>, or <code>null</code>
	 * if there are no renamings specified.
	 */
	protected String getNewNameFor(IRodinElement element) {
		String newName = null;
		if (this.renamings != null)
			newName = this.renamings.get(element);
		return newName;
	}

	/**
	 * Sets up the renamings hashtable - keys are the elements and
	 * values are the new name.
	 */
	private void initializeRenamings() {
		if (this.renamingsList != null && this.renamingsList.length == this.elementsToProcess.length) {
			this.renamings = new HashMap<IRodinElement, String>(this.renamingsList.length);
			for (int i = 0; i < this.renamingsList.length; i++) {
				if (this.renamingsList[i] != null) {
					this.renamings.put(this.elementsToProcess[i], this.renamingsList[i]);
				}
			}
		}
	}
	
	/**
	 * Returns <code>true</code> if this operation represents a move or rename, <code>false</code>
	 * if this operation represents a copy.<br>
	 * Note: a rename is just a move within the same parent with a name change.
	 */
	protected boolean isMove() {
		return false;
	}
	
	/**
	 * Returns <code>true</code> if this operation represents a rename, <code>false</code>
	 * if this operation represents a copy or move.
	 */
	protected boolean isRename() {
		return false;
	}
	
	/**
	 * Subclasses must implement this method to process a given <code>IRodinElement</code>.
	 */
	protected abstract void processElement(IRodinElement element) throws RodinDBException;
	
	/**
	 * Processes all the <code>IRodinElement</code>s in turn, collecting errors
	 * and updating the progress monitor.
	 *
	 * @exception RodinDBException if one or several operation(s) was unable to
	 * be completed.
	 */
	protected void processElements() throws RodinDBException {
		beginTask(getMainTaskName(), this.elementsToProcess.length);
		IRodinDBStatus[] errors = new IRodinDBStatus[3];
		int errorsCounter = 0;
		for (int i = 0; i < this.elementsToProcess.length; i++) {
			try {
				verify(this.elementsToProcess[i]);
				processElement(this.elementsToProcess[i]);
			} catch (RodinDBException rde) {
				if (errorsCounter == errors.length) {
					// resize
					System.arraycopy(errors, 0, (errors = new IRodinDBStatus[errorsCounter*2]), 0, errorsCounter);
				}
				errors[errorsCounter++] = rde.getRodinDBStatus();
			} finally {
				worked(1);
			}
		}
		done();
		if (errorsCounter == 1) {
			throw new RodinDBException(errors[0]);
		} else if (errorsCounter > 1) {
			if (errorsCounter != errors.length) {
				// resize
				System.arraycopy(errors, 0, (errors = new IRodinDBStatus[errorsCounter]), 0, errorsCounter);
			}
			throw new RodinDBException(RodinDBStatus.newMultiStatus(errors));
		}
	}
	
	/**
	 * Sets the insertion position in the new container for the modified element. The element
	 * being modified will be inserted before the specified new sibling. The given sibling
	 * must be a child of the destination container specified for the modified element.
	 * The default is <code>null</code>, which indicates that the element is to be
	 * inserted at the end of the container.
	 */
	public void setInsertBefore(IRodinElement modifiedElement, IRodinElement newSibling) {
		this.insertBeforeElements.put(modifiedElement, newSibling);
	}
	
	/**
	 * Sets the new names to use for each element being copied. The renamings
	 * correspond to the elements being processed, and the number of
	 * renamings must match the number of elements being processed.
	 * A <code>null</code> entry in the list indicates that an element
	 * is not to be renamed.
	 *
	 * <p>Note that some renamings may not be used.  If both a parent
	 * and a child have been selected for copy/move, only the parent
	 * is changed.  Therefore, if a new name is specified for the child,
	 * the child's name will not be changed.
	 */
	public void setRenamings(String[] renamingsList) {
		this.renamingsList = renamingsList;
		initializeRenamings();
	}
	
	/**
	 * This method is called for each <code>IRodinElement</code> before
	 * <code>processElement</code>. It should check that this <code>element</code>
	 * can be processed.
	 */
	protected abstract void verify(IRodinElement element) throws RodinDBException;
	
	/**
	 * Verifies that the <code>destination</code> specified for the <code>element</code> is valid
	 * for the types of the <code>element</code> and <code>destination</code>.
	 */
	protected void verifyDestination(IRodinElement element, IRodinElement destination) throws RodinDBException {
		if (destination == null || ! destination.exists())
			error(IRodinDBStatusConstants.ELEMENT_DOES_NOT_EXIST, destination);
		
		if (destination.isReadOnly())
			error(IRodinDBStatusConstants.READ_ONLY, destination);

		if (element instanceof RodinFile) {
			if (! (destination instanceof RodinProject)) {
				error(IRodinDBStatusConstants.INVALID_DESTINATION, destination);
			}
		} else if (element instanceof InternalElement) {
			if (! (destination instanceof InternalElement)) {
				error(IRodinDBStatusConstants.INVALID_DESTINATION, destination);
			}
			verifyChildType(((InternalElement) destination), ((InternalElement) element));
		} else {
			error(IRodinDBStatusConstants.INVALID_ELEMENT_TYPES, element);
		}
	}

	private void verifyChildType(InternalElement parent, InternalElement element)
			throws RodinDBException {
		final IInternalElementType<?> childType = element.getElementType();
		if (!parent.getElementType().canParent(childType)) {
			throw new RodinDBException(new RodinDBStatus(INVALID_CHILD_TYPE,
					parent, childType.getId()));
		}
	}

	/**
	 * Verify that the new name specified for <code>element</code> is
	 * valid for that type of Rodin element.
	 */
	protected void verifyRenaming(IRodinElement element) throws RodinDBException {
		if (element instanceof RodinFile) {
			String newName = getNewNameFor(element);
			ElementTypeManager mgr = ElementTypeManager.getInstance();
			if (! mgr.isValidFileName(newName)) {
				throw new RodinDBException(new RodinDBStatus(
						IRodinDBStatusConstants.INVALID_NAME, element, newName));
			}
		} else if (element instanceof InternalElement) {
			// There is currently no restriction on internal element names.
		} else {
			error(IRodinDBStatusConstants.INVALID_ELEMENT_TYPES, element);
		}
	}
	
	/**
	 * Verifies that the positioning sibling specified for the <code>element</code> exists and
	 * its parent is the destination container of this <code>element</code>.
	 */
	protected void verifySibling(IRodinElement element, IRodinElement destination) throws RodinDBException {
		IRodinElement insertBeforeElement = this.insertBeforeElements.get(element);
		if (insertBeforeElement != null) {
			if (!insertBeforeElement.exists() || !insertBeforeElement.getParent().equals(destination)) {
				error(IRodinDBStatusConstants.INVALID_SIBLING, insertBeforeElement);
			}
		}
	}
}
