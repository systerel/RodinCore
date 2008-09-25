package org.eventb.core.indexer.tests;

import static org.eventb.core.indexer.EventBIndexUtil.DECLARATION;
import static org.eventb.core.indexer.EventBIndexUtil.REFERENCE;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IContextFile;
import org.eventb.core.indexer.ContextIndexer;
import org.eventb.core.indexer.EventBIndexUtil;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.index.IIndexer;
import org.rodinp.core.index.IOccurrence;
import org.rodinp.core.index.IOccurrenceKind;
import org.rodinp.core.index.IRodinLocation;
import org.rodinp.core.index.RodinIndexer;
import org.rodinp.core.tests.ModifyingResourceTests;
import org.rodinp.internal.core.index.Descriptor;
import org.rodinp.internal.core.index.IndexManager;
import org.rodinp.internal.core.index.Occurrence;
import org.rodinp.internal.core.index.RodinIndex;

public class ContextIndexerTests extends ModifyingResourceTests {

	// FIXME only have a client point of view
	private static final boolean DEBUG = false;

	private static class TestConstant {
		public final String elementName;
		public final String identifierString;

		public TestConstant(String identifierString) {
			this.elementName = EventBIndexUtil.getUniqueName(identifierString);
			this.identifierString = identifierString;
		}
	}

	private static class TestAxiom {
		public final String name;
		public final String pred;

		public TestAxiom(String name, String pred) {
			this.name = name;
			this.pred = pred;
		}
	}

	private static IRodinProject project;
	private static final IndexManager manager = IndexManager.getDefault();
	private static final IIndexer contextIndexer = new ContextIndexer();

	private static void addCarrierSets(IContextFile rodinFile, String... names)
			throws RodinDBException {
		for (String name : names) {
			ICarrierSet set = rodinFile.getCarrierSet(EventBIndexUtil
					.getUniqueName(name));
			set.create(null, null);
			set.setIdentifierString(name, null);
		}

	}

	private static void addConstants(IContextFile rodinFile,
			TestConstant... constants) throws RodinDBException {
		for (TestConstant c : constants) {
			IConstant constant = rodinFile.getConstant(c.elementName);
			constant.create(null, null);
			constant.setIdentifierString(c.identifierString, null);
		}
	}

	public static void addAxioms(IContextFile rodinFile, TestAxiom... axioms)
			throws RodinDBException {
		for (TestAxiom a : axioms) {
			IAxiom axiom = rodinFile.getAxiom(a.name);
			axiom.create(null, null);
			axiom.setPredicateString(a.pred, null);
			axiom.setLabel(a.name, null);
		}
	}

	public static String[] makeSList(String... strings) {
		return strings;
	}

