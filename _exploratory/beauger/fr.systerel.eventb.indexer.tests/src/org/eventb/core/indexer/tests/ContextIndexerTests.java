package org.eventb.core.indexer.tests;

import static org.eventb.core.indexer.EventBIndexUtil.DECLARATION;
import static org.eventb.core.indexer.EventBIndexUtil.REFERENCE;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IContextFile;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.IPredicateElement;
import org.eventb.core.ITheorem;
import org.eventb.core.indexer.ContextIndexer;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IAttributedElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.index.IDeclaration;
import org.rodinp.core.index.IOccurrence;
import org.rodinp.core.index.RodinIndexer;
import org.rodinp.core.tests.AbstractRodinDBTests;
import org.rodinp.internal.core.index.Declaration;
import org.rodinp.internal.core.index.IndexManager;
import org.rodinp.internal.core.index.Occurrence;

/**
 * @author Nicolas Beauger
 * 
 */
/**
 * @author Nicolas Beauger
 * 
 */
public class ContextIndexerTests extends AbstractRodinDBTests {

	private static final String CST1 = "cst1";
	private static final String INTERNAL_ELEMENT1 = "internal_element1";
	private static final String FILE_NAME = "file.buc";
	private static IRodinProject project;
	private static final List<IDeclaration> EMPTY_DECL = Collections
			.emptyList();
	private static final List<IOccurrence> EMPTY_OCC = Collections.emptyList();

	private static void setContents(IFile file, String contents)
			throws Exception {
		final InputStream input = new ByteArrayInputStream(contents
				.getBytes("utf-8"));
		file.setContents(input, IResource.NONE, null);
	}

	private static IRodinFile createRodinFile(IRodinProject project,
			String fileName) throws RodinDBException {
		IRodinFile file = project.getRodinFile(fileName);
		file.create(true, null);
		return file;
	}

	private static void initFile(IRodinFile rodinFile, String contents)
			throws Exception {
		final IFile resource = rodinFile.getResource();
		setContents(resource, contents);
	}

	private static IContextFile createContext(IRodinProject project,
			String fileName, String contents) throws Exception {
		final IRodinFile rFile = createRodinFile(project, fileName);
		initFile(rFile, contents);
		final IContextFile context = (IContextFile) rFile
				.getAdapter(IContextFile.class);
		assertNotNull("could not get adapter to IContextFile", context);

		return context;
	}

	@SuppressWarnings("restriction")
	private IDeclaration getDeclCst(IContextFile context, String cstIntName,
			String cstName) throws RodinDBException {
		final IConstant cst = context.getConstant(cstIntName);
		final IDeclaration declCst = makeDecl(cst, cstName);

		return declCst;
	}

	@SuppressWarnings("restriction")
	private IDeclaration getDeclSet(IContextFile context, String setIntName,
			String setName) throws RodinDBException {
		final ICarrierSet set = context.getCarrierSet(setIntName);
		final IDeclaration declSet = makeDecl(set, setName);

		return declSet;
	}

	@SuppressWarnings("restriction")
	private IOccurrence makeOccDecl(final IRodinElement element) {
		final IOccurrence occDecl = new Occurrence(DECLARATION, RodinIndexer
				.getRodinLocation(element));
		return occDecl;
	}

	@SuppressWarnings("restriction")
	private IOccurrence makeOccRef(IAttributedElement element,
			IAttributeType.String attributeType, int start, int end) {
		final IOccurrence occDecl = new Occurrence(REFERENCE, RodinIndexer
				.getRodinLocation(element, attributeType, start, end));
		return occDecl;
	}

	private IOccurrence makeOccRefPred(IPredicateElement pred, int start, int end) {
		return makeOccRef(pred, EventBAttributes.PREDICATE_ATTRIBUTE, start, end);
	}

	@SuppressWarnings("restriction")
	private IDeclaration makeDecl(IIdentifierElement elt, String name) {
		final IDeclaration declCst1 = new Declaration(elt, name);
		return declCst1;
	}

	private List<IDeclaration> makeDeclList(IDeclaration... declarations) {
		final List<IDeclaration> expected = new ArrayList<IDeclaration>();
		for (IDeclaration declaration : declarations) {
			expected.add(declaration);
		}
		return expected;
	}

	private List<IOccurrence> makeOccList(IOccurrence... occurrences) {
		final List<IOccurrence> expected = new ArrayList<IOccurrence>();
		for (IOccurrence occurrence : occurrences) {
			expected.add(occurrence);
		}
		return expected;
	}

