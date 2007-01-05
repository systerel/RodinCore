/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.projectexplorer;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eventb.ui.EventBUIPlugin;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This class provide an object for immediate group of variables,
 *         invariants, etc. in the tree of the Project Explorer.
 */
public class TreeNode<T extends IInternalElement> {

	// Name of the node
	private String name;

	// The parent of the node
	private IParent parent;

	// The type of the children of this node (IVariable, IInvariant, etc.)
	private IInternalElementType<T> childrenType;

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param name
	 *            Name of the node
	 * @param parent
	 *            The parent of the node
	 * @param type
	 *            The type of the children
	 */
	public TreeNode(String name, IParent parent, IInternalElementType<T> type) {
		this.name = name;
		this.parent = parent;
		childrenType = type;
	}

	/**
	 * Return the parent of this node
	 * <p>
	 * 
	 * @return An IParent
	 */
	public IParent getParent() {
		return parent;
	}

	/**
	 * Return the children of this node (the children of the parent with the
	 * specified type).
	 * <p>
	 * 
	 * @return An array of IRodinElement.
	 */
	public IRodinElement[] getChildren() {
		try {
			return parent.getChildrenOfType(childrenType);
		} catch (RodinDBException e) {
			return new IRodinElement[0];
		}
	}

	/**
	 * Check if the node has any children.
	 * <p>
	 * 
	 * @return <code>true</code> if the parent has children of the specified
	 *         type.
	 */
	public boolean hasChildren() {
		try {
			return (parent.getChildrenOfType(childrenType).length != 0);
		}
		catch (RodinDBException e) {
			// TODO Exception handle
			e.printStackTrace();
			MessageDialog
					.openWarning(
							EventBUIPlugin.getActiveWorkbenchShell(),
							"Resource out of date",
							"Component "
									+ parent.toString()
									+ " is out of date with the file system and will be refresh.");
			ProjectExplorerActionGroup.refreshAction.refreshAll();
		}
		return false;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return name;
	}

	/**
	 * Check the type of the children.
	 * <p>
	 * 
	 * @param type
	 *            A string represent the type
	 * @return <code>true</code> if the children of the node has the specified
	 *         type
	 */
	public boolean isType(IInternalElementType type) {
		return childrenType == type;
	}

}