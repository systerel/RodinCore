/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.core.tests.index.persistence;

import static org.rodinp.core.tests.index.IndexTestsUtil.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.index.IDeclaration;
import org.rodinp.core.index.IOccurrence;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.internal.core.index.Declaration;
import org.rodinp.internal.core.index.Descriptor;
import org.rodinp.internal.core.index.IIndexDelta;
import org.rodinp.internal.core.index.IndexDelta;
import org.rodinp.internal.core.index.PerProjectPIM;
import org.rodinp.internal.core.index.ProjectIndexManager;
import org.rodinp.internal.core.index.Registry;
import org.rodinp.internal.core.index.IIndexDelta.Kind;
import org.rodinp.internal.core.index.persistence.PersistentIndexManager;
import org.rodinp.internal.core.index.sort.TotalOrder;
import org.rodinp.internal.core.index.tables.ExportTable;
import org.rodinp.internal.core.index.tables.FileTable;
import org.rodinp.internal.core.index.tables.NameTable;
import org.rodinp.internal.core.index.tables.RodinIndex;

/**
 * @author Nicolas Beauger
 * 
 */
public class Resources {

	public static interface IPersistResource {
		PersistentIndexManager getIMData();

		List<IRodinFile> getRodinFiles();

		List<String> getNames();
	}

	private static class PersistResource implements IPersistResource {
		private final PerProjectPIM pppim;
		private final List<IIndexDelta> deltas;
		private final List<IRodinFile> rodinFiles;
		private final List<String> names;

		public PersistResource() {
			pppim = new PerProjectPIM();
			deltas = new ArrayList<IIndexDelta>();
			rodinFiles = new ArrayList<IRodinFile>();
			names = new ArrayList<String>();
		}

		public PersistentIndexManager getIMData() {
			return new PersistentIndexManager(pppim, deltas,
					new Registry<String, String>());
		}

		public List<String> getNames() {
			return names;
		}

		public List<IRodinFile> getRodinFiles() {
			return rodinFiles;
		}
	}

	public static final IPersistResource EMPTY_RESOURCE = new PersistResource();

	public static File makeBasicFile() throws Exception {
		final String xml =
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
						+ "<index_root>"
						+ "<pim project=\"/P\">"
						+ "<rodin_index>"
						+ "<descriptor element=\"/P/F1.test|org.rodinp.core.tests.test#F1|org.rodinp.core.tests.namedElement#intName1\" name=\"name1\">"
						+ "<occurrence element=\"/P/F1.test|org.rodinp.core.tests.test#F1\" kind=\"testKind\"/>"
						+ "</descriptor>"
						+ "</rodin_index>"
						+ "<export_table>"
						+ "<export file=\"/P/F1.test\">"
						+ "<exported element=\"/P/F1.test|org.rodinp.core.tests.test#F1|org.rodinp.core.tests.namedElement#intName1\" name=\"name1\"/>"
						+ "</export>"
						+ "</export_table>"
						+ "<graph is_sorted=\"false\">"
						+ "<node label=\"/P/F1.test\" mark=\"true\" order_pos=\"-1\"/>"
						+ "</graph>"
						+ "</pim>"
						+ "<delta_list/>"
						+ "<indexer_registry/>"
						+ "</index_root>";
		final File file = getNewFile("basic");

		write(file, xml);

		return file;
	}

	public static IPersistResource makeBasic(IRodinProject project)
			throws Exception {

		final PersistResource pr = new PersistResource();
		final String testElt1Name = "name1";
		final String intName1 = "intName1";

		final IRodinFile rodinFile = createRodinFile(project, "F1.test");
		final NamedElement testElt1 = createNamedElement(rodinFile, intName1);
		final IDeclaration declaration =
				new Declaration(testElt1, testElt1Name);
		final IOccurrence occurrence =
				createDefaultOccurrence(rodinFile.getRoot(), declaration);

		final PerProjectPIM pppim = pr.getIMData().getPPPIM();
		final ProjectIndexManager pim = pppim.getOrCreate(project);

		final RodinIndex index = pim.getIndex();
		final ExportTable exportTable = pim.getExportTable();
		final TotalOrder<IRodinFile> order = pim.getOrder();
		final FileTable fileTable = pim.getFileTable();
		final NameTable nameTable = pim.getNameTable();

		// fill elements
		// index
		final Descriptor descriptor = index.makeDescriptor(declaration);
		descriptor.addOccurrence(occurrence);
		// export table
		exportTable.add(rodinFile, declaration);
		// order
		order.setToIter(rodinFile);
		// file table
		fileTable.add(rodinFile, testElt1);
		// name table
		nameTable.add(declaration);

		pr.getRodinFiles().add(rodinFile);
		pr.getNames().add(testElt1Name);

		return pr;
	}

