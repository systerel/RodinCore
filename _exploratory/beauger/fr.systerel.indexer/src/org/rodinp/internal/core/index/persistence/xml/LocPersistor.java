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

import org.rodinp.core.IAttributeType;
import org.rodinp.core.IAttributedElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinCore;
import org.rodinp.core.index.IAttributeLocation;
import org.rodinp.core.index.IAttributeSubstringLocation;
import org.rodinp.core.index.IRodinLocation;
import org.rodinp.internal.core.index.AttributeLocation;
import org.rodinp.internal.core.index.AttributeSubstringLocation;
import org.rodinp.internal.core.index.RodinLocation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Nicolas Beauger
 * 
 */
public class LocPersistor {

	private LocPersistor() {
		// private constructor:
	}

	public static IRodinLocation getLocation(Element occNode) {
		final IRodinElement element = IREPersistor.getIREAtt(ELEMENT, occNode);
		final String attId = getAttribute(occNode, LOC_ATTRIBUTE);
		final String charStString = getAttribute(occNode, LOC_CHAR_START);
		final String charEndString = getAttribute(occNode, LOC_CHAR_END);

		if (attId.length() == 0) {
			return new RodinLocation(element);
		}
		final IAttributedElement attElem = (IAttributedElement) element;
		final IAttributeType attType = RodinCore.getAttributeType(attId);
		if (charStString.length() == 0) {
			return new AttributeLocation(attElem, attType);
		}
		final IAttributeType.String attTypeStr =
				(IAttributeType.String) attType;
		final int charStart = Integer.parseInt(charStString);
		final int charEnd = Integer.parseInt(charEndString);

		return new AttributeSubstringLocation(attElem, attTypeStr, charStart,
				charEnd);
	}

	public static void save(IRodinLocation location, Document doc,
			Element occNode) {
		final IRodinElement element = location.getElement();
		IREPersistor.setIREAtt(element, ELEMENT, occNode);

		if (location instanceof IAttributeLocation) {
			final IAttributeLocation attLocation =
					(IAttributeLocation) location;
			final IAttributeType attType = attLocation.getAttributeType();
			setAttribute(occNode, LOC_ATTRIBUTE, attType.getId());
		}

		if (location instanceof IAttributeSubstringLocation) {
			final IAttributeSubstringLocation attSubLoc =
					((IAttributeSubstringLocation) location);
			final int charStart = attSubLoc.getCharStart();
			final int charEnd = attSubLoc.getCharEnd();
			setAttribute(occNode, LOC_CHAR_START, Integer.toString(charStart));
			setAttribute(occNode, LOC_CHAR_END, Integer.toString(charEnd));
		}
	}

}