	/**
	 * @param name
	 * @throws Exception
	 */
	@SuppressWarnings("restriction")
	public ContextIndexerTests(String name) throws Exception {
		super(name);
		IndexManager.getDefault().disableIndexing();
	}

	protected void setUp() throws Exception {
		project = createRodinProject("P");
		super.setUp();
	}

	protected void tearDown() throws Exception {
		deleteProject("P");
		super.tearDown();
	}

	// TODO see if tests with carrier sets and theorems are needed (code review)

	private static final String CST_1DECL = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"1\">"
			+ "<org.eventb.core.constant name=\"internal_element1\" org.eventb.core.identifier=\"cst1\"/>"
			+ "</org.eventb.core.contextFile>";

	/**
	 * @throws Exception
	 */
	public void testDeclaration() throws Exception {
		final IContextFile context = createContext(project, FILE_NAME,
				CST_1DECL);

		final IDeclaration declCst1 = getDeclCst(context, INTERNAL_ELEMENT1, CST1);
		final List<IDeclaration> expected = makeDeclList(declCst1);

		final ToolkitStub tk = new ToolkitStub(context, EMPTY_DECL, null);

		final ContextIndexer indexer = new ContextIndexer();

		indexer.index(tk);

		tk.assertDeclarations(expected);
	}

	/**
	 * @throws Exception
	 */
	public void testOccurrence() throws Exception {
		final IContextFile context = createContext(project, FILE_NAME,
				CST_1DECL);

		final IOccurrence occDecl = makeOccDecl(context);

		final List<IOccurrence> expected = makeOccList(occDecl);
		final IConstant cst1 = context.getConstant(INTERNAL_ELEMENT1);

		final ToolkitStub tk = new ToolkitStub(context, EMPTY_DECL, null);

		final ContextIndexer indexer = new ContextIndexer();

		indexer.index(tk);

		tk.assertOccurrences(cst1, expected);

	}

	/**
	 * @throws Exception
	 */
	public void testOccurrenceOtherThanDecl() throws Exception {
		final String CST_1DECL_1REF_AXM = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"1\">"
				+ "<org.eventb.core.carrierSet name=\"internal_element1\" org.eventb.core.identifier=\"set1\"/>"
				+ "<org.eventb.core.constant name=\"internal_element1\" org.eventb.core.identifier=\"cst1\"/>"
				+ "<org.eventb.core.axiom name=\"internal_element1\" org.eventb.core.label=\"axm1\" org.eventb.core.predicate=\"cst1 ∈ set1\"/>"
				+ "</org.eventb.core.contextFile>";

		final IContextFile context = createContext(project, FILE_NAME,
				CST_1DECL_1REF_AXM);

		final IAxiom axiom = context.getAxiom(INTERNAL_ELEMENT1);
		final IOccurrence occRef = makeOccRefPred(axiom, 0, 4);

		final List<IOccurrence> expected = makeOccList(occRef);
		final IConstant cst1 = context.getConstant(INTERNAL_ELEMENT1);

		final ToolkitStub tk = new ToolkitStub(context, EMPTY_DECL, null);

		final ContextIndexer indexer = new ContextIndexer();

		indexer.index(tk);

		tk.assertOccurrencesOtherThanDecl(cst1, expected);
	}

	/**
	 * @throws Exception
	 */
	public void testDoubleOccurrenceSameElement() throws Exception {
		final String CST_1DECL_2OCC_SAME_AXM = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"1\">"
				+ "<org.eventb.core.constant name=\"internal_element1\" org.eventb.core.identifier=\"cst1\"/>"
				+ "<org.eventb.core.axiom name=\"internal_element1\" org.eventb.core.label=\"axm1\" org.eventb.core.predicate=\"cst1 = cst1\"/>"
				+ "<org.eventb.core.carrierSet name=\"internal_element1\" org.eventb.core.identifier=\"set1\"/>"
				+ "</org.eventb.core.contextFile>";

		final IContextFile context = createContext(project, FILE_NAME,
				CST_1DECL_2OCC_SAME_AXM);

		final IAxiom axiom = context.getAxiom(INTERNAL_ELEMENT1);
		final IOccurrence occRef1 = makeOccRefPred(axiom, 0, 4);
		final IOccurrence occRef2 = makeOccRefPred(axiom, 7, 11);

		final List<IOccurrence> expected = makeOccList(occRef1, occRef2);
		final IConstant cst1 = context.getConstant(INTERNAL_ELEMENT1);

		final ToolkitStub tk = new ToolkitStub(context, EMPTY_DECL, null);

		final ContextIndexer indexer = new ContextIndexer();

		indexer.index(tk);

		tk.assertOccurrencesOtherThanDecl(cst1, expected);
	}

