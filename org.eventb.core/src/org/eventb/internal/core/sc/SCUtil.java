/*******************************************************************************
 * Copyright (c) 2008, 2012 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University of Southampton - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.internal.core.sc;

import org.eventb.core.IIdentifierElement;
import org.eventb.core.ILabeledElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProblem;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class SCUtil {

	public static boolean DEBUG = false;
	public static boolean DEBUG_STATE = false;
	public static boolean DEBUG_MARKERS = false;
	public static boolean DEBUG_MODULECONF = false;
	
	public static void createProblemMarker(
			IRodinElement element, 
			IRodinProblem problem, 
			Object... args)
	throws RodinDBException {
		if (DEBUG_MARKERS)
			traceMarker(element, problem.getLocalizedMessage(args));

		element.createProblemMarker(problem, args);
	}

	public static void traceMarker(IRodinElement element, String message) {
		
		String name = element.getElementName();
		
		try {
			if (element instanceof ILabeledElement)
				name = ((ILabeledElement) element).getLabel();
			else if (element instanceof IIdentifierElement)
				name = ((IIdentifierElement) element).getIdentifierString();
			else if (element instanceof IRodinFile)
				name = ((IRodinFile) element).getBareName();
		} catch (RodinDBException e) {
			// ignore
		} finally {
		
			System.out.println("SC MARKER: " + name + ": " + message);
		}
	}

}
