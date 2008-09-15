package org.rodinp.internal.core.index.tests;

import static org.rodinp.internal.core.index.tests.IndexTestsUtil.assertDescriptor;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.assertIsEmpty;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.assertSameElements;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.createNamedElement;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.createRodinFile;

import java.util.Map;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.index.IIndexingFacade;
import org.rodinp.core.index.IRodinLocation;
import org.rodinp.core.index.OccurrenceKind;
import org.rodinp.core.index.RodinIndexer;
import org.rodinp.core.tests.AbstractRodinDBTests;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.internal.core.index.Descriptor;
import org.rodinp.internal.core.index.IndexingFacade;
import org.rodinp.internal.core.index.Occurrence;
import org.rodinp.internal.core.index.RodinIndex;
import org.rodinp.internal.core.index.tables.DependenceTable;
import org.rodinp.internal.core.index.tables.ExportTable;
import org.rodinp.internal.core.index.tables.FileTable;
import org.rodinp.internal.core.index.tables.NameTable;

public class IndexingFacadeTests extends AbstractRodinDBTests {

	// TODO verify that there is no double tests with DependenceTableUsageTests

	public IndexingFacadeTests(String name) {
		super(name);
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
	private static final DependenceTable emptyDeps = new DependenceTable();
	private static final DependenceTable f1DepsOnf2 = new DependenceTable();
	private static IndexingFacade indexingFacade1;
	private static final OccurrenceKind kind = OccurrenceKind.NULL;

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
		f1DepsOnf2.put(file1, new IRodinFile[] { file2 });

		indexingFacade1 = new IndexingFacade(file1, index, fileTable,
				nameTable, emptyExports, f1DepsOnf2);
	}

	@Override
	protected void tearDown() throws Exception {
		deleteProject("P");
		index.clear();
		fileTable.clear();
		nameTable.clear();
		f2ExportsElt2.clear();
		f1DepsOnf2.clear();
		super.tearDown();
	}

	public void testAddDeclaration() throws Exception {

		indexingFacade1.addDeclaration(elt1, name1);

		final Descriptor descriptor = index.getDescriptor(elt1);
		assertNotNull(descriptor);
		assertDescriptor(descriptor, elt1, name1, 0);
	}

	public void testDoubleDeclaration() throws Exception {
		indexingFacade1.addDeclaration(elt1, name1);
		try {
			indexingFacade1.addDeclaration(elt1, name1);
		} catch (IllegalArgumentException e) {
			return;
		}
		fail("Attempting to add a double declaration should raise IllegalArgumentException");
	}

	public void testAddDeclAlien() throws Exception {

		try {
			indexingFacade1.addDeclaration(elt2, name2);
		} catch (IllegalArgumentException e) {
			return;
		}
		fail("Declaring an alien element should raise IllegalArgumentException");
	}

	public void testAddOccurrence() throws Exception {

		indexingFacade1.addDeclaration(elt1, name1);
		indexingFacade1.addOccurrence(elt1, kind, locF1);

		final Descriptor descriptor = index.getDescriptor(elt1);
		assertNotNull(descriptor);
		assertDescriptor(descriptor, elt1, name1, 1);
	}

	public void testAddOccBadFile() throws Exception {
		indexingFacade1.addDeclaration(elt1, name1);
		try {
			indexingFacade1.addOccurrence(elt1, kind, locF2);
		} catch (IllegalArgumentException e) {
			return;
		}
		fail("Attempting to add an alien occurrence should raise IllegalArgumentException");
	}

	public void testAddOccNoDecl() throws Exception {
		try {
			indexingFacade1.addOccurrence(elt1, kind, locF1);
		} catch (IllegalArgumentException e) {
			return;
		}
		fail("Adding an occurrence to a non declared element should raise IllegalArgumentException");
	}

	public void testAddOccAlien() throws Exception {
		// alien element has a descriptor but was declared outside;
		// it must be exported by a file on which the current file
		// depends directly

		IIndexingFacade indexingFacade2 = new IndexingFacade(file2, index,
				fileTable, nameTable, f2ExportsElt2, f1DepsOnf2);
		// add a declaration of elt2 in file2
		indexingFacade2.addDeclaration(elt2, name2);
		indexingFacade2.export(elt2);

		indexingFacade1 = new IndexingFacade(file1, index, fileTable,
				nameTable, f2ExportsElt2, f1DepsOnf2);
		try {
			indexingFacade1.addOccurrence(elt2, kind, locF1);
		} catch (Exception e) {
			fail("Adding an occurrence to an alien file should not raise an exception");
		}
		final Descriptor descriptor = index.getDescriptor(elt2);
		assertNotNull(descriptor);
		assertDescriptor(descriptor, elt2, name2, 1);

	}

	public void testAddOccAlienNoDecl() throws Exception {

		IIndexingFacade indexingFacade2 = new IndexingFacade(file2,
				new RodinIndex(), fileTable, nameTable, f2ExportsElt2,
				f1DepsOnf2);
		// the declaration of elt2 in file2 will be recorded in another index;
		// it will therefore appear as not declared, whereas the rest is OK
		indexingFacade2.addDeclaration(elt2, name2);

		indexingFacade1 = new IndexingFacade(file1, index, fileTable,
				nameTable, f2ExportsElt2, f1DepsOnf2);

		try {
			indexingFacade1.addOccurrence(elt2, kind, locF1);
		} catch (IllegalArgumentException e) {
			return;
		}
		fail("Trying to add an alien occurrence to a non declared alien element should raise IllegalArgumentException");
	}

