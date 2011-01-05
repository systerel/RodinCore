/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.perf.tests.rodinDB;

import static fr.systerel.perf.tests.RodinDBUtils.createRodinProject;
import static fr.systerel.perf.tests.RodinDBUtils.deleteAllProjects;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IAxiom;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.rodinp.core.IAttributeValue;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

import fr.systerel.perf.tests.Chrono;

/**
 * @author Nicolas Beauger
 * 
 */
public class RodinDBPerfTests {

	@Rule
	public static final TestName testName = new TestName();

	private static final IInternalElementType<IAxiom> ELEMENT_TYPE = IAxiom.ELEMENT_TYPE;
	private static final int NB_LEVELS = 5;
	private static final int NB_CHILDREN = 7;
	private static final IAttributeValue[] ATTRIBUTE_VALUES = new IAttributeValue[] {
			EventBAttributes.EXTENDED_ATTRIBUTE.makeValue(true),
			EventBAttributes.COMMENT_ATTRIBUTE.makeValue("this is a comment"),
			EventBAttributes.CONFIDENCE_ATTRIBUTE.makeValue(250),
			EventBAttributes.POSTAMP_ATTRIBUTE.makeValue(459459999), };

	private static final IAttributeValue[] MODIFIED_ATTRIBUTE_VALUES = new IAttributeValue[] {
		EventBAttributes.EXTENDED_ATTRIBUTE.makeValue(false),
		EventBAttributes.COMMENT_ATTRIBUTE.makeValue("this is another comment"),
		EventBAttributes.CONFIDENCE_ATTRIBUTE.makeValue(280),
		EventBAttributes.POSTAMP_ATTRIBUTE.makeValue(999945671), };

	private static void fill(IRodinFile file) throws RodinDBException {
		createChildren(file.getRoot(), NB_LEVELS);
	}

	private static void createChildren(IInternalElement elem, int rec)
			throws RodinDBException {
		final IInternalElement[] children = new IInternalElement[NB_CHILDREN];

		for (int i = 0; i < children.length; i++) {
			children[i] = elem.createChild(ELEMENT_TYPE, null, null);
			setAttributeValues(children[i], ATTRIBUTE_VALUES, children[i]);
		}
		if (rec <= 1)
			return;
		for (IInternalElement child : children) {
			createChildren(child, rec - 1);
		}
	}

	private static void setAttributeValues(IInternalElement elem, IAttributeValue[] attValues, IRodinElement handle) throws RodinDBException {
		for (IAttributeValue attVal : attValues) {
			elem.setAttributeValue(attVal, null);
		}
		elem.setAttributeValue(
				EventBAttributes.SOURCE_ATTRIBUTE.makeValue(handle),
				null);
	}

	private static IRodinFile createTestFile() throws CoreException, RodinDBException {
		final IRodinProject prj = createRodinProject("P");
		final IRodinFile file = prj.getRodinFile("f.buc");
		file.create(true, null);
		fill(file);
		file.save(null, true);
		return file;
	}

	private static void read(IInternalElement elem) throws RodinDBException {
		elem.getAttributeValues();
		for (IInternalElement child : elem.getChildrenOfType(ELEMENT_TYPE)) {
			read(child);
		}
	}
	
	private static void modify(IInternalElement elem) throws RodinDBException {
		setAttributeValues(elem, MODIFIED_ATTRIBUTE_VALUES, elem.getParent());
		for (IInternalElement child : elem.getChildrenOfType(ELEMENT_TYPE)) {
			modify(child);
		}
	}
	
	@After
	public void clean() throws Exception {
		deleteAllProjects();
	}
	
	@Test
	public void create() throws Exception {
		final Chrono chrono = new Chrono(testName);
		chrono.startMeasure();
		createTestFile();
		chrono.endMeasure();
	}

	@Test
	public void read() throws Exception {
		final IRodinFile file = createTestFile();
		final Chrono chrono = new Chrono(testName);
		chrono.startMeasure();
		read(file.getRoot());
		chrono.endMeasure();
	}

	@Test
	public void modify() throws Exception {
		final IRodinFile file = createTestFile();
		final Chrono chrono = new Chrono(testName);
		chrono.startMeasure();
		modify(file.getRoot());
		chrono.endMeasure();

	}

}