	public static File makeNoPIMFile() throws Exception {
		final String xml =
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<index_root/>";

		final File file = getNewFile("noPIM");

		write(file, xml);

		return file;
	}

	public static IPersistResource makeNoPIM(IRodinProject project)
			throws Exception {

		return new PersistResource();
	}

	public static File make2PIMsFile() throws Exception {
		final String xml =
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
						+ "<index_root>"
						+ "<pim project=\"/P1\">"
						+ "<rodin_index/>"
						+ "<export_table/>"
						+ "<graph is_sorted=\"false\"/>"
						+ "</pim>"
						+ "<pim project=\"/P2\">"
						+ "<rodin_index/>"
						+ "<export_table/>"
						+ "<graph is_sorted=\"false\"/>"
						+ "</pim>"
						+ "<delta_list/>"
						+ "<indexer_registry/>"
						+ "</index_root>";
		final File file = getNewFile("2PIMs");

		write(file, xml);

		return file;
	}

	public static IPersistResource make2PIMs(IRodinProject p1, IRodinProject p2)
			throws Exception {

		final PersistResource pr = new PersistResource();

		final PerProjectPIM pppim = pr.getIMData().getPPPIM();
		pppim.getOrCreate(p1);
		pppim.getOrCreate(p2);

		return pr;
	}

	public static File makeSortedFilesFile() throws Exception {
		final String xml =
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
						+ "<index_root>"
						+ "<pim project=\"/P\">"
						+ "<rodin_index/>"
						+ "<export_table/>"
						+ "<graph is_sorted=\"true\">"
						+ "<node label=\"/P/F1.test\" mark=\"true\" order_pos=\"0\"/>"
						+ "<node label=\"/P/F2.test\" mark=\"true\" order_pos=\"1\">"
						+ "<predecessor label=\"/P/F1.test\"/>"
						+ "</node>"
						+ "<node label=\"/P/F3.test\" mark=\"true\" order_pos=\"2\">"
						+ "<predecessor label=\"/P/F1.test\"/>"
						+ "<predecessor label=\"/P/F2.test\"/>"
						+ "</node>"
						+ "</graph>"
						+ "</pim>"
						+ "<delta_list/>"
						+ "<indexer_registry/>"
						+ "</index_root>";
		final File file = getNewFile("sortedFiles");

		write(file, xml);

		return file;
	}

	public static IPersistResource makeSortedFiles(IRodinProject project)
			throws Exception {

		final PersistResource pr = new PersistResource();

		final IRodinFile file1 = createRodinFile(project, "F1.test");
		final IRodinFile file2 = createRodinFile(project, "F2.test");
		final IRodinFile file3 = createRodinFile(project, "F3.test");

		final PerProjectPIM pppim = pr.getIMData().getPPPIM();
		final ProjectIndexManager pim = pppim.getOrCreate(project);

		final TotalOrder<IRodinFile> order = pim.getOrder();

		// order
		order.setPredecessors(file2, makeArray(file1));
		order.setPredecessors(file3, makeArray(file1, file2));

		order.setToIter(file1);
		order.setToIter(file2);
		order.setToIter(file3);

		// make sorted
		order.hasNext();

		pr.getRodinFiles().add(file1);
		pr.getRodinFiles().add(file2);
		pr.getRodinFiles().add(file3);

		return pr;
	}

	public static File makeIteratingFile() throws Exception {
		final String xml =
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
						+ "<index_root>"
						+ "<pim project=\"/P\">"
						+ "<rodin_index/>"
						+ "<export_table/>"
						+ "<graph is_sorted=\"true\">"
						+ "<node label=\"/P/F1.test\" mark=\"true\" order_pos=\"0\"/>"
						+ "<node label=\"/P/F2.test\" mark=\"true\" order_pos=\"1\">"
						+ "<predecessor label=\"/P/F1.test\"/>"
						+ "</node>"
						+ "<node label=\"/P/F3.test\" mark=\"true\" order_pos=\"2\">"
						+ "<predecessor label=\"/P/F1.test\"/>"
						+ "<predecessor label=\"/P/F2.test\"/>"
						+ "</node>"
						+ "<iterated label=\"/P/F1.test\"/>"
						+ "<iterated label=\"/P/F2.test\"/>"
						+ "</graph>"
						+ "</pim>"
						+ "<delta_list/>"
						+ "<indexer_registry/>"
						+ "</index_root>";
		final File file = getNewFile("iterating");

		write(file, xml);

		return file;
	}