	public void testAddOccAlienNoDeps() throws Exception {

		IIndexingFacade indexingFacade2 = new IndexingFacade(file2, index,
				fileTable, nameTable, f2ExportsElt2, emptyDeps);
		// elt2 is well declared in file2 and exported from it
		// but there is no dependence from file1 to file2
		indexingFacade2.addDeclaration(elt2, name2);

		indexingFacade1 = new IndexingFacade(file1, index, fileTable,
				nameTable, f2ExportsElt2, emptyDeps);

		try {
			indexingFacade1.addOccurrence(elt2, kind, locF1);
		} catch (IllegalArgumentException e) {
			return;
		}
		fail("Trying to add an alien occurrence when there is no dependence to its file should raise IllegalArgumentException");
	}

	public void testAddOccAlienNoExp() throws Exception {

		IIndexingFacade indexingFacade2 = new IndexingFacade(file2, index,
				fileTable, nameTable, emptyExports, f1DepsOnf2);
		// elt2 is well declared in file2 but not exported from it
		// there is still a dependence from file1 to file2
		indexingFacade2.addDeclaration(elt2, name2);

		indexingFacade1 = new IndexingFacade(file1, index, fileTable,
				nameTable, emptyExports, f1DepsOnf2);

		try {
			indexingFacade1.addOccurrence(elt2, kind, locF1);
		} catch (IllegalArgumentException e) {
			return;
		}
		fail("Trying to add an alien occurrence when there is no dependence to its file should raise IllegalArgumentException");
	}

	public void testReindexKeepAlienOccs() throws Exception {

		final IIndexingFacade indexingFacade2 = new IndexingFacade(file2,
				index, fileTable, nameTable, f2ExportsElt2, f1DepsOnf2);
		// add a declaration of elt2 in file2
		indexingFacade2.addDeclaration(elt2, name2);
		indexingFacade2.export(elt2);

		indexingFacade1 = new IndexingFacade(file1, index, fileTable,
				nameTable, f2ExportsElt2, f1DepsOnf2);

		// add an alien occurrence in file1
		indexingFacade1.addOccurrence(elt2, kind, locF1);

		final Descriptor descBefore = index.getDescriptor(elt2);
		assertNotNull(descBefore);
		assertDescriptor(descBefore, elt2, name2, 1);

		// clean before reindexing file2
		index.getDescriptor(elt2).removeOccurrences(file2);
		fileTable.remove(file2);
		final IIndexingFacade indexingFacade2Bis = new IndexingFacade(file2,
				index, fileTable, nameTable, f2ExportsElt2, f1DepsOnf2);
		// reindexing file2
		indexingFacade2Bis.addDeclaration(elt2, name2);
		indexingFacade2Bis.export(elt2);

		// occurrence in file1 must have been kept
		final Descriptor descAfter = index.getDescriptor(elt2);
		assertNotNull(descAfter);
		assertDescriptor(descAfter, elt2, name2, 1);

		final Occurrence occAfter = descAfter.getOccurrences()[0];
		assertEquals("Alien Occurrence from file1 was not kept", file1,
				occAfter.getLocation().getElement());
	}

	public void testRenameWithAlienOccs() throws Exception {

		final IIndexingFacade indexingFacade2 = new IndexingFacade(file2,
				index, fileTable, nameTable, f2ExportsElt2, f1DepsOnf2);
		// add a declaration of elt2 in file2
		indexingFacade2.addDeclaration(elt2, name2);
		indexingFacade2.export(elt2);

		indexingFacade1 = new IndexingFacade(file1, index, fileTable,
				nameTable, f2ExportsElt2, f1DepsOnf2);

		// add an alien occurrence in file1
		// required to have a non null descriptor after cleaning
		indexingFacade1.addOccurrence(elt2, kind, locF1);

		final Descriptor descBefore = index.getDescriptor(elt2);
		assertNotNull(descBefore);
		assertDescriptor(descBefore, elt2, name2, 1);

		// clean before reindexing file2
		index.getDescriptor(elt2).removeOccurrences(file2);
		fileTable.remove(file2);
		final IIndexingFacade indexingFacade2Bis = new IndexingFacade(file2,
				index, fileTable, nameTable, f2ExportsElt2, f1DepsOnf2);
		// reindexing file2, renaming elt2
		final String name2Bis = name2 + "Bis";
		indexingFacade2Bis.addDeclaration(elt2, name2Bis);
		indexingFacade2Bis.export(elt2);

		// occurrence in file1 must have been kept
		final Descriptor descAfter = index.getDescriptor(elt2);
		assertNotNull(descAfter);
		assertDescriptor(descAfter, elt2, name2Bis, 1);

		final Occurrence occAfter = descAfter.getOccurrences()[0];
		assertEquals("Alien Occurrence from file1 was not kept", file1,
				occAfter.getLocation().getElement());

		// name2Bis should have replaced name2 in nameTable and exportTable
		final Map<IInternalElement, String> actExports = f2ExportsElt2
		.get(file2);
		assertEquals("Exported element " + elt2.getElementName()
				+ " was not properly renamed", name2Bis, actExports.get(elt2));

		final IInternalElement[] expName2Bis = new IInternalElement[] { elt2 };
		final IInternalElement[] actName2Bis = nameTable.getElements(name2Bis);

		assertSameElements(expName2Bis, actName2Bis);

		// there should not be anymore reference to the old name in the nameTable
		// as even if file1 still knows elt2 with name2
		final IInternalElement[] actName2 = nameTable.getElements(name2);

		assertIsEmpty(actName2);
	}
	
	
}
