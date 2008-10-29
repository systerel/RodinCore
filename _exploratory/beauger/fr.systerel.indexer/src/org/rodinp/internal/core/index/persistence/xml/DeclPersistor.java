/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.index.persistence.xml;

import static org.rodinp.internal.core.index.persistence.xml.XMLAttributeTypes.*;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.index.IDeclaration;
import org.rodinp.internal.core.index.Declaration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Nicolas Beauger
 * 
 */
public class DeclPersistor {

	public static IDeclaration getDeclaration(Element declNode) {
		final IInternalElement element =
				(IInternalElement) IREPersistor.getIREAtt(ELEMENT, declNode);
		final String name = getAttribute(declNode, NAME);
		return new Declaration(element, name);
	}

	public static void save(IDeclaration declaration, Document doc, Element declNode) {
		final IInternalElement element = declaration.getElement();
		final String name = declaration.getName();
		IREPersistor.setIREAtt(element, ELEMENT, declNode);
		setAttribute(declNode, NAME, name);
	}
}
