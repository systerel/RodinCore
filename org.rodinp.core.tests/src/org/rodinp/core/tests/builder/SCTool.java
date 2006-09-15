/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.core.tests.builder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class SCTool {

	public static boolean SHOW_CLEAN = false;
	public static boolean SHOW_RUN = false;
	public static boolean SHOW_EXTRACT = false;
	public static boolean SHOW_REMOVE = false;

	public static boolean DEBUG = true;
	
	public static boolean RUN_SC = false;

	private int index = 0;

	protected void copyDataElements(IRodinFile ctx, IRodinFile target) throws RodinDBException {
		if (DEBUG)
			System.out.println("Copying " + ctx.getElementName() +
					" -> " + target.getElementName() + " ...");
		IRodinElement[] datas = ctx.getChildrenOfType(IData.ELEMENT_TYPE);
		for (IRodinElement element : datas) {
			IData data = (IData) element;
			IData copy = (IData) target.createInternalElement(
					IData.ELEMENT_TYPE, "foo" + index++, null, null);
			copy.setContents(data.getContents());
		}
		if (DEBUG)
			System.out.println("Copying " + ctx.getElementName() +
					" -> " + target.getElementName() + " done.");
	}
	
	public void remove(IFile file, IFile origin, IProgressMonitor monitor, String name) throws CoreException {
		if (SHOW_REMOVE)
			ToolTrace.addTrace(name, "remove", file);
	
		if (AbstractBuilderTest.getComponentName(file.getName()).equals(AbstractBuilderTest.getComponentName(origin.getName())))
			file.delete(true, monitor);
	}

}
