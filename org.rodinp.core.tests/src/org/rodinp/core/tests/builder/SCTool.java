/*******************************************************************************
 * Copyright (c) 2006, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - fixed for Rodin DB API cleanup
 *******************************************************************************/
package org.rodinp.core.tests.builder;

import static org.rodinp.core.tests.builder.AbstractBuilderTest.fString;

import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.builder.IAutomaticTool;
import org.rodinp.core.builder.IExtractor;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class SCTool implements IExtractor, IAutomaticTool {

	public static boolean SHOW_CLEAN = false;
	public static boolean SHOW_RUN = false;
	public static boolean SHOW_EXTRACT = false;

	public static boolean DEBUG = false;
	
	public static boolean RUN_SC = false;

	private int index = 0;

	@SuppressWarnings("deprecation")
	protected void copyDataElements(IRodinFile ctx, IRodinFile target) throws RodinDBException {
		if (DEBUG)
			System.out.println("Copying " + ctx.getElementName() +
					" -> " + target.getElementName() + " ...");
		IRodinElement[] datas = ctx.getChildrenOfType(IData.ELEMENT_TYPE);
		for (IRodinElement element : datas) {
			IData data = (IData) element;
			IData copy = (IData) target.getInternalElement(IData.ELEMENT_TYPE,
					"foo" + index++);
			copy.create(null, null);
			final String contents = data.getAttributeValue(fString);
			copy.setAttributeValue(fString, contents, null);
		}
		if (DEBUG)
			System.out.println("Copying " + ctx.getElementName() +
					" -> " + target.getElementName() + " done.");
	}

}
