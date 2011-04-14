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
package org.rodinp.core.tests;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRefinementParticipant;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.core.tests.basis.RodinTestRoot;
import org.rodinp.core.tests.basis.RodinTestRoot2;
import org.rodinp.internal.core.RefinementRegistry;
import org.rodinp.internal.core.RefinementRegistry.RefinementException;

/**
 * Tests about refinements.
 * 
 * @author Nicolas Beauger
 */
public class RefinementTests extends AbstractRodinDBTests {

	public RefinementTests() {
		super("Refinement Tests");
	}

	private static class RefPartCallLogger {

		private final List<Integer> calls = new ArrayList<Integer>();

		public void called(Integer i) {
			calls.add(i);
		}

		public void clear() {
			calls.clear();
		}

		public void assertCalls(boolean ordered, Integer... expected) {
			final List<Integer> expList = asList(expected);
			if (ordered) {
				assertEquals(expList, calls);
			} else {
				assertEquals(expList.size(), calls.size());
				assertTrue(calls.containsAll(expList));
			}
		}
	}

	private static final RefPartCallLogger LOGGER = new RefPartCallLogger();

	private static abstract class AbsRefPart implements IRefinementParticipant {

		private final Integer number;
		private final RefPartCallLogger logger;

		private AbsRefPart(Integer number, RefPartCallLogger logger) {
			this.number = number;
			this.logger = logger;
		}

		@Override
		public void process(IInternalElement refinedRoot,
				IInternalElement abstractRoot) {
			logger.called(number);
		}

	}

	public static class RefPart1 extends AbsRefPart {
		public RefPart1() {
			super(1, LOGGER);
		}
	}

	public static class RefPart2 extends AbsRefPart {
		public RefPart2() {
			super(2, LOGGER);
		}
	}

	private static final IRefinementParticipant EXCEPTION_PARTICIPANT = new AbsRefPart(
			3, LOGGER) {

		@Override
		public void process(IInternalElement refinedRoot,
				IInternalElement abstractRoot) {
			super.process(refinedRoot, abstractRoot);
			throw new RuntimeException();
		}
	};

	private static final RefinementRegistry REG = RefinementRegistry
			.getDefault();

