/*******************************************************************************
 * Copyright (c) 2008, 2012 Systerel and others.
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

import org.rodinp.internal.core.indexer.persistence.PersistenceException;
import org.w3c.dom.Element;

/**
 * @author Nicolas Beauger
 * 
 */
public class IndexerPersistor {

	public static void save(String indexerId, Element indexerNode) {
		setAttribute(indexerNode, ID, indexerId);
	}

	public static String getIndexerId(Element indexerNode)
			throws PersistenceException {
		return getAttribute(indexerNode, ID);
	}

}
