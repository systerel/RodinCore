/*******************************************************************************
 * Copyright (c) 2011, 2014 Systerel and others.
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRefinementManager;
import org.rodinp.core.IRefinementParticipant;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
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
				IInternalElement abstractRoot, IProgressMonitor monitor) {
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
				IInternalElement abstractRoot, IProgressMonitor monitor) {
			super.process(refinedRoot, abstractRoot, monitor);
			throw new RuntimeException();
		}
	};

	private static final RefinementRegistry REG = RefinementRegistry
			.getDefault();

	private IRodinProject project;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		clearDB();
		
		REG.clear();
		LOGGER.clear();
		project = createRodinProject("RefTestPrj");
	}

	private static void clearDB() throws RodinDBException, CoreException {
		final IRodinDB rodinDB = RodinCore.getRodinDB();
		final IRodinProject[] projecs = rodinDB.getRodinProjects();
		for (IRodinProject prj : projecs) {
			prj.getProject().delete(true, null);
		}
	}

	@After
	public void tearDown() throws Exception {
		clearDB();
		super.tearDown();
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

	private static void assertRefCalls(boolean successExpected,
			IInternalElement sourceRoot, boolean ordered, Integer... expected)
			throws CoreException {
		final String refinedName = "refined_"
				+ sourceRoot.getRodinFile().getElementName();
		final IRodinFile targetFile = sourceRoot.getRodinProject()
				.getRodinFile(refinedName);
		final IInternalElement refinedRoot = targetFile.getRoot();

		final IRefinementManager refMgr = RodinCore.getRefinementManager();
		final boolean success = refMgr.refine(sourceRoot, refinedRoot, null);
		assertEquals(successExpected, success);
		LOGGER.assertCalls(ordered, expected);
	}

	private static void assertUnorderedRefinementCalls(
			IInternalElement abstractRoot, Integer... expected)
			throws CoreException {
		assertRefCalls(true, abstractRoot, false, expected);
	}

	private static void assertRefinementCalls(IInternalElement abstractRoot,
			Integer... expected) throws CoreException {
		assertRefCalls(true, abstractRoot, true, expected);
	}

	private static void assertNoRefinementCall(IInternalElement abstractRoot)
			throws Exception {
		assertFailure(abstractRoot);
	}

	private static void assertFailure(IInternalElement sourceRoot,
			Integer... expected) throws Exception {
		assertRefCalls(false, sourceRoot, true, expected);
	}

	// // functional cases ////
	// no refinement & no participant & no order => empty list
	@Test
	public void testAllEmpty() throws Exception {
		// use root type 2 to avoid contributions
		final IInternalElement root = makeRoot2("f");
		assertRefCalls(false, root, false);
	}

	// 1 refinement & no participant & no order => empty list
	@Test
	public void test1Ref0Part() throws Exception {
		REG.addRefinement(RodinTestRoot.ELEMENT_TYPE, "refTest");
		final IInternalElement root = makeRoot1("f");
		assertNoRefinementCall(root);
	}

	// 1 refinement & 1 participant & no order => singleton participant
	@Test
	public void test1Ref1Part() throws Exception {
		REG.addRefinement(RodinTestRoot.ELEMENT_TYPE, "refTest");
		REG.addParticipant(new RefPart1(), "refPart1Id",
				RodinTestRoot.ELEMENT_TYPE);
		final IInternalElement root = makeRoot1("f");
		assertRefinementCalls(root, 1);
	}

	// 1 refinement & 2 participants & no order => 2 participants in any order
	@Test
	public void test1Ref2PartNoOrder() throws Exception {
		REG.addRefinement(RodinTestRoot.ELEMENT_TYPE, "refTest");
		REG.addParticipant(new RefPart1(), "part1", RodinTestRoot.ELEMENT_TYPE);
		REG.addParticipant(new RefPart2(), "part2", RodinTestRoot.ELEMENT_TYPE);
		final IInternalElement root = makeRoot1("f");
		assertUnorderedRefinementCalls(root, 1, 2);
	}

	// 1 refinement & 2 participants & 1 order => 2 participants in expected
	// order
	@Test
	public void test1Ref2Part1Order() throws Exception {
		REG.addRefinement(RodinTestRoot.ELEMENT_TYPE, "refTest");
		REG.addParticipant(new RefPart1(), "part1", RodinTestRoot.ELEMENT_TYPE);
		REG.addParticipant(new RefPart2(), "part2", RodinTestRoot.ELEMENT_TYPE);
		REG.addOrder("part1", "part2");
		final IInternalElement root = makeRoot1("f");
		assertRefinementCalls(root, 1, 2);
	}

	// 2 refinements with 1 participant each => the good participant is called
	@Test
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
	@Test
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
	@Test
	public void testLoad() throws Exception {
		final IInternalElement root = makeRoot1("f");
		assertRefinementCalls(root, 1, 2);
	}

	// failure cases => error logged + fail to refine ////

	// refinement added twice with same id
	@Test
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
	@Test
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
	@Test
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
	@Test
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
	@Test
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
	@Test
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
	@Test
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
	@Test
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

	@Test
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

	@Test
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

	// 1 refinement & 1 participant that throws an exception
	@Test
	public void testExceptionInParticipantCall() throws Exception {
		REG.addRefinement(RodinTestRoot.ELEMENT_TYPE, "refTest");
		REG.addParticipant(EXCEPTION_PARTICIPANT, "exceptionParticipant",
				RodinTestRoot.ELEMENT_TYPE);
		final IInternalElement root = makeRoot1("f");
		final IInternalElement target = makeRoot1("f2");
		// the exception is not caught underneath
		try {
			final IRefinementManager refMgr = RodinCore.getRefinementManager();
			refMgr.refine(root, target, null);
			fail("exception expected");
		} catch (RuntimeException e) {
			// as expected
		}
	}

}
