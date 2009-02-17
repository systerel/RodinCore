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
import static org.rodinp.internal.core.indexer.persistence.xml.XMLElementTypes.*;

import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.internal.core.indexer.ProjectIndexManager;
import org.rodinp.internal.core.indexer.persistence.PersistenceException;
import org.rodinp.internal.core.indexer.persistence.PersistentPIM;
import org.rodinp.internal.core.indexer.sort.TotalOrder;
import org.rodinp.internal.core.indexer.tables.ExportTable;
import org.rodinp.internal.core.indexer.tables.RodinIndex;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Nicolas Beauger
 * 
 */
public class PIMPersistor {

	public ProjectIndexManager restore(Element pimNode)
			throws PersistenceException {
		assertName(pimNode, PIM);

		final String projectString = getAttribute(pimNode, PROJECT);
		final IRodinProject project =
				(IRodinProject) RodinCore.valueOf(projectString);

		final NodeList rodinIndexNodes =
				getElementsByTagName(pimNode, RODIN_INDEX, 1);
		final Element indexNode = (Element) rodinIndexNodes.item(0);
		final RodinIndex index = new RodinIndex();
		IndexPersistor.restore(indexNode, index);

		final NodeList exportNodes =
				getElementsByTagName(pimNode, EXPORT_TABLE, 1);
		final Element exportNode = (Element) exportNodes.item(0);
		final ExportTable exportTable = new ExportTable();
		ExpTablePersistor.restore(exportNode, exportTable);

		final NodeList orderNodes = getElementsByTagName(pimNode, GRAPH, 1);
		final Element orderNode = (Element) orderNodes.item(0);
		final TotalOrder<IRodinFile> order = new TotalOrder<IRodinFile>();
		TotalOrderPersistor.restore(orderNode, order);

		return new ProjectIndexManager(project, index, exportTable, order);
	}

	public void save(PersistentPIM pim, Document doc, Element pimNode) {

		IREPersistor.setIREAtt(pim.getProject(), PROJECT, pimNode);
		
		final Element indexNode = createElement(doc, RODIN_INDEX);
		IndexPersistor.save(pim.getDescriptors(), doc, indexNode);
		pimNode.appendChild(indexNode);

		final Element exportNode = createElement(doc, EXPORT_TABLE);
		ExpTablePersistor.save(pim.getExportTable(), doc, exportNode);
		pimNode.appendChild(exportNode);

		final Element orderNode = createElement(doc, GRAPH);
		TotalOrderPersistor.save(pim.getOrder(), doc, orderNode);
		pimNode.appendChild(orderNode);
	}

}