	public static IPersistResource makeIterating(IRodinProject project)
			throws Exception {

		final PersistResource pr = new PersistResource();

		final IRodinFile file1 = createRodinFile(project, "F1.test");
		final IRodinFile file2 = createRodinFile(project, "F2.test");
		final IRodinFile file3 = createRodinFile(project, "F3.test");

		final PerProjectPIM pppim = pr.getIMData().getPPPIM();
		final ProjectIndexManager pim = pppim.getOrCreate(project);

		final TotalOrder<IRodinFile> order = pim.getOrder();

		// fill elements

		// order
		order.setPredecessors(file2, makeArray(file1));
		order.setPredecessors(file3, makeArray(file1, file2));

		order.setToIter(file1);
		order.setToIter(file2);
		order.setToIter(file3);

		order.next();
		order.next();

		pr.getRodinFiles().add(file1);
		pr.getRodinFiles().add(file2);
		pr.getRodinFiles().add(file3);

		return pr;
	}

	public static File makeNoExportNodeFile() throws Exception {
		final String xml =
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
						+ "<index_root>"
						+ "<pim project=\"/P\">"
						+ "<rodin_index/>"
						+ "<graph is_sorted=\"false\"/>"
						+ "</pim>"
						+ "<delta_list/>"
						+ "<indexer_registry/>"
						+ "</index_root>";
		final File file = getNewFile("noExportNode");

		write(file, xml);

		return file;
	}

	public static File makeTwoRodinIndexesFile() throws Exception {
		final String xml =
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
						+ "<index_root>"
						+ "<pim project=\"/P\">"
						+ "<rodin_index/>"
						+ "<rodin_index/>"
						+ "<export_table/>"
						+ "<graph is_sorted=\"false\"/>"
						+ "</pim>"
						+ "<delta_list/>"
						+ "<indexer_registry/>"
						+ "</index_root>";
		final File file = getNewFile("twoRodinIndexes");

		write(file, xml);

		return file;
	}

	public static File makeMissingAttributeFile() throws Exception {
		// no isSorted attribute in the graph node
		final String xml =
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
						+ "<index_root>"
						+ "<pim project=\"/P\">"
						+ "<rodin_index/>"
						+ "<export_table/>"
						+ "<graph/>"
						+ "</pim>"
						+ "<delta_list/>"
						+ "<indexer_registry/>"
						+ "</index_root>";
		final File file = getNewFile("missingAttribute");

		write(file, xml);

		return file;
	}

	public static File makeBadElementHandleFile() throws Exception {
		// IRF instead of IIE in descriptor element attribute
		final String xml =
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
						+ "<index_root>"
						+ "<pim project=\"/P\">"
						+ "<rodin_index>"
						+ "<descriptor element=\"/P/F1.test\" name=\"name1\">"
						+ "<occurrence element=\"/P/F1.test|org.rodinp.core.tests.test#F1\" kind=\"testKind\"/>"
						+ "</descriptor>"
						+ "</rodin_index>"
						+ "<export_table>"
						+ "<export file=\"/P/F1.test\">"
						+ "<exported element=\"/P/F1.test|org.rodinp.core.tests.test#F1|org.rodinp.core.tests.namedElement#intName1\" name=\"name1\"/>"
						+ "</export>"
						+ "</export_table>"
						+ "<graph is_sorted=\"false\">"
						+ "<node label=\"/P/F1.test\" mark=\"true\" order_pos=\"-1\"/>"
						+ "</graph>"
						+ "</pim>"
						+ "<delta_list/>"
						+ "<indexer_registry/>"
						+ "</index_root>";
		final File file = getNewFile("badElementHandle");

		write(file, xml);

		return file;
	}

	public static File makeDeltaFile() throws Exception {
		final String xml =
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
						+ "<index_root>"
						+ "<delta_list>"
						+ "<delta kind=\"FILE_CHANGED\" element=\"/P/F1.test\"/>"
						+ "<delta kind=\"PROJECT_CLOSED\" element=\"/P\"/>"
						+ "</delta_list>"
						+ "<indexer_registry/>"
						+ "</index_root>";
		final File file = getNewFile("delta");

		write(file, xml);

		return file;
	}

	public static IPersistResource makeDelta(IRodinProject project)
			throws Exception {

		final PersistResource pr = new PersistResource();

		final IRodinFile file1 = createRodinFile(project, "F1.test");

		final Collection<IIndexDelta> deltas = pr.getIMData().getDeltas();

		deltas.add(new IndexDelta(file1, Kind.FILE_CHANGED));
		deltas.add(new IndexDelta(project, Kind.PROJECT_CLOSED));
		
		pr.getRodinFiles().add(file1);

		return pr;
	}

	public static File getNewFile(String name) throws IOException {
		final IPath path = new Path(name);
		final File file = path.toFile();
		// file.createNewFile();
		return file;
	}

	private static void write(File file, String xml)
			throws FileNotFoundException, IOException,
			UnsupportedEncodingException {
		FileOutputStream stream = new FileOutputStream(file);
		stream.write(xml.getBytes("UTF8")); //$NON-NLS-1$
		stream.close();
	}

}
