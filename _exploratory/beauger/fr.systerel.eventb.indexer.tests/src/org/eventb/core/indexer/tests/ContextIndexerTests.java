package org.eventb.core.indexer.tests;

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
import org.rodinp.core.RodinDBException;
import org.rodinp.core.index.IDescriptor;
import org.rodinp.core.index.IIndexer;
import org.rodinp.core.index.IRodinIndex;
import org.rodinp.core.index.IRodinLocation;
import org.rodinp.core.index.Occurrence;
import org.rodinp.core.index.OccurrenceKind;
import org.rodinp.core.index.RodinIndexer;
import org.rodinp.core.tests.ModifyingResourceTests;
import org.rodinp.internal.core.index.IndexManager;
import org.rodinp.internal.core.index.RodinIndex;

public class ContextIndexerTests extends ModifyingResourceTests {

	private void addCarrierSets(IContextFile rodinFile, String... names)
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

	private static final FormulaFactory ff = FormulaFactory.getDefault();
	private static final IndexManager manager = IndexManager.getDefault();

	private static final String S1 = "S1";
	private static final TestConstant C1 = new TestConstant("C1");
	private static final TestConstant C2 = new TestConstant("C2");

	private static final TestAxiom A1 = new TestAxiom("A1", "C1∈ℕ∪S1");
	private static final TestAxiom A2 = new TestAxiom("A2", "  C2∈S1");
	private static IContextFile evbFile;

	private static void assertConstants(IRodinIndex index) {
		IConstant cA1 = evbFile.getConstant(C1.elementName);
		IDescriptor desc1 = index.getDescriptor(EventBIndexUtil
				.getUniqueKey(cA1));
		Occurrence[] occurrences1 = desc1.getOccurrences();
		for (Occurrence occ : occurrences1) {
			OccurrenceKind kind = occ.getKind();
			IRodinLocation loc = occ.getLocation();
			if (kind == EventBOccurrenceKind.DECLARATION) {
				assertLocation(evbFile, null, IRodinLocation.NULL_CHAR_POS,
						IRodinLocation.NULL_CHAR_POS, loc);
			} else if (kind == EventBOccurrenceKind.REFERENCE) {
				assertLocation(evbFile.getAxiom(A1.name), EventBAttributes.PREDICATE_ATTRIBUTE,
						0, 1, loc);
			} else {
				fail("unexpected occurrence" + occ);
			}
		}

		IConstant cA2 = evbFile.getConstant(C2.elementName);
		IDescriptor desc2 = index.getDescriptor(EventBIndexUtil
				.getUniqueKey(cA2));
		Occurrence[] occurrences2 = desc2.getOccurrences();
		for (Occurrence occ : occurrences2) {
			OccurrenceKind kind = occ.getKind();
			IRodinLocation loc = occ.getLocation();
			if (kind == EventBOccurrenceKind.DECLARATION) {
				assertLocation(evbFile, null, IRodinLocation.NULL_CHAR_POS,
						IRodinLocation.NULL_CHAR_POS, loc);
			} else if (kind == EventBOccurrenceKind.REFERENCE) {
				assertLocation(evbFile.getAxiom(A2.name), EventBAttributes.PREDICATE_ATTRIBUTE,
						0, 1, loc);
			} else {
				fail("unexpected occurrence" + occ);
			}
		}

	}

	private static void assertLocation(IRodinElement element,
			IAttributeType.String attributeId, int start, int end,
			IRodinLocation loc) {
		assertEquals("bad container element for " + loc, element, loc
				.getElement());
		assertEquals("bad attribute id for " + loc, attributeId, loc
				.getAttributeId());
		assertEquals("bad start location", start, loc.getCharStart());
		assertEquals("bad end location", end, loc.getCharEnd());
	}

	private IContextFile constructTestFile() throws CoreException {

		createRodinProject("P");
		// IEventBProject evbProject = (IEventBProject) rodinProject
		// .getAdapter(IEventBProject.class);

		final IRodinFile file = createRodinFile("P/constants.buc");
		final IContextFile evbFile = (IContextFile) file
				.getAdapter(IContextFile.class);

		ITypeEnvironment typeEnvironment = ff.makeTypeEnvironment();
		typeEnvironment.addGivenSet(S1); // FIXME useful ?

		addCarrierSets(evbFile, S1);
		addConstants(evbFile, C1, C2);
		addAxioms(evbFile, A1, A2);

		// file.save(null, true);
		return (IContextFile) file;
	}

	public void testShowIndex() throws Exception {
		IContextFile file = constructTestFile();
		IIndexer indexer = new ContextIndexer();
		RodinIndexer.register(indexer);
		manager.scheduleIndexing(file);
		RodinIndex index = manager.getIndex(file.getRodinProject());
		System.out.println(index.toString());
		assertConstants(index);
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

