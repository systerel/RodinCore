/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     ETH Zurich - adapted from org.eclipse.jdt.internal.core.MoveElementsOperation
 *******************************************************************************/
package org.rodinp.internal.core;

import org.rodinp.core.IRodinElement;
import org.rodinp.internal.core.util.Messages;

/**
 * This operation moves internal elements from their current container to a
 * specified destination container, optionally renaming the elements. A move
 * operation is equivalent to a copy operation, where the source elements are
 * deleted after the copy.
 * <p>
 * This operation can be used for reorganizing elements within the same
 * container.
 * 
 * @see CopyElementsOperation
 */
public class MoveElementsOperation extends CopyElementsOperation {
	/**
	 * When executed, this operation will move the given elements to the given
	 * containers.
	 */
	public MoveElementsOperation(IRodinElement[] elementsToMove,
			IRodinElement[] destContainers, boolean force) {
		super(elementsToMove, destContainers, force);
	}

	public MoveElementsOperation(IRodinElement[] elementsToMove, boolean force) {
		super(elementsToMove, force);
	}

	public MoveElementsOperation(IRodinElement elementToMove,
			IRodinElement destContainer, boolean force) {
		super(elementToMove, destContainer, force);
	}

	public MoveElementsOperation(IRodinElement elementToMove, boolean force) {
		super(elementToMove, force);
	}

	@Override
	protected String getMainTaskName() {
		return Messages.operation_moveElementProgress;
	}

	@Override
	protected boolean isMove() {
		return true;
	}
}