	private IRodinProject project;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		final IRodinDB rodinDB = RodinCore.getRodinDB();
		final IRodinProject[] projecs = rodinDB.getRodinProjects();
		for (IRodinProject prj : projecs) {
			prj.getProject().delete(true, null);
		}
		REG.clear();
		LOGGER.clear();
		project = createRodinProject("RefTestPrj");
	}

	private IInternalElement makeRoot(String bareName, String extension) {
		final IRodinFile rodinFile = project.getRodinFile(bareName + "."
				+ extension);
		return rodinFile.getRoot();
	}

	private IInternalElement makeRoot1(String bareName) {
		return makeRoot(bareName, "test");
	}

	private IInternalElement makeRoot2(String bareName) {
		return makeRoot(bareName, "test2");
	}

	private IInternalElement createRoot1(String bareName)
			throws RodinDBException {
		final IInternalElement root = makeRoot1(bareName);
		root.getRodinFile().create(true, null);
		return root;
	}

	private static void assertRefCalls(boolean successExpected,
			IInternalElement abstractRoot, boolean ordered, Integer... expected) {
		final String refinedName = "refined_"
				+ abstractRoot.getRodinFile().getElementName();
		final IInternalElement refinedRoot = RodinCore.refine(abstractRoot,
				refinedName, null);
		if (successExpected) {
			assertNotNull(refinedRoot);
			assertFalse(refinedRoot.equals(abstractRoot));
		} else {
			assertNull(refinedRoot);
		}
		LOGGER.assertCalls(ordered, expected);
	}

	private static void assertUnorderedRefinementCalls(
			IInternalElement abstractRoot, Integer... expected) {
		assertRefCalls(true, abstractRoot, false, expected);
	}

	private static void assertRefinementCalls(IInternalElement abstractRoot,
			Integer... expected) {
		assertRefCalls(true, abstractRoot, true, expected);
	}

	private static void assertNoRefinementCall(IInternalElement abstractRoot)
			throws Exception {
		assertFailure(abstractRoot);
	}

	private static void assertFailure(IInternalElement abstractRoot,
			Integer... expected) throws Exception {
		final IRodinProject copyPrj = createRodinProject("copy_project");
		abstractRoot.getRodinFile().copy(copyPrj, null, null, false, null);
		final IInternalElement copy = copyPrj
				.getRootElementsOfType(abstractRoot.getElementType())[0];

		assertRefCalls(false, abstractRoot, true, expected);
		assertFalse(abstractRoot.hasSameContents(copy));
	}

	// // functional cases ////
	// no refinement & no participant & no order => empty list
	public void testAllEmpty() throws Exception {
		// use root type 2 to avoid contributions
		final IInternalElement root = makeRoot2("f");
		assertRefCalls(false, root, false);
	}

	// 1 refinement & no participant & no order => empty list
	public void test1Ref0Part() throws Exception {
		REG.addRefinement(RodinTestRoot.ELEMENT_TYPE, "refTest");
		final IInternalElement root = createRoot1("f");
		assertNoRefinementCall(root);
	}

	// 1 refinement & 1 participant & no order => singleton participant
	public void test1Ref1Part() throws Exception {
		REG.addRefinement(RodinTestRoot.ELEMENT_TYPE, "refTest");
		REG.addParticipant(new RefPart1(), "refPart1Id",
				RodinTestRoot.ELEMENT_TYPE);
		final IInternalElement root = makeRoot1("f");
		assertRefinementCalls(root, 1);
	}

	// 1 refinement & 2 participants & no order => 2 participants in any order
	public void test1Ref2PartNoOrder() throws Exception {
		REG.addRefinement(RodinTestRoot.ELEMENT_TYPE, "refTest");
		REG.addParticipant(new RefPart1(), "part1", RodinTestRoot.ELEMENT_TYPE);
		REG.addParticipant(new RefPart2(), "part2", RodinTestRoot.ELEMENT_TYPE);
		final IInternalElement root = makeRoot1("f");
		assertUnorderedRefinementCalls(root, 1, 2);
	}

	// 1 refinement & 2 participants & 1 order => 2 participants in expected
	// order
	public void test1Ref2Part1Order() throws Exception {
		REG.addRefinement(RodinTestRoot.ELEMENT_TYPE, "refTest");
		REG.addParticipant(new RefPart1(), "part1", RodinTestRoot.ELEMENT_TYPE);
		REG.addParticipant(new RefPart2(), "part2", RodinTestRoot.ELEMENT_TYPE);
		REG.addOrder("part1", "part2");
		final IInternalElement root = makeRoot1("f");
		assertRefinementCalls(root, 1, 2);
	}

	// 2 refinements with 1 participant each => the good participant is called
	public void test2Ref1PartEach() throws Exception {
		REG.addRefinement(RodinTestRoot.ELEMENT_TYPE, "refTest");
		REG.addRefinement(RodinTestRoot2.ELEMENT_TYPE, "refTest2");
		REG.addParticipant(new RefPart1(), "part1", RodinTestRoot.ELEMENT_TYPE);
		REG.addParticipant(new RefPart2(), "part2", RodinTestRoot2.ELEMENT_TYPE);
		final IInternalElement root1 = makeRoot1("f");
		assertRefinementCalls(root1, 1);
		LOGGER.clear();
		final IInternalElement root2 = makeRoot2("f");
		assertRefinementCalls(root2, 2);
	}

	// 2 refinements with same 2 participants each, in a different order =>
	// order respected for each
	public void test2Ref2PartEachDiffOrder() throws Exception {
		REG.addRefinement(RodinTestRoot.ELEMENT_TYPE, "refTest");
		REG.addRefinement(RodinTestRoot2.ELEMENT_TYPE, "refTest2");
		final RefPart1 refPart1 = new RefPart1();
		final RefPart2 refPart2 = new RefPart2();
		final String part11 = "part11";
		final String part12 = "part12";
		final String part21 = "part21";
		final String part22 = "part22";
		REG.addParticipant(refPart1, part11, RodinTestRoot.ELEMENT_TYPE);
		REG.addParticipant(refPart2, part12, RodinTestRoot.ELEMENT_TYPE);
		REG.addOrder(part11, part12);
		REG.addParticipant(refPart1, part21, RodinTestRoot2.ELEMENT_TYPE);
		REG.addParticipant(refPart2, part22, RodinTestRoot2.ELEMENT_TYPE);
		REG.addOrder(part22, part21);
		final IInternalElement root1 = makeRoot1("f1");
		assertRefinementCalls(root1, 1, 2);
		LOGGER.clear();
		final IInternalElement root2 = makeRoot2("f2");
		assertRefinementCalls(root2, 2, 1);
	}

	// test contribution loading
	public void testLoad() throws Exception {
		final IInternalElement root = makeRoot1("f");
		assertRefinementCalls(root, 1, 2);
	}

	// failure cases => error logged + fail to refine ////

	// refinement added twice with same id
	public void testDuplicateRefinementId() throws Exception {
		REG.addRefinement(RodinTestRoot.ELEMENT_TYPE, "refTest");
		try {
			REG.addRefinement(RodinTestRoot2.ELEMENT_TYPE, "refTest");
			fail("expected an exception");
		} catch (RefinementException e) {
			// as expected
		}
	}

	// refinement added twice with different id
	public void testDuplicateRefinementRoot() throws Exception {
		REG.addRefinement(RodinTestRoot.ELEMENT_TYPE, "refTest");
		try {
			REG.addRefinement(RodinTestRoot.ELEMENT_TYPE, "refTest2");
			fail("expected an exception");
		} catch (RefinementException e) {
			// as expected
		}
	}

	// test participant id uinicity
	public void testIdUnicity() throws Exception {
		REG.addRefinement(RodinTestRoot.ELEMENT_TYPE, "refTest");
		final String partId = "partId";
		REG.addParticipant(new RefPart1(), partId, RodinTestRoot.ELEMENT_TYPE);
		try {
			REG.addParticipant(new RefPart2(), partId,
					RodinTestRoot2.ELEMENT_TYPE);
			fail("expected an exception");
		} catch (RefinementException e) {
			// as expected
		}
	}

	// participant to unknown refinement
	public void testAddPartUnknownRefinement() throws Exception {
		try {
			REG.addParticipant(new RefPart1(), "part1",
					RodinTestRoot.ELEMENT_TYPE);
			fail("expected an exception");
		} catch (RefinementException e) {
			// as expected
		}
	}

	// order with unknown first
	public void testAddOrderUnknownPart1() throws Exception {
		REG.addRefinement(RodinTestRoot.ELEMENT_TYPE, "refTest");
		final String part2 = "part2";
		REG.addParticipant(new RefPart2(), part2, RodinTestRoot.ELEMENT_TYPE);
		try {
			REG.addOrder("part1", part2);
			fail("expected an exception");
		} catch (RefinementException e) {
			// as expected
		}
	}

	// order with unknown second
	public void testAddOrderUnknownPart2() throws Exception {
		REG.addRefinement(RodinTestRoot.ELEMENT_TYPE, "refTest");
		final String part1 = "part1";
		REG.addParticipant(new RefPart1(), part1, RodinTestRoot.ELEMENT_TYPE);
		try {
			REG.addOrder(part1, "part2");
			fail("expected an exception");
		} catch (RefinementException e) {
			// as expected
		}
	}

	// order with known second but not registered for given root
	public void testParticipantsRegisteredForDifferentRoots() throws Exception {
		REG.addRefinement(RodinTestRoot.ELEMENT_TYPE, "refTest");
		REG.addRefinement(RodinTestRoot2.ELEMENT_TYPE, "refTest2");
		final RefPart1 refPart1 = new RefPart1();
		final RefPart2 refPart2 = new RefPart2();
		final String part1 = "part1";
		final String part2 = "part2";
		REG.addParticipant(refPart1, part1, RodinTestRoot.ELEMENT_TYPE);
		REG.addParticipant(refPart2, part2, RodinTestRoot2.ELEMENT_TYPE);
		try {
			REG.addOrder(part1, part2);
			fail("expected an exception");
		} catch (RefinementException e) {
			// as expected
		}
	}

	// cycle in order (with resp. 1,2,3 participants)
	public void testCycle1() throws Exception {
		REG.addRefinement(RodinTestRoot.ELEMENT_TYPE, "refTest");
		final RefPart1 refPart1 = new RefPart1();
		final String part1 = "part1";
		REG.addParticipant(refPart1, part1, RodinTestRoot.ELEMENT_TYPE);
		try {
			REG.addOrder(part1, part1);
			fail("expected an exception");
		} catch (RefinementException e) {
			// as expected
		}
	}

	public void testCycle2() throws Exception {
		REG.addRefinement(RodinTestRoot.ELEMENT_TYPE, "refTest");
		final RefPart1 refPart1 = new RefPart1();
		final RefPart2 refPart2 = new RefPart2();
		final String part1 = "part1";
		final String part2 = "part2";
		REG.addParticipant(refPart1, part1, RodinTestRoot.ELEMENT_TYPE);
		REG.addParticipant(refPart2, part2, RodinTestRoot.ELEMENT_TYPE);
		REG.addOrder(part1, part2);
		try {
			REG.addOrder(part2, part1);
			fail("expected an exception");
		} catch (RefinementException e) {
			// as expected
		}
	}

	public void testCycle3() throws Exception {
		REG.addRefinement(RodinTestRoot.ELEMENT_TYPE, "refTest");
		final RefPart1 refPart1 = new RefPart1();
		final RefPart2 refPart2 = new RefPart2();
		final RefPart2 refPart3 = new RefPart2();
		final String part1 = "part1";
		final String part2 = "part2";
		final String part3 = "part3";
		REG.addParticipant(refPart1, part1, RodinTestRoot.ELEMENT_TYPE);
		REG.addParticipant(refPart2, part2, RodinTestRoot.ELEMENT_TYPE);
		REG.addParticipant(refPart3, part3, RodinTestRoot.ELEMENT_TYPE);
		REG.addOrder(part1, part2);
		REG.addOrder(part2, part3);
		try {
			REG.addOrder(part3, part1);
			fail("expected an exception");
		} catch (RefinementException e) {
			// as expected
		}

	}

	// target root already exists
	public void testTargetRootAlreadyExists() throws Exception {
		REG.addRefinement(RodinTestRoot.ELEMENT_TYPE, "refTest");
		REG.addParticipant(new RefPart1(), "refPart1Id",
				RodinTestRoot.ELEMENT_TYPE);
		final IInternalElement root1 = createRoot1("refTest1");
		final IInternalElement root2 = createRoot1("refTest2");
		final String root2Name = root2.getRodinFile().getElementName();
		try {
			RodinCore.refine(root1, root2Name, null);
			fail("exception expected");
		} catch (IllegalArgumentException e) {
			// as expected
		}
	}

	private static void createChildren(IInternalElement root, int nbChildren)
			throws RodinDBException {
		for (int i = 0; i < nbChildren; i++)
			root.createChild(NamedElement.ELEMENT_TYPE, null, null);
	}

	// 1 refinement & 1 participant that throws an exception
	public void testExceptionInParticipantCall() throws Exception {
		REG.addRefinement(RodinTestRoot.ELEMENT_TYPE, "refTest");
		REG.addParticipant(EXCEPTION_PARTICIPANT, "exceptionParticipant",
				RodinTestRoot.ELEMENT_TYPE);
		final IInternalElement root = createRoot1("f");
		createChildren(root, 5);
		// the exception must be caught underneath
		assertFailure(root, 3);
	}

}
