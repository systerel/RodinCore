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

import static org.rodinp.internal.core.index.persistence.xml.XMLElementTypes.*;

import java.util.Collection;

import org.rodinp.internal.core.index.IIndexDelta;
import org.rodinp.internal.core.index.persistence.PersistenceException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Nicolas Beauger
 * 
 */
public class DeltaListPersistor {

    public static void save(Collection<IIndexDelta> deltas, Document doc,
	    Element indexRoot) {
	for (IIndexDelta delta : deltas) {

	    final DeltaPersistor persistor = new DeltaPersistor();
	    final Element deltaNode = createElement(doc, DELTA);
	    persistor.save(delta, doc, deltaNode);

	    indexRoot.appendChild(deltaNode);
	}
    }

    public static void restore(Element indexRoot, Collection<IIndexDelta> deltaList)
	    throws PersistenceException {
	final NodeList deltaNodes = getElementsByTagName(indexRoot, DELTA);
	
	for (int i = 0; i < deltaNodes.getLength(); i++) {
	    final Element deltaNode = (Element) deltaNodes.item(i);

	    final IIndexDelta delta = DeltaPersistor.getDelta(deltaNode);
	    deltaList.add(delta);
	}

    }

}
