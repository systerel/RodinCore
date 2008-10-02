package org.rodinp.internal.core.index.tests;

import static org.rodinp.internal.core.index.tests.IndexTestsUtil.TEST_KIND;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.assertDescriptor;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.assertIsEmpty;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.assertSameElements;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.createNamedElement;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.createRodinFile;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.makeIIEArray;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.index.IDeclaration;
import org.rodinp.core.index.IOccurrence;
import org.rodinp.core.index.IOccurrenceKind;
import org.rodinp.core.index.IRodinLocation;
import org.rodinp.core.index.RodinIndexer;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.internal.core.index.Declaration;
import org.rodinp.internal.core.index.Descriptor;
import org.rodinp.internal.core.index.IndexingToolkit;
import org.rodinp.internal.core.index.tables.ExportTable;
import org.rodinp.internal.core.index.tables.FileTable;
import org.rodinp.internal.core.index.tables.NameTable;
import org.rodinp.internal.core.index.tables.RodinIndex;

public class IndexingToolkitTests extends IndexTests {

	public IndexingToolkitTests(String name) {
		super(name, true);
	}

	private static IRodinProject project;
	private static IRodinFile file1;
	private static IRodinFile file2;
	private static NamedElement elt1;
	private static NamedElement elt2;
	private static IRodinLocation locF1;
	private static IRodinLocation locF2;
	private static final String name1 = "name1";
	private static final String name2 = "name2";
	private static final RodinIndex index = new RodinIndex();
	private static final FileTable fileTable = new FileTable();
	private static final NameTable nameTable = new NameTable();
	private static final ExportTable emptyExports = new ExportTable();
	private static final ExportTable f2ExportsElt2 = new ExportTable();
	// private static final DependenceTable emptyDeps = new DependenceTable();
	// private static final IRodinFile[] emptyDeps = new IRodinFile[] {};
	// private static final DependenceTable f1DepsOnf2 = new DependenceTable();
	// private static IRodinFile[] f1DepsOnf2;
	private static final Set<IDeclaration> f1ImportsElt2 = new HashSet<IDeclaration>();
	private static final Set<IDeclaration> emptyImports = Collections
			.emptySet();
	private static IndexingToolkit indexingToolkit1;
	private static final IOccurrenceKind kind = TEST_KIND;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		project = createRodinProject("P");
		file1 = createRodinFile(project, "indFac1.test");
		file2 = createRodinFile(project, "indFac2.test");
		elt1 = createNamedElement(file1, "elt1");
		elt2 = createNamedElement(file2, "elt2");
		locF1 = RodinIndexer.getRodinLocation(file1);
		locF2 = RodinIndexer.getRodinLocation(file2);