	/**
	 * @throws Exception
	 */
	public void testExportLocal() throws Exception {
		final IContextFile context = createContext(project, FILE_NAME,
				CST_1DECL);

		final IDeclaration declCst1 = getDeclCst(context, INTERNAL_ELEMENT1, CST1);
		final List<IDeclaration> expected = makeDeclList(declCst1);

		final ToolkitStub tk = new ToolkitStub(context, EMPTY_DECL, null);

		final ContextIndexer indexer = new ContextIndexer();

		indexer.index(tk);

		tk.assertExports(expected);
	}

	/**
	 * @throws Exception
	 */
	public void testExportImported() throws Exception {
		final String EMPTY_CONTEXT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"1\"/>";

		final IContextFile exporter = createContext(project, FILE_NAME,
				CST_1DECL);

		final IDeclaration declCst1 = getDeclCst(exporter, INTERNAL_ELEMENT1, CST1);
		final List<IDeclaration> declCst1List = makeDeclList(declCst1);

		final IContextFile importer = createContext(project, FILE_NAME,
				EMPTY_CONTEXT);

		final ToolkitStub tk = new ToolkitStub(importer, declCst1List, null);

		final ContextIndexer indexer = new ContextIndexer();

		indexer.index(tk);

		tk.assertExports(declCst1List);
	}

	private static final String CST_1REF_AXM = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"1\">"
			+ "<org.eventb.core.axiom name=\"internal_element1\" org.eventb.core.comment=\"\" org.eventb.core.label=\"axm1\" org.eventb.core.predicate=\"1 &lt; cst1\"/>"
			+ "</org.eventb.core.contextFile>";

	/**
	 * @throws Exception
	 */
	public void testImportedOccurrence() throws Exception {
		final IContextFile exporter = createContext(project, FILE_NAME,
				CST_1DECL);

		final IDeclaration declCst1 = getDeclCst(exporter, INTERNAL_ELEMENT1, CST1);
		final List<IDeclaration> declCst1List = makeDeclList(declCst1);

		final IContextFile importer = createContext(project, FILE_NAME,
				CST_1REF_AXM);

		final IAxiom axiom = importer.getAxiom(INTERNAL_ELEMENT1);
		final IOccurrence occCst1 = makeOccRefPred(axiom, 4, 8);
		final List<IOccurrence> expected = makeOccList(occCst1);

		final ToolkitStub tk = new ToolkitStub(importer, declCst1List, null);

		final ContextIndexer indexer = new ContextIndexer();

		indexer.index(tk);

		tk.assertOccurrencesOtherThanDecl(declCst1.getElement(), expected);
	}

	/**
	 * @throws Exception
	 */
	public void testUnknownElement() throws Exception {
		final IContextFile independent = createContext(project, FILE_NAME,
				CST_1DECL);
		final IDeclaration declCst1 = getDeclCst(independent, INTERNAL_ELEMENT1, CST1);

		final IContextFile context = createContext(project, FILE_NAME,
				CST_1REF_AXM);

		final List<IOccurrence> expected = EMPTY_OCC;

		final ToolkitStub tk = new ToolkitStub(context, EMPTY_DECL, null);

		final ContextIndexer indexer = new ContextIndexer();

		indexer.index(tk);

		tk.assertOccurrencesOtherThanDecl(declCst1.getElement(), expected);
	}

	/**
	 * @throws Exception
	 */
	public void testTwoImportsSameName() throws Exception {
		final IContextFile exporter1 = createContext(project, FILE_NAME,
				CST_1DECL);
		final IDeclaration declCstExp1 = getDeclCst(exporter1, INTERNAL_ELEMENT1, CST1);

		final IContextFile exporter2 = createContext(project, FILE_NAME,
				CST_1DECL);
		final IDeclaration declCstExp2 = getDeclCst(exporter2, INTERNAL_ELEMENT1, CST1);

		final List<IDeclaration> declList = makeDeclList(declCstExp1,
				declCstExp2);

		final IContextFile importer = createContext(project, FILE_NAME,
				CST_1REF_AXM);

		final ToolkitStub tk = new ToolkitStub(importer, declList, null);

		final ContextIndexer indexer = new ContextIndexer();

		indexer.index(tk);

		tk.assertOccurrencesOtherThanDecl(declCstExp1.getElement(), EMPTY_OCC);
		tk.assertOccurrencesOtherThanDecl(declCstExp2.getElement(), EMPTY_OCC);
	}

