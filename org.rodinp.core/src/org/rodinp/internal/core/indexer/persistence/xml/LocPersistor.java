/*******************************************************************************
 * Copyright (c) 2008, 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.indexer.persistence.xml;

import static org.rodinp.internal.core.indexer.persistence.xml.XMLAttributeTypes.*;

import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinCore;
import org.rodinp.core.location.IAttributeLocation;
import org.rodinp.core.location.IAttributeSubstringLocation;
import org.rodinp.core.location.IInternalLocation;
import org.rodinp.internal.core.indexer.persistence.PersistenceException;
import org.rodinp.internal.core.location.AttributeLocation;
import org.rodinp.internal.core.location.AttributeSubstringLocation;
import org.rodinp.internal.core.location.InternalLocation;
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

	public static IInternalLocation getLocation(Element occNode)
			throws PersistenceException {
		final IInternalElement element =
				IREPersistor.getIIEAtt(occNode, ELEMENT);

		if (!hasAttribute(occNode, LOC_ATTRIBUTE)) {
			return new InternalLocation(element);
		}
		final String attId = getAttribute(occNode, LOC_ATTRIBUTE);
		IAttributeType.String attType = RodinCore.getStringAttrType(attId);
		if (!hasAttribute(occNode, LOC_CHAR_START)) {
			return new AttributeLocation(element, attType);
		}
		final String charStString = getAttribute(occNode, LOC_CHAR_START);
		final String charEndString = getAttribute(occNode, LOC_CHAR_END);

		final int charStart = Integer.parseInt(charStString);
		final int charEnd = Integer.parseInt(charEndString);

		return new AttributeSubstringLocation(element, attType, charStart,
				charEnd);
	}

	public static void save(IInternalLocation location, Document doc,
			Element occNode) {
		final IInternalElement element = location.getElement();
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
