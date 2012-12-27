/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.core.emf.lightcore;

/**
 * Utilities for the Light Core classes.
 * 
 * @author "Thomas Muller"
 */
public class LightCoreUtils {
	
	public static void debug(String message) {
		System.out.println("----------------------");
		System.out.println(message);
	}

	public static void log(Exception e) {
		System.out.println("--- An exception occured ---");
		e.printStackTrace();
	}

}
