/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast;

/**
 * Language versions known by the AST library.
 * 
 * @author Nicolas Beauger
 * @since Math Language V2
 */
public enum LanguageVersion {

	/**
	 * The original mathematical language used by the Rodin platform up to 0.9.x. 
	 */
	V1,
	/**
	 * The mathematical language used by the Rodin platform since 1.0.0.
	 */
	V2;
	
	/**
	 * The latest language version supported by the AST library.
	 */
	public static LanguageVersion LATEST = latest();

	private static LanguageVersion latest() {
		final LanguageVersion[] values = values();
		return values[values.length - 1];
	}

}
