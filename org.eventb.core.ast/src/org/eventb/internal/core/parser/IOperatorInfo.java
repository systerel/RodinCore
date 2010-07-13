/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.parser;

import org.eventb.core.ast.Formula;

public interface IOperatorInfo<T extends Formula<?>> {
	
	IParserPrinter<T> makeParser(int kind);

	String getImage();
	
	String getId();
	
	String getGroupId();
	
	boolean isSpaced();
}