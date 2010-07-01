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
import org.eventb.core.ast.extension.IOperatorProperties;

public interface IParserInfo<T extends Formula<?>> {
	
	IParserPrinter<T> makeParser(int tag);
	
	IOperatorProperties getProperties();
	
	boolean isExtension();
	
	
}