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

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinDBStatus;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.index.IIndexer;
import org.rodinp.core.index.RodinIndexer;
import org.rodinp.internal.core.RodinDBStatus;

public class FileIndexingManager {

	private final IndexerRegistry indexersRegistry;

	public FileIndexingManager(IndexerRegistry indManager) {
		this.indexersRegistry = indManager;
	}

	public IRodinFile[] getDependencies(IRodinFile file) {
		final IInternalElementType<?> fileType = file.getRoot()
				.getElementType();
		final IIndexer indexer = indexersRegistry.getIndexerFor(fileType);
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

		final IInternalElementType<? extends IInternalElement> fileType = file
				.getRoot().getElementType();
		final IIndexer indexer = indexersRegistry.getIndexerFor(fileType);

		printVerbose(makeMessage("indexing", file, indexer));

		IIndexingResult result = IndexingResult.failed(file);

		try {
			indexer.index(indexingToolkit);
			indexingToolkit.complete();
			result = indexingToolkit.getResult();
		} catch (Throwable t) {
			printDebug(makeMessage("Exception while indexing: "
					+ t.getMessage(), file, indexer));

			IRodinDBStatus status = new RodinDBStatus(
					RodinIndexer.INDEXER_ERROR, t);
			RodinCore.getRodinCore().getLog().log(status);
			printVerbose(makeMessage("indexing failed", file, indexer));

			return IndexingResult.failed(file);
		}
		printVerbose(makeMessage("indexing successfully completed", file,
				indexer));
		printVerbose("result:\n" + result);
		return result;
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
