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

import org.rodinp.core.IRodinDBStatus;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.index.IIndexer;
import org.rodinp.core.index.RodinIndexer;
import org.rodinp.internal.core.RodinDBStatus;

public class FileIndexingManager {

	private static FileIndexingManager instance;
	
	private final IndexerRegistry indexerRegistry;

	private FileIndexingManager() {
		this.indexerRegistry = IndexerRegistry.getDefault();
	}

	public static final FileIndexingManager getDefault() {
		if (instance == null) {
			instance = new FileIndexingManager();
		}
		return instance;
	}
	
	public IRodinFile[] getDependencies(IRodinFile file) {
		final IIndexer indexer = indexerRegistry.getIndexerFor(file);
		printVerbose(makeMessage("extracting dependencies", file, indexer));
		try {
			final IRodinFile[] result = indexer.getDependencies(file.getRoot());
			if (IndexManager.DEBUG) {
				System.out.println("INDEXER: Dependencies for file "
						+ file.getPath() + " are:");
				for (IRodinFile dep : result) {
					System.out.println("\t" + dep.getPath());
				}
			}
			return result;
		} catch (Throwable t) {
			printDebug(makeMessage("Exception while extracting dependencies",
					file, indexer));

			IRodinDBStatus status = new RodinDBStatus(
					RodinIndexer.INDEXER_ERROR, t);
			RodinCore.getRodinCore().getLog().log(status);
			return null;
		}
	}

	public IIndexingResult doIndexing(IndexingToolkit indexingToolkit) {
		final IRodinFile file = indexingToolkit.getRodinFile();

		if (!file.exists()) {
			return IndexingResult.failed(file);
		}

		final IIndexer indexer = indexerRegistry.getIndexerFor(file);

		printVerbose(makeMessage("indexing", file, indexer));

		final IIndexingResult result;

		try {
			final boolean success = indexer.index(indexingToolkit);
			if(!success) {
				return IndexingResult.failed(file);
			}
			indexingToolkit.complete();
			result = indexingToolkit.getResult();
			printVerbose(makeMessage("indexing complete", file, indexer));
			printVerbose("result:\n" + result);
			return result;
		} catch (Throwable t) {
			printDebug(makeMessage("Exception while indexing: "
					+ t.getMessage(), file, indexer));

			IRodinDBStatus status = new RodinDBStatus(
					RodinIndexer.INDEXER_ERROR, t);
			RodinCore.getRodinCore().getLog().log(status);
			printVerbose(makeMessage("indexing failed", file, indexer));

			return IndexingResult.failed(file);
		}
	}

	private String makeMessage(String context, IRodinFile file,
			final IIndexer indexer) {
		return "INDEXER: " + context + " : file=" + file.getPath()
				+ " : indexer=" + indexer.getId();
	}

	private void printVerbose(final String message) {
		if (IndexManager.VERBOSE) {
			System.out.println(message);
		}
	}

	private void printDebug(String message) {
		if (IndexManager.DEBUG) {
			System.out.println(message);
		}
	}
}
