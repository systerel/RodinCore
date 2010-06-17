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

import org.eventb.core.ast.extension.IOperatorProperties;
import org.eventb.internal.core.ast.extension.ExtensionPrinters.IExtensionPrinter;

public interface IParserBuilder {
	
	IParserPrinter makeParser(int tag);
	
	IExtensionPrinter getPrinter();
	
	IOperatorProperties getProperties();
	
	boolean isExtension();
	
	
}