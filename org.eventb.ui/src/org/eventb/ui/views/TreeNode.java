/*******************************************************************************
 * Copyright (c) 2005 ETH-Zurich
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH RODIN Group
 *******************************************************************************/

package org.eventb.ui.views;

import org.eventb.ui.Utils;
import org.rodinp.core.IParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 * <p>
 * This class provide an object for immediate group of variables, invariants, etc. in the
 * tree of the Project Explorer.
 */
public class TreeNode 
{
	
	// Name of the node
	private String name;
	
	// The parent of the node
	private IParent parent;
	
	// The type of the children of this node (IVariable, IInvariant, etc.)
	private String childrenType;
	
	
	/**
	 * Constructor.
	 * <p> 
	 * @param name Name of the node
	 * @param parent The parent of the node
	 * @param type The type of the children
	 */
	public TreeNode(String name, IParent parent, String type) {
		this.name = name;
		this.parent = parent;
		childrenType = type;
	}
	
	
	/**
	 * Return the parent of this node
	 * <p>
	 * @return An IParent
	 */
	public IParent getParent() {return parent;}
	
	
	/**
	 * Return the children of this node (the children of the parent with the specified type).
	 * <p>
	 * @return An array of IRodinElement.
	 */
	public IRodinElement [] getChildren() {
		return Utils.getChildrenOfType(parent, childrenType);
	}
	
	
	/**
	 * Check if the node has any children.
	 * <p>
	 * @return <code>true</code> if the parent has children of the specified type.
	 */
	public boolean hasChildren() {
		return hasChildrenOfType(parent, childrenType);
	}

	
	/*
	 * Utility method.
	 */
	private boolean hasChildrenOfType(IParent parent, String childrenType) {
		// TODO Should be replace by method of IParent
		try {
			IRodinElement [] children = parent.getChildren();
			for (int i = 0; i < children.length; i++) {
				if (children[i].getElementType().equals(childrenType)) {
					return true;
				}
			}
		}
		catch (RodinDBException e) {
			return false;
			// TODO Exception handle
		}
		return false;
	}
	
	
	/**
	 * Return the name of the node.
	 */
	public String toString() {return name;}
	
	
	/**
	 * Check the type of the children.
	 * <p>
	 * @param type A string represent the type
	 * @return <code>true</code> if the children of the node has the specified type
	 */
	public boolean isType(String type) {return (childrenType.equals(type));}
	
}