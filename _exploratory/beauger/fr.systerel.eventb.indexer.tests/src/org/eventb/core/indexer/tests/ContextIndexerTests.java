package org.eventb.core.indexer.tests;

import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IContextFile;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
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
	}

	protected void tearDown() throws Exception {
	}

	private static class TestConstant {
		// public final Object key;
		public final String elementName;
		public final String identifierString;

		public TestConstant(String identifierString) {
			// this.key = EventBIndexUtil.getUniqueKey();
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
	private static final FormulaFactory ff = FormulaFactory.getDefault();
	private static final IndexManager manager = IndexManager.getDefault();
	private static final IIndexer contextIndexer = new ContextIndexer();

	private static final String S1 = "S1";
	private static final TestConstant C1 = new TestConstant("C1");
	private static final TestConstant C2 = new TestConstant("C2");

	private static final TestAxiom A1 = new TestAxiom("A1", "C1∈ℕ∪S1");
	private static final TestAxiom A2 = new TestAxiom("A2", "  C2∈S1");
	private final IContextFile evbFile = constructTestFile();

	private final IDescriptor C1Descriptor = makeConstantDescriptor(C1, A1,
			evbFile, 0, 1);
	private final IDescriptor C2Descriptor = makeConstantDescriptor(C2, A2,
			evbFile, 2, 3);

	private final IDescriptor[] expectedDescriptors = new IDescriptor[] {
			C1Descriptor, C2Descriptor };

	// private static void assertIndex(IRodinIndex index,
	// IDescriptor... descriptors) {
	// for (IDescriptor expected : descriptors) {
	// IDescriptor actual = index.getDescriptor(expected.getElement());
	//
	// }
	// }

	/**
	 * The given constant is assumed to be declared in the given file and to
	 * have exactly one reference in the given axiom at the given position.
	 */
	private IDescriptor makeConstantDescriptor(TestConstant cst, TestAxiom axm,
			IContextFile file, int refStart, int refEnd) {
		try {
			IRodinProject tmpProject;
			tmpProject = createRodinProject("tmpProject");
			// IRodinFile tmpFile = tmpProject.getRodinFile("tmpFile.buc");
			// tmpFile.create(false, null);
			// IContextFile tmpCtx = (IContextFile) tmpFile
			// .getAdapter(IContextFile.class);
			IConstant tmpCst = file.getConstant(cst.elementName);

			IAxiom tmpAxm = file.getAxiom(axm.name);

			IRodinIndex tmpIndex = manager.getIndex(tmpProject);
			tmpIndex.getDescriptors().clear(); // based on hypothesis that
			// returned collection is backed by index structure
			IDescriptor descriptor = tmpIndex.makeDescriptor(tmpCst
					.getElementName(), tmpCst);

			final IRodinLocation locDecl = RodinIndexer.getRodinLocation(file);
			addOccurrence(locDecl, EventBOccurrenceKind.DECLARATION,
					descriptor, contextIndexer);

			final IRodinLocation locRef = RodinIndexer.getRodinLocation(tmpAxm,
					EventBAttributes.PREDICATE_ATTRIBUTE, refStart, refEnd);
			addOccurrence(locRef, EventBOccurrenceKind.REFERENCE, descriptor,
					contextIndexer);

			deleteProject(tmpProject.getElementName());

			return descriptor;

		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();// private static void assertIndex(IRodinIndex
			// index,
			// IDescriptor... descriptors) {
			// for (IDescriptor expected : descriptors) {
			// IDescriptor actual = index.getDescriptor(expected.getElement());
			//
			// }
			// }

			return null;
		}
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

	// private static void assertConstants(IRodinIndex index) {
	// IConstant cA1 = evbFile.getConstant(C1.elementName);
	// IDescriptor desc1 = index.getDescriptor(EventBIndexUtil
	// .getUniqueKey(cA1));
	// Occurrence[] occurrences1 = desc1.getOccurrences();
	// for (Occurrence occ : occurrences1) {
	// OccurrenceKind kind = occ.getKind();
	// IRodinLocation loc = occ.getLocation();
	// if (kind == EventBOccurrenceKind.DECLARATION) {
	// assertLocation(evbFile, null, IRodinLocation.NULL_CHAR_POS,
	// IRodinLocation.NULL_CHAR_POS, loc);
	// } else if (kind == EventBOccurrenceKind.REFERENCE) {
	// assertLocation(evbFile.getAxiom(A1.name),
	// EventBAttributes.PREDICATE_ATTRIBUTE.getId(), 0, 1, loc);
	// } else {
	// fail("unexpected occurrence" + occ);
	// }
	// }
	//
	// IConstant cA2 = evbFile.getConstant(C2.elementName);
	// IDescriptor desc2 = index.getDescriptor(EventBIndexUtil
	// .getUniqueKey(cA2));
	// Occurrence[] occurrences2 = desc2.getOccurrences();
	// for (Occurrence occ : occurrences2) {
	// OccurrenceKind kind = occ.getKind();
	// IRodinLocation loc = occ.getLocation();
	// if (kind == EventBOccurrenceKind.DECLARATION) {
	// assertLocation(evbFile, null, IRodinLocation.NULL_CHAR_POS,
	// IRodinLocation.NULL_CHAR_POS, loc);
	// } else if (kind == EventBOccurrenceKind.REFERENCE) {
	// assertLocation(evbFile.getAxiom(A2.name),
	// EventBAttributes.PREDICATE_ATTRIBUTE.getId(), 2, 3, loc);
	// } else {
	// fail("unexpected occurrence" + occ);
	// }
	// }
	//
	// }

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

	private IContextFile constructTestFile() {
		IContextFile resultFile = null;
		try {
			project = createRodinProject("P");
			// IEventBProject evbProject = (IEventBProject) rodinProject
			// .getAdapter(IEventBProject.class);

			final IRodinFile file = project.getRodinFile("constants.buc");
			file.create(false, null);
			resultFile = (IContextFile) file
			.getAdapter(IContextFile.class);
			assertNotNull("could not get adapter to ContextFile", resultFile);
			ITypeEnvironment typeEnvironment = ff.makeTypeEnvironment();
			typeEnvironment.addGivenSet(S1); // FIXME useful ?

			addCarrierSets(resultFile, S1);
			addConstants(resultFile, C1, C2);
			addAxioms(resultFile, A1, A2);

			// file.save(null, true);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultFile;
	}

	public void testShowIndex() throws Exception {
		RodinIndexer.register(contextIndexer);
		manager.scheduleIndexing(evbFile);
		IRodinIndex index = manager.getIndex(evbFile.getRodinProject());
		System.out.println(index.toString());
		// assertConstants(index);
		assertIndex(expectedDescriptors, index);
	}
}

// <?xml version="1.0" encoding="UTF-8"?>
// <org.eventb.core.contextFile>
// <org.eventb.core.constant name="internal_element1"
// org.eventb.core.identifier="cst1"/>
// <org.eventb.core.constant name="internal_element2"
// org.eventb.core.identifier="cst2"/>
// <org.eventb.core.constant name="internal_element3"
// org.eventb.core.identifier="cst3"/>
// <org.eventb.core.constant name="internal_element4"
// org.eventb.core.identifier="cst4"/>
// <org.eventb.core.constant name="internal_element5"
// org.eventb.core.identifier="cst5"/>
// <org.eventb.core.constant name="internal_element6"
// org.eventb.core.identifier="cst6"/>
// <org.eventb.core.constant name="internal_element7"
// org.eventb.core.identifier="cst7"/>
// <org.eventb.core.axiom name="internal_element7" org.eventb.core.label="axm7"
// org.eventb.core.predicate="cst1 ∈ Z"/>
// <org.eventb.core.axiom name="internal_element1" org.eventb.core.label="axm1"
// org.eventb.core.predicate="cst1 = cst2"/>
// <org.eventb.core.axiom name="internal_element2" org.eventb.core.label="axm2"
// org.eventb.core.predicate="cst2 = cst3"/>
// <org.eventb.core.axiom name="internal_element3" org.eventb.core.label="axm3"
// org.eventb.core.predicate="cst3 = cst4"/>
// <org.eventb.core.axiom name="internal_element4" org.eventb.core.label="axm4"
// org.eventb.core.predicate="cst4 = cst5"/>
// <org.eventb.core.axiom name="internal_element5" org.eventb.core.label="axm5"
// org.eventb.core.predicate="cst5 = cst6"/>
// <org.eventb.core.axiom name="internal_element6" org.eventb.core.label="axm6"
// org.eventb.core.predicate="cst6 = cst7"/>
// <org.eventb.core.theorem name="internal_element1"
// org.eventb.core.label="thm1" org.eventb.core.predicate="cst1 = cst7"/>
// <org.eventb.core.carrierSet name="internal_element1"
// org.eventb.core.identifier="Z"/>
// </org.eventb.core.contextFile>

