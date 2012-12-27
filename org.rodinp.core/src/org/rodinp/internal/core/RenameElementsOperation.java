/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     ETH Zurich - adapted from org.eclipse.jdt.internal.core.RenameElementsOperation
 *******************************************************************************/
package org.rodinp.internal.core;

import org.rodinp.core.IRodinDBStatus;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinElement;
import org.rodinp.internal.core.util.Messages;

/**
 * This operation renames elements.
 * <p>
 * Resource rename is not supported - this operation only renames elements
 * contained in Rodin files.
 * </p>
 */
public class RenameElementsOperation extends MoveElementsOperation {
	/**
	 * When executed, this operation will rename the specified elements with the
	 * given names.
	 */
	public RenameElementsOperation(IRodinElement[] elements, String[] newNames,
			boolean force) {
		super(elements, force);
		setRenamings(newNames);
	}

	public RenameElementsOperation(IRodinElement element, String newName,
			boolean force) {
		super(element, force);
		setRenamings(new String[] {newName});
	}

	@Override
	protected String getMainTaskName() {
		return Messages.operation_renameElementProgress;
	}

	@Override
	protected boolean isRename() {
		return true;
	}

	@Override
	protected IRodinDBStatus verify() {
		IRodinDBStatus status = super.verify();
		if (!status.isOK())
			return status;
		if (this.renamingsList == null || this.renamingsList.length == 0)
			return new RodinDBStatus(IRodinDBStatusConstants.NULL_NAME);
		return RodinDBStatus.VERIFIED_OK;
	}

}
