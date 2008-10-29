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
package org.rodinp.internal.core.index.persistence.tests;

import static org.rodinp.internal.core.index.tests.IndexTestsUtil.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
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
import org.rodinp.internal.core.index.PerProjectPIM;
import org.rodinp.internal.core.index.ProjectIndexManager;
import org.rodinp.internal.core.index.tables.ExportTable;
import org.rodinp.internal.core.index.tables.FileTable;
import org.rodinp.internal.core.index.tables.NameTable;
import org.rodinp.internal.core.index.tables.RodinIndex;
import org.rodinp.internal.core.index.tables.TotalOrder;

/**
 * @author Nicolas Beauger
 * 
 */
public class Resources {

	public static interface IPersistResource {
		PerProjectPIM getPPPIM();

		List<IRodinFile> getRodinFiles();

		List<String> getNames();
	}

	private static class PersistResource implements IPersistResource {
		private final PerProjectPIM pppim;
		private final List<IRodinFile> rodinFiles;
		private final List<String> names;

		public PersistResource() throws IOException {
			pppim = new PerProjectPIM();
			rodinFiles = new ArrayList<IRodinFile>();
			names = new ArrayList<String>();
		}

		public PerProjectPIM getPPPIM() {
			return pppim;
		}

		public List<String> getNames() {
			return names;
		}

		public List<IRodinFile> getRodinFiles() {
			return rodinFiles;
		}
	}

	public static File makePR1File() throws Exception {
		final String xml1 =
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
						+ "<index_root>"
						+ "<pim project=\"/P\">"
						+ "<rodin_index>"
						+ "<descriptor element=\"/P/F1.test|org.rodinp.core.tests.test#F1|org.rodinp.core.tests.namedElement#intName1\" name=\"name1\">"
						+ "<occurrence element=\"/P/F1.test|org.rodinp.core.tests.test#F1\" occ_kind=\"fr.systerel.indexer.test\"/>"
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
						+ "</index_root>";
		final File file = getNewFile("PR1");

		write(file, xml1);

		return file;
	}

	public static IPersistResource makePR1(IRodinProject project)
			throws Exception {

		final PersistResource pr1 = new PersistResource();
		final String testElt1Name = "name1";
		final String intName1 = "intName1";

		final IRodinFile rodinFile = createRodinFile(project, "F1.test");
		final NamedElement testElt1 = createNamedElement(rodinFile, intName1);
		System.out.println(rodinFile.getHandleIdentifier());
		System.out.println(testElt1.getHandleIdentifier());
		final IDeclaration declaration =
				new Declaration(testElt1, testElt1Name);
		final IOccurrence occurrence =
				createDefaultOccurrence(rodinFile.getRoot());

		final ProjectIndexManager pim = pr1.getPPPIM().getOrCreate(project);

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

		// pr1.getPPPIM().put(project, pim);
		pr1.getRodinFiles().add(rodinFile);
		pr1.getNames().add(testElt1Name);

		return pr1;
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
