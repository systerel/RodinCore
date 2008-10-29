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

import org.rodinp.core.index.IOccurrence;
import org.rodinp.core.index.IOccurrenceKind;
import org.rodinp.core.index.IRodinLocation;
import org.rodinp.core.index.RodinIndexer;
import org.rodinp.internal.core.index.Occurrence;
import org.rodinp.internal.core.index.persistence.PersistenceException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Nicolas Beauger
 * 
 */
public class OccPersistor {

	public static IOccurrence getOccurrence(Element occNode) throws PersistenceException {
		final IOccurrenceKind kind = getKind(occNode);

		final IRodinLocation location = LocPersistor.getLocation(occNode);

		return new Occurrence(kind, location);
	}

	private static IOccurrenceKind getKind(Element occNode) throws PersistenceException {
		final String kindId = getAttribute(occNode, OCC_KIND);
		return RodinIndexer.getOccurrenceKind(kindId);
	}

	public static void save(IOccurrence occurrence, Document doc, Element occNode) {

		final IOccurrenceKind kind = occurrence.getKind();
		setAttribute(occNode, OCC_KIND, kind.getId());

		final IRodinLocation location = occurrence.getLocation();
		LocPersistor.save(location, doc, occNode);
	}

}
