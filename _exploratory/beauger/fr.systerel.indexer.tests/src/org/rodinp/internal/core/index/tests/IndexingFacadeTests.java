package org.rodinp.internal.core.index.tests;

import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.index.IRodinLocation;
import org.rodinp.core.index.OccurrenceKind;
import org.rodinp.core.index.RodinIndexer;
import org.rodinp.core.tests.AbstractRodinDBTests;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.internal.core.index.Descriptor;
import org.rodinp.internal.core.index.IndexingFacade;
import org.rodinp.internal.core.index.RodinIndex;
import org.rodinp.internal.core.index.tables.ExportTable;
import org.rodinp.internal.core.index.tables.FileTable;
import org.rodinp.internal.core.index.tables.NameTable;

public class IndexingFacadeTests extends AbstractRodinDBTests {

	public IndexingFacadeTests(String name) {
		super(name);
	}

	private static IRodinProject project;
	private static IRodinFile file1;
	private static IRodinFile file2;
	private static NamedElement element1;
	private static NamedElement element2;
	private static IRodinLocation loc1F1;
	private static IRodinLocation loc2F1;
	private static final String name1 = "name1";
	private static final String name2 = "name2";
	private static final RodinIndex index = new RodinIndex();
	private static final FileTable fileTable = new FileTable();
	private static final NameTable nameTable = new NameTable();
	private static final ExportTable exportTable = new ExportTable();
	private static IndexingFacade indexingFacade;
	private static final OccurrenceKind kind = OccurrenceKind.NULL;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		project = createRodinProject("P");
		file1 = IndexTestsUtil.createRodinFile(project, "indFac1.test");
		file2 = IndexTestsUtil.createRodinFile(project, "indFac2.test");
		element1 = IndexTestsUtil.createNamedElement(file1, "elt1");
		element2 = IndexTestsUtil.createNamedElement(file2, "elt2");
		loc1F1 = RodinIndexer.getRodinLocation(file1);
		loc2F1 = RodinIndexer.getRodinLocation(file1);

		indexingFacade = new IndexingFacade(file1, index, fileTable, nameTable,
				exportTable);
	}

	@Override
	protected void tearDown() throws Exception {
		deleteProject("P");
		index.clear();
		super.tearDown();
	}

	public void testAddDeclaration() throws Exception {

		indexingFacade.addDeclaration(element1, name1);

		final Descriptor descriptor = index.getDescriptor(element1);
		IndexTestsUtil.assertNotNull(descriptor);
		IndexTestsUtil.assertDescriptor(descriptor, element1, name1, 0);
	}

	public void testDoubleDeclaration() throws Exception {
		indexingFacade.addDeclaration(element1, name1);
		try {
			indexingFacade.addDeclaration(element1, name1);
		} catch (IllegalArgumentException e) {
			return;
		}
		fail("Attempting to add a double declaration should raise IllegalArgumentException");
	}

	public void testBadFile() throws Exception {
		try {
			indexingFacade.addDeclaration(element2, name2);
		} catch (IllegalArgumentException e) {
			return;
		}
		fail("Attempting to declare an alien element should raise IllegalArgumentException");
	}

	public void testAddOccurrence() throws Exception {

		indexingFacade.addDeclaration(element1, name1);
		indexingFacade.addOccurrence(element1, kind, loc1F1);

		final Descriptor descriptor = index.getDescriptor(element1);
		IndexTestsUtil.assertNotNull(descriptor);
		IndexTestsUtil.assertDescriptor(descriptor, element1, name1, 1);
	}

	public void testAddOccNoDecl() throws Exception {
		try {
			indexingFacade.addOccurrence(element1, kind, loc1F1);
		} catch (IllegalArgumentException e) {
			return;
		}
		fail("Adding an occurrence to a non declared element should raise IllegalArgumentException");
	}

	public void testAddOccAlien() throws Exception {
		// alien element has a descriptor but was declared outside
		// TODO add a declaration of element2 in file2
		
//		indexingFacade.addOccurrence(element2, kind, loc2F1);
//		
//		final Descriptor descriptor = index.getDescriptor(element2);
//		IndexTestsUtil.assertNotNull(descriptor);
//		IndexTestsUtil.assertDescriptor(descriptor, element2, name2, 1);

	}
	
}
