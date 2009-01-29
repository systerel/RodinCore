/*******************************************************************************
 * Copyright (c) 2007, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.internal.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;

/**
 * @author htson
 *         <p>
 *         Utility class for drag and copy.
 *         </p>
 */
public class DragAndCopyUtil {

	public static boolean isEnable(IStructuredSelection selection) {
		Collection<IRodinElement> elements = new ArrayList<IRodinElement>();

		// Added to list of parent only.
		for (Iterator<?> it = selection.iterator(); it.hasNext();) {
			Object obj = it.next();
			IRodinElement element = null;
			if (obj instanceof IRodinElement) {
				element = (IRodinElement) obj;
			}
			if (element != null)
				elements = UIUtils.addToTreeSet(elements, element);
		}

		boolean projSelected = selectionIsOfTypeRodinProject(elements);
		boolean fileFoldersSelected = selectionIsOfTypeRodinFile(elements);

		// selection must be homogeneous
		if (projSelected && fileFoldersSelected)
			return false;

		// must have a common parent if not project selected
		if (!projSelected) {
			IRodinElement parent = null;
			for (Iterator<?> it = selection.iterator(); it.hasNext();) {
				IRodinElement c_parent = null;
				Object obj = it.next();
				if (obj instanceof IRodinElement) {
					c_parent = ((IRodinElement) obj).getParent();
				}
				if (c_parent == null)
					return false;
				if (parent == null)
					parent = c_parent;
				else if (!parent.equals(c_parent)) {
					return false;
				}
			}
		}

		return true;
	}
	
	public static boolean selectionIsOfTypeRodinProject(
			Collection<IRodinElement> elements) {
		for (IRodinElement element : elements) {
			if (!(element instanceof IRodinProject))
				return false;
		}
		return true;
	}

	public static boolean selectionIsOfTypeRodinFile(
			Collection<IRodinElement> elements) {
		for (IRodinElement element : elements) {
			if (!(element instanceof IRodinFile))
				return false;
		}
		return true;
	}

}
