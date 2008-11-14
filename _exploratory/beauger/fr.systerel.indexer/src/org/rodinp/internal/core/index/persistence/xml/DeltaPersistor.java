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

import org.rodinp.core.IRodinElement;
import org.rodinp.internal.core.index.IIndexDelta;
import org.rodinp.internal.core.index.IndexDelta;
import org.rodinp.internal.core.index.IIndexDelta.Kind;
import org.rodinp.internal.core.index.persistence.PersistenceException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Nicolas Beauger
 * 
 */
public class DeltaPersistor {

	public void save(IIndexDelta delta, Document doc, Element deltaNode) {
		final String kind = delta.getKind().toString();
		setAttribute(deltaNode, KIND, kind);

		final IRodinElement element = delta.getElement();
		IREPersistor.setIREAtt(element, ELEMENT, deltaNode);
	}

	public static IIndexDelta getDelta(Element deltaNode)
			throws PersistenceException {
		final String kindStr = getAttribute(deltaNode, KIND);
		final Kind kind = Kind.valueOf(kindStr);

		final IRodinElement element =
				IREPersistor.getIREAtt(deltaNode, ELEMENT);

		return new IndexDelta(element, kind);
	}

}
