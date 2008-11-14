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
import static org.rodinp.internal.core.index.persistence.xml.XMLElementTypes.*;

import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.internal.core.index.PerProjectPIM;
import org.rodinp.internal.core.index.ProjectIndexManager;
import org.rodinp.internal.core.index.persistence.PersistenceException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Nicolas Beauger
 * 
 */
public class PIMPersistor {

	public void restore(Element pimNode, PerProjectPIM pppim)
			throws PersistenceException {
		assertName(pimNode, PIM);

		final String projectString = getAttribute(pimNode, PROJECT);
		final IRodinProject project =
				(IRodinProject) RodinCore.valueOf(projectString);
		final ProjectIndexManager pim = pppim.getOrCreate(project);

		final NodeList rodinIndexNodes =
				getElementsByTagName(pimNode, RODIN_INDEX, 1);
		final Element indexNode = (Element) rodinIndexNodes.item(0);
		IndexPersistor.restore(indexNode, pim.getIndex());

		final NodeList exportNodes =
				getElementsByTagName(pimNode, EXPORT_TABLE, 1);
		final Element exportNode = (Element) exportNodes.item(0);
		ExpTablePersistor.restore(exportNode, pim.getExportTable());

		final NodeList orderNodes = getElementsByTagName(pimNode, GRAPH, 1);
		final Element orderNode = (Element) orderNodes.item(0);
		TotalOrderPersistor.restore(orderNode, pim.getOrder());

		pim.restoreNonPersistentData();
	}

	public void save(ProjectIndexManager pim, Document doc, Element pimNode) {

		IREPersistor.setIREAtt(pim.getProject(), PROJECT, pimNode);

		final Element indexNode = createElement(doc, RODIN_INDEX);
		IndexPersistor.save(pim.getIndex(), doc, indexNode);
		pimNode.appendChild(indexNode);

		final Element exportNode = createElement(doc, EXPORT_TABLE);
		ExpTablePersistor.save(pim.getExportTable(), doc, exportNode);
		pimNode.appendChild(exportNode);

		final Element orderNode = createElement(doc, GRAPH);
		TotalOrderPersistor.save(pim.getOrder(), doc, orderNode);
		pimNode.appendChild(orderNode);
	}

}
