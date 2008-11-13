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

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinDBStatus;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.index.IIndexer;
import org.rodinp.core.index.IIndexingToolkit;
import org.rodinp.core.index.RodinIndexer;
import org.rodinp.internal.core.RodinDBStatus;

public class FileIndexingManager {

	// Timeout for indexer.index()
	private static final int TIMEOUT = 30;
	private static final TimeUnit UNIT = TimeUnit.SECONDS;

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

		final DependCaller callGetDeps =
				new DependCaller(indexer, file.getRoot());
		Future<IRodinFile[]> task = taskExec.submit(callGetDeps);
		try {
//			final IRodinFile[] result = indexer.getDependencies(file.getRoot());
			final IRodinFile[] result = task.get(TIMEOUT, UNIT);
			if (IndexManager.DEBUG) {
				System.out.println("INDEXER: Dependencies for file "
						+ file.getPath()
						+ " are:");
				for (IRodinFile dep : result) {
					System.out.println("\t" + dep.getPath());
				}
			}
			return result;
			
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			task.cancel(true);
			return null;

		} catch (Throwable t) {
			printDebug(makeMessage("Exception while extracting dependencies",
					file, indexer));

			IRodinDBStatus status =
					new RodinDBStatus(RodinIndexer.INDEXER_ERROR, t);
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

		final IndexCaller callIndex = new IndexCaller(indexer, indexingToolkit);
		Future<Boolean> task = taskExec.submit(callIndex);

		try {
			// final boolean success = indexer.index(indexingToolkit);
			final boolean success = task.get(TIMEOUT, UNIT);
			if (!success) {
				return IndexingResult.failed(file);
			}
			indexingToolkit.complete();
			final IIndexingResult result = indexingToolkit.getResult();
			printVerbose(makeMessage("indexing complete", file, indexer));
			printVerbose("result:\n" + result);
			return result;

		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			task.cancel(true);
			return IndexingResult.failed(file);

		} catch (Throwable t) {
			printDebug(makeMessage("Exception while indexing: "
					+ t.getMessage(), file, indexer));

			IRodinDBStatus status =
					new RodinDBStatus(RodinIndexer.INDEXER_ERROR, t);
			RodinCore.getRodinCore().getLog().log(status);
			printVerbose(makeMessage("indexing failed", file, indexer));

			return IndexingResult.failed(file);
		}
	}

	private String makeMessage(String context, IRodinFile file,
			final IIndexer indexer) {
		return "INDEXER: "
				+ context
				+ " : file="
				+ file.getPath()
				+ " : indexer="
				+ indexer.getId();
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

	private static final ExecutorService taskExec =
			Executors.newSingleThreadExecutor();

	private static final class IndexCaller implements Callable<Boolean> {
		private final IIndexer indexer;
		private final IIndexingToolkit indexingToolkit;

		public IndexCaller(IIndexer indexer, IIndexingToolkit indexingToolkit) {
			this.indexer = indexer;
			this.indexingToolkit = indexingToolkit;
		}

		public Boolean call() throws Exception {
			return indexer.index(indexingToolkit);
		}

	}

	private static final class DependCaller implements Callable<IRodinFile[]> {
		private final IIndexer indexer;
		private final IInternalElement element;

		public DependCaller(IIndexer indexer, IInternalElement element) {
			this.indexer = indexer;
			this.element = element;
		}

		public IRodinFile[] call() throws Exception {
			return indexer.getDependencies(element);
		}

	}

}