	/**
	 * All other tests only check constants. This test checks a simple
	 * declaration of a carrier set. According to code structure, carrier sets
	 * are treated the same way as constants, so this test is sufficient to
	 * verify that carrier sets are also treated.
	 * 
	 * @throws Exception
	 */
	public void testDeclSet() throws Exception {
		final String SET_1DECL = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"1\">"
				+ "<org.eventb.core.carrierSet name=\"internal_element1\" org.eventb.core.identifier=\"set1\"/>"
				+ "</org.eventb.core.contextFile>";

		final String set1IntName = INTERNAL_ELEMENT1;
		final String set1Name = "set1";

		final IContextFile context = createContext(project, FILE_NAME,
				SET_1DECL);

		final IDeclaration declSet1 = getDeclSet(context, set1IntName, set1Name);
		final List<IDeclaration> expected = makeDeclList(declSet1);

		final ToolkitStub tk = new ToolkitStub(context, EMPTY_DECL, null);

		final ContextIndexer indexer = new ContextIndexer();

		indexer.index(tk);

		tk.assertDeclarations(expected);
	}

	public void testOccThm() throws Exception {
		final String CST_1DECL_1REF_THM = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
		"<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"1\">" +
		"<org.eventb.core.constant name=\"internal_element1\" org.eventb.core.identifier=\"cst1\"/>" +
		"<org.eventb.core.theorem name=\"internal_element1\" org.eventb.core.label=\"thm1\" org.eventb.core.predicate=\"∀i·i∈ℕ ⇒ cst1 = i\"/>" +
		"</org.eventb.core.contextFile>";
		
		final IContextFile context = createContext(project, FILE_NAME,
				CST_1DECL_1REF_THM);

		final ITheorem thm = context.getTheorem(INTERNAL_ELEMENT1);
		final IOccurrence occRef = makeOccRefPred(thm, 9, 13);

		final List<IOccurrence> expected = makeOccList(occRef);
		final IConstant cst1 = context.getConstant(INTERNAL_ELEMENT1);

		final ToolkitStub tk = new ToolkitStub(context, EMPTY_DECL, null);

		final ContextIndexer indexer = new ContextIndexer();

		indexer.index(tk);

		tk.assertOccurrencesOtherThanDecl(cst1, expected);
	}
	
	/**
	 * @throws Exception
	 */
	public void testMalformedXML() throws Exception {
		// constant node is not closed
		final String MALFORMED_CONTEXT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"1\">"
				+ "<org.eventb.core.constant name=\"internal_element1\" org.eventb.core.identifier=\"cst1\">"
				+ "</org.eventb.core.contextFile>";

		final IContextFile context = createContext(project, FILE_NAME,
				MALFORMED_CONTEXT);

		final ToolkitStub tk = new ToolkitStub(context, EMPTY_DECL, null);

		final ContextIndexer indexer = new ContextIndexer();

		// should not throw an exception
		indexer.index(tk);
	}

	/**
	 * @throws Exception
	 */
	public void testMissingAttribute() throws Exception {
		final String CST_1DECL_1AXM_NO_PRED_ATT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"1\">"
				+ "<org.eventb.core.axiom name=\"internal_element1\" org.eventb.core.label=\"axm1\"/>"
				+ "<org.eventb.core.constant name=\"internal_element1\" org.eventb.core.comment=\"\" org.eventb.core.identifier=\"cst1\"/>"
				+ "</org.eventb.core.contextFile>";

		final IContextFile context = createContext(project, FILE_NAME,
				CST_1DECL_1AXM_NO_PRED_ATT);

		final ToolkitStub tk = new ToolkitStub(context, EMPTY_DECL, null);

		final ContextIndexer indexer = new ContextIndexer();

		// should not throw an exception
		indexer.index(tk);
	}

	/**
	 * @throws Exception
	 */
	public void testDoesNotParse() throws Exception {
		final String CST_1DECL_1AXM_DOES_NOT_PARSE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"1\">"
				+ "<org.eventb.core.axiom name=\"internal_element1\" org.eventb.core.label=\"axm1\" org.eventb.core.predicate=\"(1&lt;\"/>"
				+ "<org.eventb.core.constant name=\"internal_element1\" org.eventb.core.identifier=\"cst1\"/>"
				+ "</org.eventb.core.contextFile>";

		final IContextFile context = createContext(project, FILE_NAME,
				CST_1DECL_1AXM_DOES_NOT_PARSE);

		final ToolkitStub tk = new ToolkitStub(context, EMPTY_DECL, null);

		final ContextIndexer indexer = new ContextIndexer();

		// should not throw an exception
		indexer.index(tk);
	}

}
