/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.pm;

import java.util.StringTokenizer;


public class UserSupportUtils {
	
	public static boolean DEBUG = false;
	
	private static final String DEBUG_PREFIX = "*** User Support *** ";
	
	public static void debug(String message) {
		StringTokenizer tokenizer = new StringTokenizer(message, "\n\r", true);
		while (tokenizer.hasMoreTokens()){
			String token = tokenizer.nextToken();
			if (!token.equals("\n"))
				System.out.println(DEBUG_PREFIX + token);
		}
	}
}
