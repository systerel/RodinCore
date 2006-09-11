/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.core.tests.builder;

import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class SCTool {

	static boolean DEBUG = false;

	protected void copyDataElements(IRodinFile ctx, IRodinFile target) throws RodinDBException {
		if (DEBUG)
			System.out.println("Copying " + ctx.getElementName() +
					" -> " + target.getElementName() + " ...");
		IRodinElement[] datas = ctx.getChildrenOfType(IData.ELEMENT_TYPE);
		for (IRodinElement element : datas) {
			IData data = (IData) element;
			IData copy = (IData) target.createInternalElement(
					IData.ELEMENT_TYPE, "foo", null, null);
			copy.setContents(data.getContents());
		}
		if (DEBUG)
			System.out.println("Copying " + ctx.getElementName() +
					" -> " + target.getElementName() + " done.");
	}

}
