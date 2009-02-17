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
package org.rodinp.core.tests.indexer.persistence;

import static java.util.Arrays.asList;
import static org.rodinp.core.tests.util.IndexTestsUtil.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.indexer.IDeclaration;
import org.rodinp.core.indexer.IOccurrence;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.internal.core.indexer.Declaration;
import org.rodinp.internal.core.indexer.Descriptor;
import org.rodinp.internal.core.indexer.IIndexDelta;
import org.rodinp.internal.core.indexer.IndexDelta;
import org.rodinp.internal.core.indexer.PerProjectPIM;
import org.rodinp.internal.core.indexer.ProjectIndexManager;
import org.rodinp.internal.core.indexer.Registry;
import org.rodinp.internal.core.indexer.IIndexDelta.Kind;
import org.rodinp.internal.core.indexer.persistence.PersistentIndexManager;

/**
 * @author Nicolas Beauger
 * 
 */
public class Resources {

	public static interface IPersistResource {
		PersistentIndexManager getIMData();
		
		Map<IRodinProject, PublicPIM> getPublicPIMs();

		List<IIndexDelta> getDeltas();
		
		List<IRodinFile> getRodinFiles();

		List<String> getNames();
	}

	private static class PersistResource implements IPersistResource {
		private final PerProjectPIM pppim;
		private final Map<IRodinProject, PublicPIM> publicPIMs;
		
		private final List<IIndexDelta> deltas;
		private final List<IRodinFile> rodinFiles;
		private final List<String> names;

		public PersistResource() {
			pppim = new PerProjectPIM();
			publicPIMs = new HashMap<IRodinProject, PublicPIM>();
			deltas = new ArrayList<IIndexDelta>();
			rodinFiles = new ArrayList<IRodinFile>();
			names = new ArrayList<String>();
		}

		public PersistentIndexManager getIMData() {
			return new PersistentIndexManager(pppim, deltas,
					new Registry<String, String>());
		}

		public List<IIndexDelta> getDeltas() {
			return deltas;
		}

		public List<String> getNames() {
			return names;
		}

		public List<IRodinFile> getRodinFiles() {
			return rodinFiles;
		}

		public void putPIM(PublicPIM pim) {
			publicPIMs.put(pim.project, pim);
			pppim.put(new ProjectIndexManager(pim.project, pim.index,
					pim.fileTable, pim.nameTable, pim.exportTable, pim.order));
		}

		public Map<IRodinProject, PublicPIM> getPublicPIMs() {
			return publicPIMs;
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

		
		final PublicPIM pim = new PublicPIM(project);
		// fill elements
		// index
		final Descriptor descriptor = pim.index.makeDescriptor(declaration);
		descriptor.addOccurrence(occurrence);
		// export table
		pim.exportTable.add(rodinFile, declaration);
		// order
		pim.order.setToIter(rodinFile);
		// file table
		pim.fileTable.add(rodinFile, declaration);
		// name table
		pim.nameTable.add(declaration);

		
		pr.putPIM(pim);
		
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

		pr.putPIM(new PublicPIM(p1));
		pr.putPIM(new PublicPIM(p2));
		
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

		final PublicPIM pim = new PublicPIM(project);
		
		// order
		pim.order.setPredecessors(file2, asList(file1));
		pim.order.setPredecessors(file3, asList(file1, file2));

		pim.order.setToIter(file1);
		pim.order.setToIter(file2);
		pim.order.setToIter(file3);

		// make sorted
		pim.order.hasNext();

		pr.putPIM(pim);
		
		pr.getRodinFiles().addAll(Arrays.asList(file1, file2, file3));

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

		final PublicPIM pim = new PublicPIM(project);
		
		// order
		pim.order.setPredecessors(file2, asList(file1));
		pim.order.setPredecessors(file3, asList(file1, file2));

		pim.order.setToIter(file1);
		pim.order.setToIter(file2);
		pim.order.setToIter(file3);

		pim.order.next();
		pim.order.next();

		pr.putPIM(pim);

		pr.getRodinFiles().addAll(Arrays.asList(file1, file2, file3));

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

		final Collection<IIndexDelta> deltas = pr.getDeltas();

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
