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
package org.rodinp.internal.core.index.tests;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.index.IDeclaration;
import org.rodinp.core.index.IOccurrence;
import org.rodinp.core.index.IOccurrenceKind;
import org.rodinp.core.index.RodinIndexer;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.internal.core.index.Descriptor;
import org.rodinp.internal.core.index.Occurrence;
import org.rodinp.internal.core.index.tables.RodinIndex;

public class IndexTestsUtil {

	// TODO remove unused methods
	
	public static final IOccurrenceKind TEST_KIND = RodinIndexer
			.addOccurrenceKind("fr.systerel.indexer.test", "test");

	public static final IOccurrenceKind TEST_KIND_1 = RodinIndexer
			.addOccurrenceKind("fr.systerel.indexer.test_1", "test_1");

	public static final IOccurrenceKind TEST_KIND_2 = RodinIndexer
			.addOccurrenceKind("fr.systerel.indexer.test_2", "test_2");

	public static final String defaultName = "banzai";

	public static IRodinFile createRodinFile(IRodinProject project,
			String fileName) throws RodinDBException {
		IRodinFile file = project.getRodinFile(fileName);
		file.create(true, null);
		return file;
	}

	public static Occurrence createDefaultOccurrence(IRodinElement element) {
		return new Occurrence(TEST_KIND, RodinIndexer.getRodinLocation(element));
	}

	public static NamedElement createNamedElement(IRodinFile file,
			String elementName) throws CoreException {
		NamedElement el = new NamedElement(elementName, file.getRoot());
		el.create(null, null);
		return el;
	}

	public static void assertNoSuchDescriptor(RodinIndex index,
			IInternalElement element) {
		Descriptor desc = index.getDescriptor(element);
		TestCase.assertNull("there should not be any descriptor for element "
				+ element.getElementName(), desc);
	}

	public static void assertNotNull(Descriptor desc) {
		TestCase.assertNotNull("Descriptor " + desc + " should not be null",
				desc);
	}

	public static void addOccurrences(IOccurrence[] occurrences,
			Descriptor descriptor) {
		for (IOccurrence occ : occurrences) {
			descriptor.addOccurrence(occ);
		}
	}

	public static void assertDescriptor(Descriptor desc,
			IDeclaration declaration, int expectedLength) {
		assertDescriptor(desc, declaration);
		assertLength(desc, expectedLength);
	}

	public static void assertContains(Descriptor desc, IOccurrence occ) {
		assertNotNull(desc);
		TestCase.assertTrue("occurrence not found: "
				+ occ.getLocation().getElement(), desc.hasOccurrence(occ));
	}

	public static void assertContainsNot(Descriptor desc, IOccurrence occ) {
		assertNotNull(desc);
		TestCase.assertFalse("occurrence should not be found: "
				+ occ.getLocation().getElement(), desc.hasOccurrence(occ));
	}

	public static void assertContainsAll(Descriptor desc, IOccurrence... occs) {
		assertNotNull(desc);
		for (IOccurrence occ : occs) {
			assertContains(desc, occ);
		}
	}

	public static void assertContainsNone(Descriptor desc, IOccurrence[] occs) {
		assertNotNull(desc);
		for (IOccurrence occ : occs) {
			assertContainsNot(desc, occ);
		}
	}

	public static void assertLength(Descriptor desc, int expectedLength) {
		assertNotNull(desc);
		TestCase.assertEquals("bad number of occurrences", expectedLength, desc
				.getOccurrences().length);
	}

	public static void assertDescriptor(Descriptor desc,
			IDeclaration declaration) {
		assertNotNull(desc);
		TestCase.assertEquals("bad declaration for descriptor " + desc,
				declaration, desc.getDeclaration());
	}

	public static void assertLength(IRodinElement[] elements, int length) {
		TestCase.assertEquals("incorrect number of elements in: " + elements
				+ "=" + Arrays.asList(elements), length, elements.length);
	}

	public static void assertIsEmpty(IInternalElement[] elements) {
		assertLength(elements, 0);
	}

	public static void assertIsEmpty(IRodinFile[] files) {
		assertLength(files, 0);
	}

	public static void assertContainsAll(IInternalElement[] expectedElements,
			IInternalElement[] actualElements) {

		for (IInternalElement elem : expectedElements) {
			assertContains(elem, actualElements);
		}
	}

	public static void assertSameElements(IInternalElement[] expectedElements,
			IInternalElement[] actualElements) {

		assertContainsAll(expectedElements, actualElements);

		assertLength(actualElements, expectedElements.length);
	}

	public static void assertContains(IInternalElement elem,
			IInternalElement[] actualElements) {

		List<IInternalElement> actList = Arrays.asList(actualElements);

		TestCase.assertTrue("element " + elem.getElementName()
				+ " is not present", actList.contains(elem));
	}

	public static void assertExports(Set<IDeclaration> expected,
			Set<IDeclaration> actual) {

		TestCase.assertEquals("Bad exports.", expected, actual);
	}

	public static <T> void assertPredecessors(final List<T> predecessors,
			T... preds) {
		TestCase.assertEquals("Bad predecessors length", preds.length,
				predecessors.size());
		for (T pred : preds) {
			TestCase.assertTrue("Predecessors should contain " + pred,
					predecessors.contains(pred));
		}
	}

	public static Integer[] makeIntArray(Integer... integers) {
		return integers;
	}

	public static IInternalElement[] makeIIEArray(IInternalElement... elements) {
		return elements;
	}

	public static IRodinFile[] makeIRFArray(IRodinFile... files) {
		return files;
	}

}
