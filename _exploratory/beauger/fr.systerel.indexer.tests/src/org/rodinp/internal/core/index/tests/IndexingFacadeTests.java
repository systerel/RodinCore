package org.rodinp.internal.core.index.tests;

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
		file1 = IndexTestsUtil.createRodinFile(project, "indFac1.test");
		file2 = IndexTestsUtil.createRodinFile(project, "indFac2.test");
		elt1 = IndexTestsUtil.createNamedElement(file1, "elt1");
		elt2 = IndexTestsUtil.createNamedElement(file2, "elt2");
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
		super.tearDown();
	}

	public void testAddDeclaration() throws Exception {

		indexingFacade1.addDeclaration(elt1, name1);

		final Descriptor descriptor = index.getDescriptor(elt1);
		IndexTestsUtil.assertNotNull(descriptor);
		IndexTestsUtil.assertDescriptor(descriptor, elt1, name1, 0);
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
		IndexTestsUtil.assertNotNull(descriptor);
		IndexTestsUtil.assertDescriptor(descriptor, elt1, name1, 1);
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

		f1DepsOnf2.put(file1, new IRodinFile[] { file2 });
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
		IndexTestsUtil.assertNotNull(descriptor);
		IndexTestsUtil.assertDescriptor(descriptor, elt2, name2, 1);

	}

	public void testAddOccAlienNoDecl() throws Exception {

		f1DepsOnf2.put(file1, new IRodinFile[] { file2 });
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

		f1DepsOnf2.put(file1, new IRodinFile[] { file2 });
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

		f1DepsOnf2.put(file1, new IRodinFile[] { file2 });
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

}
