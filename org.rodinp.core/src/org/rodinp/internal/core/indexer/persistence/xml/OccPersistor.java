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

import org.rodinp.core.RodinCore;
import org.rodinp.core.indexer.IDeclaration;
import org.rodinp.core.indexer.IOccurrence;
import org.rodinp.core.indexer.IOccurrenceKind;
import org.rodinp.core.location.IInternalLocation;
import org.rodinp.internal.core.indexer.Occurrence;
import org.rodinp.internal.core.indexer.persistence.PersistenceException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Nicolas Beauger
 * 
 */
public class OccPersistor {

	public static IOccurrence getOccurrence(Element occNode,
			IDeclaration declaration) throws PersistenceException {
		final IOccurrenceKind kind = getKind(occNode);

		final IInternalLocation location = LocPersistor.getLocation(occNode);

		return new Occurrence(kind, location, declaration);
	}

	private static IOccurrenceKind getKind(Element occNode)
			throws PersistenceException {
		final String kindId = getAttribute(occNode, KIND);
		final IOccurrenceKind kind = RodinCore.getOccurrenceKind(kindId);
		if (kind == null) {
			throw new PersistenceException();
		}
		return kind;
	}

	public static void save(IOccurrence occurrence, Document doc,
			Element occNode) {

		final IOccurrenceKind kind = occurrence.getKind();
		setAttribute(occNode, KIND, kind.getId());

		final IInternalLocation location = occurrence.getLocation();
		LocPersistor.save(location, doc, occNode);
	}

}
