package org.eventb.core.indexer.tests;

import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IContextFile;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.indexer.ContextIndexer;
import org.eventb.core.indexer.EventBIndexUtil;
import org.eventb.core.indexer.EventBOccurrenceKind;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.index.IDescriptor;
import org.rodinp.core.index.IIndexer;
import org.rodinp.core.index.IRodinIndex;
import org.rodinp.core.index.IRodinLocation;
import org.rodinp.core.index.Occurrence;
import org.rodinp.core.index.RodinIndexer;
import org.rodinp.core.tests.ModifyingResourceTests;
import org.rodinp.internal.core.index.IndexManager;

public class ContextIndexerTests extends ModifyingResourceTests {

	// static IIndexer fakeIndexer = new IIndexer() {
	//
	// public boolean canIndex(IRodinFile file) {
	// return false;
	// }
	//
	// public void index(IRodinFile file, IRodinIndex index) {
	// }
	// };

	private static IRodinProject project;
	private static final FormulaFactory ff = FormulaFactory.getDefault();
	private static final IndexManager manager = IndexManager.getDefault();
	private static final IIndexer contextIndexer = new ContextIndexer();

	private static final String S1 = "S1";
	private static final TestConstant C1 = new TestConstant("C");
	private static final TestConstant C2 = new TestConstant("C2");

	private static final TestAxiom A1 = new TestAxiom("A1", "C ∈ ℕ ∪ S1");
	private static final TestAxiom A2 = new TestAxiom("A2", "  C2 ∈ S1");
	private static IContextFile evbFile;

	private static IDescriptor C1Descriptor;
	private static IDescriptor C2Descriptor;

	private static IDescriptor[] expectedDescriptors;

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
		evbFile = constructTestFile();
		C1Descriptor = makeConstantDescriptor(C1, A1, evbFile, 0, 1);
		C2Descriptor = makeConstantDescriptor(C2, A2, evbFile, 2, 4);
		expectedDescriptors = new IDescriptor[] { C1Descriptor, C2Descriptor };
	}

	protected void tearDown() throws Exception {
	}

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

	/**
	 * The given constant is assumed to be declared in the given file and to
	 * have exactly one reference in the given axiom at the given position.
	 * @throws CoreException 
	 */
	private IDescriptor makeConstantDescriptor(TestConstant cst, TestAxiom axm,
			IContextFile file, int refStart, int refEnd) throws CoreException {
		IRodinProject tmpProject;
		tmpProject = createRodinProject("tmpProject");
		IConstant tmpCst = file.getConstant(cst.elementName);
		IAxiom tmpAxm = file.getAxiom(axm.name);

		IRodinIndex tmpIndex = manager.getIndex(tmpProject);
		IDescriptor descriptor = tmpIndex.makeDescriptor(tmpCst, cst.identifierString);

		final IRodinLocation locDecl = RodinIndexer.getRodinLocation(file);
		addOccurrence(locDecl, EventBOccurrenceKind.DECLARATION,
				descriptor, contextIndexer);

		final IRodinLocation locRef = RodinIndexer.getRodinLocation(tmpAxm,
				EventBAttributes.PREDICATE_ATTRIBUTE, refStart, refEnd);
		addOccurrence(locRef, EventBOccurrenceKind.REFERENCE, descriptor,
				contextIndexer);

		deleteProject(tmpProject.getElementName());

		return descriptor;

	}

	private void addOccurrence(IRodinLocation loc, EventBOccurrenceKind kind,
			IDescriptor descriptor, IIndexer indexer) {

		final Occurrence declaration = new Occurrence(kind, loc, indexer);
		descriptor.addOccurrence(declaration);
	}

	private static void assertIndex(IDescriptor[] expectedDescriptors,
			IRodinIndex actual) {
		final Collection<IDescriptor> actDescs = actual.getDescriptors();

		for (IDescriptor exp : expectedDescriptors) {
			IDescriptor actSameElem = null;
			for (IDescriptor act : actDescs) {
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
	private static void assertDescriptor(IDescriptor expected,
			IDescriptor actual) {
		Occurrence[] expOccs = expected.getOccurrences();
		Occurrence[] actOccs = actual.getOccurrences();

		assertEquals("bad number of occurrences for descriptor of "
				+ actual.getName(), expOccs.length, actOccs.length);

		for (Occurrence occ : expOccs) {
			Occurrence actSameKind = null;
			for (Occurrence act : actOccs) {
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

	private static void assertOccurrence(Occurrence expected, Occurrence actual) {
		assertEquals("bad occurrence kind", expected.getKind(), actual
				.getKind());
		assertEquals("bad indexer", expected.getIndexer(), actual.getIndexer());
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

	private IContextFile constructTestFile() throws CoreException {
		project = createRodinProject("P");

		final IRodinFile file = project.getRodinFile("constants.buc");
		file.create(false, null);
		IContextFile resultFile = (IContextFile) file.getAdapter(IContextFile.class);
		assertNotNull("could not get adapter to ContextFile", resultFile);

		addCarrierSets(resultFile, S1);
		addConstants(resultFile, C1, C2);
		addAxioms(resultFile, A1, A2);

		// resultFile.save(null, true);
		return resultFile;
	}

	public void testCtxIndBasicCase() throws Exception {
		RodinIndexer.register(contextIndexer);
		manager.scheduleIndexing(evbFile);
		IRodinIndex index = manager.getIndex(evbFile.getRodinProject());
		System.out.println(index.toString());
		assertIndex(expectedDescriptors, index);
	}
}
