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
package org.rodinp.core.tests.indexer;

import static org.rodinp.core.tests.util.IndexTestsUtil.*;

import java.util.Collection;
import java.util.Iterator;

import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.indexer.IDeclaration;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.core.tests.util.IndexTestsUtil;
import org.rodinp.internal.core.indexer.Declaration;
import org.rodinp.internal.core.indexer.Descriptor;
import org.rodinp.internal.core.indexer.tables.RodinIndex;

public class RodinIndexTests extends IndexTests {

	public RodinIndexTests(String name) {
		super(name);
	}

	private static IRodinProject project;
	private static IRodinFile file;
	private static NamedElement elt1;
	private static NamedElement elt2;

	private static final RodinIndex index = new RodinIndex();
	private static final String defaultName = "defaultName";
	private static final String name1 = "eltName1";
	private static final String name2 = "eltName2";
	private static IDeclaration declElt1;
	private static IDeclaration declElt2;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		project = createRodinProject("P");
		file = createRodinFile(project, "rodinIndex.test");
		elt1 = createNamedElement(file,
				defaultName);
		elt2 = IndexTestsUtil.createNamedElement(file,
				defaultName + "2");
		declElt1 = new Declaration(elt1, name1);
		declElt2 = new Declaration(elt2, name2);
	}

	@Override
	protected void tearDown() throws Exception {
		deleteProject("P");
		index.clear();
		super.tearDown();
	}

	public void testMakeDescriptor() throws Exception {
		final Descriptor descriptor = index.makeDescriptor(declElt1);

		assertDescriptor(descriptor, declElt1, 0);
	}

	public void testGetDescriptor() throws Exception {
		final Descriptor descriptorMake = index.makeDescriptor(declElt1);

		final Descriptor descriptorGet = index.getDescriptor(elt1);

		assertEquals("descriptors returned by make and get are different",
				descriptorMake, descriptorGet);
	}

	public void testMakeDoubleDescriptor() throws Exception {
		index.makeDescriptor(declElt1);

		try {
			index.makeDescriptor(declElt1);
			fail("expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// OK
		}
	}

	public void testMakeDoubleDescriptorDiffName() throws Exception {
		index.makeDescriptor(declElt1);

		try {
			index.makeDescriptor(new Declaration(elt1, name2));
			fail("expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// OK
		}
	}

	public void testRemoveDescriptor() throws Exception {
		index.makeDescriptor(declElt1);
		index.removeDescriptor(elt1);

		assertNoSuchDescriptor(index, elt1);
	}

	public void testGetDescriptors() throws Exception {
		index.makeDescriptor(declElt1);
		index.makeDescriptor(declElt2);

		final Collection<Descriptor> descriptors = index.getDescriptors();

		assertEquals("bad number of descriptors", 2, descriptors.size());

		final Iterator<Descriptor> iter = descriptors.iterator();
		Descriptor desc = iter.next();
		Descriptor desc2 = iter.next();

		if (desc.getDeclaration().getElement() == elt1) {
			assertDescriptor(desc, declElt1, 0);
			assertDescriptor(desc2, declElt2, 0);
		} else {
			assertDescriptor(desc, declElt2, 0);
			assertDescriptor(desc2, declElt1, 0);
		}
	}

	public void testClear() throws Exception {
		index.makeDescriptor(declElt1);
		index.makeDescriptor(declElt2);

		index.clear();

		assertNoSuchDescriptor(index, elt1);
		assertNoSuchDescriptor(index, elt2);
	}

}
