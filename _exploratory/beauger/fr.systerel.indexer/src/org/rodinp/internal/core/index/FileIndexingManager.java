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
package org.rodinp.internal.core.index;

import org.rodinp.core.IFileElementType;
import org.rodinp.core.IRodinDBStatus;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.index.IIndexer;
import org.rodinp.core.index.RodinIndexer;
import org.rodinp.internal.core.RodinDBStatus;

public class FileIndexingManager {

	private final IndexersRegistry indexersRegistry;

	public FileIndexingManager(IndexersRegistry indManager) {
		this.indexersRegistry = indManager;
	}

	public IRodinFile[] getDependencies(IRodinFile file) throws Throwable {
		final IFileElementType<? extends IRodinFile> fileType = file
				.getElementType();

		final IIndexer indexer = indexersRegistry.getIndexerFor(fileType);

		if (IndexManager.VERBOSE) {
			System.out.println("INDEXER: Extracting dependencies for file "
					+ file.getPath() + " with indexer " + indexer.getId());
		}
		try {
			final IRodinFile[] result = indexer.getDependencies(file);
			if (IndexManager.DEBUG) {
				System.out.println("INDEXER: Dependencies for file "
						+ file.getPath() + " are:");
				for (IRodinFile dep : result) {
					System.out.println("\t" + dep.getPath());
				}
			}
			return result;
		} catch (Throwable t) {
			if (IndexManager.DEBUG) {
				printIndexerException(file, indexer, t,
						"extracting dependencies");
			}
			IRodinDBStatus status = new RodinDBStatus(RodinIndexer.INDEXER_ERROR, t);
			RodinCore.getRodinCore().getLog().log(status);

			// propagate
			throw t;
		}
	}

	public IIndexingResult doIndexing(IRodinFile file,
			IndexingToolkit indexingToolkit) {
	
		if (!file.exists()) {
			return IndexingResult.failed(file);
		}
		
		final IFileElementType<? extends IRodinFile> fileType = file
				.getElementType();
		final IIndexer indexer = indexersRegistry.getIndexerFor(fileType);
	
		if (IndexManager.VERBOSE) {
			System.out.println("INDEXER: Indexing file " + file.getPath()
					+ " with indexer " + indexer.getId());
		}
		try {
			indexer.index(file, indexingToolkit);
	
			return indexingToolkit.getResult();
		} catch (Throwable t) {
			if (IndexManager.DEBUG) {
				printIndexerException(file, indexer, t, "indexing");
			}
			IRodinDBStatus status = new RodinDBStatus(RodinIndexer.INDEXER_ERROR, t);
			RodinCore.getRodinCore().getLog().log(status);

			return IndexingResult.failed(file);
		}
	
	}

	private void printIndexerException(IRodinFile file, IIndexer indexer,
			Throwable e, String context) {
		System.out.println("INDEXER: Exception while " + context + " in file "
				+ file.getPath() + " with indexer " + indexer.getId());
		System.out.println(e.getMessage());
	}
}
