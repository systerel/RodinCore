/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - Extracted categories in separate class
 *******************************************************************************/
package org.eventb.core.sc;

/**
 * @author Laurent Voisin
 * @since 1.0
 */
public class ParseProblemCategory {

	public static final int CATEGORY_NONE = 0;
	public static final int CATEGORY_LEXICAL = 1;
	public static final int CATEGORY_SYNTAX = 2;
	public static final int CATEGORY_LEGIBILITY = 4;
	public static final int CATEGORY_TYPING = 8;

	private ParseProblemCategory() {
		// singleton class
	}
	
}