		f2ExportsElt2.add(file2, elt2, name2);
		// f1DepsOnf2.put(file1, makeIRFArray(file2));
		// f1DepsOnf2 = makeIRFArray(file2);
		final IDeclaration declaration = new Declaration(elt2, name2);
		f1ImportsElt2.add(declaration);
		indexingToolkit1 = new IndexingToolkit(file1, index, fileTable,
				nameTable, emptyExports, f1ImportsElt2);
	}

	@Override
	protected void tearDown() throws Exception {
		deleteProject("P");
		index.clear();
		fileTable.clear();
		nameTable.clear();
		f2ExportsElt2.clear();
		// f1DepsOnf2.clear();
		super.tearDown();
	}

	public void testAddDeclaration() throws Exception {

		indexingToolkit1.declare(elt1, name1);

		final Descriptor descriptor = index.getDescriptor(elt1);
		assertNotNull(descriptor);
		assertDescriptor(descriptor, elt1, name1, 0);

		final IInternalElement[] elt1Array = makeIIEArray(elt1);
		assertSameElements(elt1Array, fileTable.get(file1));
		assertSameElements(elt1Array, nameTable.getElements(name1));
	}

	public void testDoubleDeclaration() throws Exception {
		indexingToolkit1.declare(elt1, name1);
		try {
			indexingToolkit1.declare(elt1, name1);
			fail("expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// OK
		}
	}

	public void testAddDeclImport() throws Exception {

		try {
			indexingToolkit1.declare(elt2, name2);
			fail("expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// OK
		}
	}

	public void testAddOccurrence() throws Exception {

		indexingToolkit1.declare(elt1, name1);
		indexingToolkit1.addOccurrence(elt1, kind, locF1);

		final Descriptor descriptor = index.getDescriptor(elt1);
		assertNotNull(descriptor);
		assertDescriptor(descriptor, elt1, name1, 1);

		final IInternalElement[] elt1Array = makeIIEArray(elt1);
		assertSameElements(elt1Array, fileTable.get(file1));
		assertSameElements(elt1Array, nameTable.getElements(name1));
	}

	public void testAddOccBadFile() throws Exception {
		indexingToolkit1.declare(elt1, name1);
		try {
			indexingToolkit1.addOccurrence(elt1, kind, locF2);
			fail("expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// OK
		}
	}

	public void testAddOccNoDecl() throws Exception {
		try {
			indexingToolkit1.addOccurrence(elt1, kind, locF1);
			fail("expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// OK
		}
	}

	public void testAddOccImport() throws Exception {
		// Import element has a descriptor but was declared outside;
		// it must be exported by a file on which the current file
		// depends directly

		IndexingToolkit indexingToolkit2 = new IndexingToolkit(file2, index,
				fileTable, nameTable, f2ExportsElt2, emptyImports);
		// add a declaration of elt2 in file2
		indexingToolkit2.declare(elt2, name2);
		indexingToolkit2.export(elt2);

		indexingToolkit1 = new IndexingToolkit(file1, index, fileTable,
				nameTable, f2ExportsElt2, f1ImportsElt2);

		indexingToolkit1.addOccurrence(elt2, kind, locF1);

		final Descriptor descriptor = index.getDescriptor(elt2);
		assertNotNull(descriptor);
		assertDescriptor(descriptor, elt2, name2, 1);

		final IInternalElement[] elt2Array = makeIIEArray(elt2);
		assertSameElements(elt2Array, fileTable.get(file1));
		assertSameElements(elt2Array, nameTable.getElements(name2));
	}

	public void testAddOccImportNoDecl() throws Exception {

		IndexingToolkit indexingToolkit2 = new IndexingToolkit(file2,
				new RodinIndex(), fileTable, nameTable, f2ExportsElt2,
				emptyImports);
		// the declaration of elt2 in file2 will be recorded in another index;
		// it will therefore appear as not declared, whereas the rest is OK
		indexingToolkit2.declare(elt2, name2);

		indexingToolkit1 = new IndexingToolkit(file1, index, fileTable,
				nameTable, f2ExportsElt2, f1ImportsElt2);

		try {
			indexingToolkit1.addOccurrence(elt2, kind, locF1);
			fail("expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// OK
		}
	}

	public void testAddOccImportNoImport() throws Exception {

		IndexingToolkit indexingToolkit2 = new IndexingToolkit(file2, index,
				fileTable, nameTable, f2ExportsElt2, emptyImports);
		// elt2 is well declared in file2 and exported from it
		// but there is no dependence from file1 to file2
		indexingToolkit2.declare(elt2, name2);

		indexingToolkit1 = new IndexingToolkit(file1, index, fileTable,
				nameTable, f2ExportsElt2, emptyImports);

		try {
			indexingToolkit1.addOccurrence(elt2, kind, locF1);
			fail("expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// OK
		}
	}

	// public void testAddOccImportNoExp() throws Exception {
	//
	// IndexingToolkit indexingToolkit2 = new IndexingToolkit(file2, index,
	// fileTable, nameTable, emptyExports, emptyImports);
	// // elt2 is well declared in file2 but not exported from it
	// // there is still a dependence from file1 to file2
	// indexingToolkit2.declare(elt2, name2);
	//
	// indexingToolkit1 = new IndexingToolkit(file1, index, fileTable,
	// nameTable, emptyExports, f1ImportsElt2);
	//
	// try {
	// indexingToolkit1.addOccurrence(elt2, kind, locF1);
	// fail("expected IllegalArgumentException");
	// } catch (IllegalArgumentException e) {
	// return;
	// }
	// }

	public void testReindexKeepImportOccs() throws Exception {

		final IndexingToolkit indexingToolkit2 = new IndexingToolkit(file2,
				index, fileTable, nameTable, f2ExportsElt2, emptyImports);
		// add a declaration of elt2 in file2
		indexingToolkit2.declare(elt2, name2);
		indexingToolkit2.export(elt2);

		indexingToolkit1 = new IndexingToolkit(file1, index, fileTable,
				nameTable, f2ExportsElt2, f1ImportsElt2);

		// add an Import occurrence in file1
		indexingToolkit1.addOccurrence(elt2, kind, locF1);

		final Descriptor descBefore = index.getDescriptor(elt2);
		assertNotNull(descBefore);
		assertDescriptor(descBefore, elt2, name2, 1);

		final IndexingToolkit indexingToolkit2Bis = new IndexingToolkit(file2,
				index, fileTable, nameTable, f2ExportsElt2, emptyImports);
		// reindexing file2
		indexingToolkit2Bis.declare(elt2, name2);
		indexingToolkit2Bis.export(elt2);

		// occurrence in file1 must have been kept
		final Descriptor descAfter = index.getDescriptor(elt2);
		assertNotNull(descAfter);
		assertDescriptor(descAfter, elt2, name2, 1);

		final IOccurrence occAfter = descAfter.getOccurrences()[0];
		assertEquals("Import Occurrence from file1 was not kept", file1,
				occAfter.getLocation().getElement());

		final IInternalElement[] elt2Array = makeIIEArray(elt2);
		assertSameElements(elt2Array, fileTable.get(file2));
		assertSameElements(elt2Array, fileTable.get(file1));

		assertSameElements(elt2Array, nameTable.getElements(name2));
	}

	public void testRenameWithImportOccs() throws Exception {

		final IndexingToolkit indexingToolkit2 = new IndexingToolkit(file2,
				index, fileTable, nameTable, f2ExportsElt2, emptyImports);
		// add a declaration of elt2 in file2 and export it
		indexingToolkit2.declare(elt2, name2);
		indexingToolkit2.export(elt2);

		indexingToolkit1 = new IndexingToolkit(file1, index, fileTable,
				nameTable, f2ExportsElt2, f1ImportsElt2);

		// add an Import occurrence in file1
		// required to have a non null descriptor after cleaning
		indexingToolkit1.addOccurrence(elt2, kind, locF1);

		final Descriptor descBefore = index.getDescriptor(elt2);
		assertNotNull(descBefore);
		assertDescriptor(descBefore, elt2, name2, 1);

		final IndexingToolkit indexingToolkit2Bis = new IndexingToolkit(file2,
				index, fileTable, nameTable, f2ExportsElt2, emptyImports);
		// reindexing file2, renaming elt2
		final String name2Bis = name2 + "Bis";
		indexingToolkit2Bis.declare(elt2, name2Bis);
		indexingToolkit2Bis.export(elt2);

		// occurrence in file1 must have been kept
		final Descriptor descAfter = index.getDescriptor(elt2);
		assertNotNull(descAfter);

		// name2Bis should have replaced name2 in nameTable and exportTable
		final Set<IDeclaration> actExports = f2ExportsElt2.get(file2);
		IDeclaration declElt2 = null;
		for (IDeclaration decl : actExports) {
			if (decl.getElement().equals(elt2)) {
				declElt2 = decl;
			}
		}
		assertNotNull("no declaration found for " + elt2, declElt2);
		assertEquals("Exported element " + elt2 + " was not properly renamed",
				name2Bis, declElt2.getName());

		final IInternalElement[] elt2Array = makeIIEArray(elt2);

		assertSameElements(elt2Array, nameTable.getElements(name2Bis));
		assertIsEmpty(nameTable.getElements(name2));

		assertSameElements(elt2Array, fileTable.get(file1));
		assertSameElements(elt2Array, fileTable.get(file2));
	}

	public void testGetImports() throws Exception {

		final IndexingToolkit indexingToolkit2 = new IndexingToolkit(file2,
				index, fileTable, nameTable, f2ExportsElt2, emptyImports);
		// add a declaration of elt2 in file2 and export it
		indexingToolkit2.declare(elt2, name2);
		indexingToolkit2.export(elt2);

		indexingToolkit1 = new IndexingToolkit(file1, index, fileTable,
				nameTable, f2ExportsElt2, f1ImportsElt2);

		final Set<IDeclaration> imports = indexingToolkit1.getImports();
		final IDeclaration declElt2 = new Declaration(elt2, name2);
		final Set<IDeclaration> expected = new HashSet<IDeclaration>();
		expected.add(declElt2);
		assertEquals(expected, imports);
	}

	public void testGetImportsEmpty() throws Exception {
		final IndexingToolkit indexingToolkit2 = new IndexingToolkit(file2,
				index, fileTable, nameTable, f2ExportsElt2, emptyImports);

		final Set<IDeclaration> imports = indexingToolkit2.getImports();

		assertTrue("imports should be empty", imports.isEmpty());
	}
}