	public ContextIndexerTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		project = createRodinProject("P");
		RodinIndexer.register(contextIndexer, IContextFile.ELEMENT_TYPE);
	}

	protected void tearDown() throws Exception {
		manager.getIndex(project).clear();
		deleteProject("P");
	}

	/**
	 * The given constant is assumed to be declared in the given file and to
	 * have exactly one reference in the given axiom at the given position.
	 * 
	 * @throws CoreException
	 */
	private Descriptor makeConstantDescriptor(TestConstant constant,
			TestAxiom axiom, IContextFile file, int start, int end)
			throws CoreException {
		IRodinProject tmpProject = createRodinProject("tmpPrj");
		IConstant tmpCst = file.getConstant(constant.elementName);
		IAxiom tmpAxm = file.getAxiom(axiom.name);

		RodinIndex tmpIndex = manager.getIndex(tmpProject);
		Descriptor descriptor = (Descriptor) tmpIndex.makeDescriptor(tmpCst,
				constant.identifierString);

		final IRodinLocation locDecl = RodinIndexer.getRodinLocation(file);
		addOccurrence(locDecl, DECLARATION, descriptor,
				contextIndexer);

		final IRodinLocation locRef = RodinIndexer.getRodinLocation(tmpAxm,
				EventBAttributes.PREDICATE_ATTRIBUTE, start, end);
		addOccurrence(locRef, REFERENCE, descriptor,
				contextIndexer);

		deleteProject(tmpProject.getElementName());

		return descriptor;

	}

	private static void addOccurrence(IRodinLocation loc,
			IOccurrenceKind kind, Descriptor descriptor, IIndexer indexer) {

		final Occurrence declaration = new Occurrence(kind, loc);
		descriptor.addOccurrence(declaration);
	}

	private static void assertIndex(Descriptor[] expectedDescriptors,
			RodinIndex index) {
		final Descriptor[] actDescs = index.getDescriptors();

		assertEquals("bad number of descriptors", expectedDescriptors.length,
				actDescs.length);

		for (Descriptor exp : expectedDescriptors) {
			Descriptor actSameElem = null;
			for (Descriptor act : actDescs) {
				if (act.getName().equals(exp.getName())) {
					actSameElem = act;
					break;
				}
			}
			assertNotNull("Missing descriptor for element " + exp.getName(),
					actSameElem);
			assertDescriptor(exp, actSameElem);
		}
	}

	/**
	 * Based on the hypothesis that there are only 2 occurrences: one
	 * declaration and one reference
	 */
	private static void assertDescriptor(Descriptor expected,
			Descriptor actual) {
		IOccurrence[] expOccs = expected.getOccurrences();
		IOccurrence[] actOccs = actual.getOccurrences();

		assertEquals("bad number of occurrences for descriptor of "
				+ actual.getName(), expOccs.length, actOccs.length);

		for (IOccurrence occ : expOccs) {
			IOccurrence actSameKind = null;
			for (IOccurrence act : actOccs) {
				if (act.getKind() == occ.getKind()) {
					actSameKind = act;
					break;
				}
			}
			assertNotNull("Missing occurrence " + occ + " in descriptor of "
					+ actual.getName(), actSameKind);
			assertOccurrence(occ, actSameKind);
		}

	}

	private static void assertOccurrence(IOccurrence expected, IOccurrence actual) {
		assertEquals("bad occurrence kind", expected.getKind(), actual
				.getKind());
		assertLocation(expected.getLocation(), actual.getLocation());
	}

	private static void assertLocation(IRodinLocation expected,
			IRodinLocation actual) {
		assertLocation(expected.getElement(), expected.getAttributeType(),
				expected.getCharStart(), expected.getCharEnd(), actual);
	}

	private static void assertLocation(IRodinElement element,
			IAttributeType attributeType, int start, int end, IRodinLocation loc) {
		assertEquals("bad container element "
				+ loc.getElement().getElementName(), element, loc.getElement());
		assertEquals("bad attribute id for " + loc, attributeType, loc
				.getAttributeType());
		assertEquals("bad start location", start, loc.getCharStart());
		assertEquals("bad end location", end, loc.getCharEnd());
	}

	private void fillTestFile(IContextFile file, List<Descriptor> descriptors)
			throws CoreException {

		final String S1 = "S1";
		final TestConstant C1 = new TestConstant("C");
		final TestConstant C2 = new TestConstant("C2");

		final TestAxiom A1 = new TestAxiom("A1", "C ∈ ℕ ∪ S1");
		final TestAxiom A2 = new TestAxiom("A2", "  C2 ∈ S1");

		addCarrierSets(file, S1);
		addConstants(file, C1, C2);
		addAxioms(file, A1, A2);

		final Descriptor C1Descriptor = makeConstantDescriptor(C1, A1, file,
				0, 1);
		final Descriptor C2Descriptor = makeConstantDescriptor(C2, A2, file,
				2, 4);

		descriptors.add(C1Descriptor);
		descriptors.add(C2Descriptor);
	}

	private static IContextFile createContextFile(final String fileName)
			throws CoreException {
		assertNotNull("project was not initialized", project);
		assertTrue("project does not exist", project.exists());

		final IRodinFile file = project.getRodinFile(fileName);
		file.create(true, null);
		IContextFile resultFile = (IContextFile) file
				.getAdapter(IContextFile.class);
		assertNotNull("could not get adapter to ContextFile", resultFile);

		return resultFile;
	}

	public void testCtxIndBasicCase() throws Exception {
		IContextFile file = createContextFile("basicCtx.buc");
		List<Descriptor> descList = new ArrayList<Descriptor>();

		fillTestFile(file, descList);

		Descriptor[] expectedResult = descList
				.toArray(new Descriptor[descList.size()]);

		manager.scheduleIndexing(file);

		RodinIndex index = manager.getIndex(file.getRodinProject());
		if (DEBUG) {
			System.out.println("Basic Case");
			System.out.println(index.toString());
		}
		assertIndex(expectedResult, index);
	}

	public void testCtxIndEmptyFile() throws Exception {
		final IContextFile emptyFile = createContextFile("empty.buc");
		final Descriptor[] expectedResult = new Descriptor[] {};

		manager.scheduleIndexing(emptyFile);

		RodinIndex index = manager.getIndex(emptyFile.getRodinProject());
		if (DEBUG) {
			System.out.println("Empty File");
			System.out.println(index.toString());
		}
		assertIndex(expectedResult, index);
	}

	// TODO add tests
}
