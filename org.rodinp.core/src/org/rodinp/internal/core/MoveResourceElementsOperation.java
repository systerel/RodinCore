/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * Strongly inspired by org.eclipse.jdt.internal.core.MoveResourceElementsOperation.java which is
 * 
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.internal.core;

import org.rodinp.core.IRodinElement;
import org.rodinp.internal.core.util.Messages;

/**
 * This operation moves resources (Rodin files) from their current container to
 * a specified destination container, optionally renaming the elements. A move
 * resource operation is equivalent to a copy resource operation, where the
 * source resources are deleted after the copy.
 * <p>
 * This operation can be used for reorganizing resources within the same
 * container.
 * 
 * @see CopyResourceElementsOperation
 */
public class MoveResourceElementsOperation extends CopyResourceElementsOperation {
	
	/**
	 * When executed, this operation will move the given elements to the given
	 * containers.
	 */
	public MoveResourceElementsOperation(IRodinElement[] elementsToMove,
			IRodinElement[] destContainers, boolean force) {
		super(elementsToMove, destContainers, force);
	}

	public MoveResourceElementsOperation(IRodinElement[] elementsToMove, boolean force) {
		super(elementsToMove, force);
	}

	public MoveResourceElementsOperation(IRodinElement elementToMove,
			IRodinElement destContainer, boolean force) {
		super(elementToMove, destContainer, force);
	}

	public MoveResourceElementsOperation(RodinFile elementToMove, boolean force) {
		super(elementToMove, force);
	}

	@Override
	protected String getMainTaskName() {
		return Messages.operation_moveResourceProgress;
	}

	@Override
	protected boolean isMove() {
		return true;
	}
}
