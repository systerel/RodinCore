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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
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

	// Timeout for IIndexer#index() and IIndexer#getDependencies()
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

	public IRodinFile[] getDependencies(IRodinFile file)
			throws IndexingException {
		final List<IIndexer> indexers = indexerRegistry.getIndexersFor(file);
		final Set<IRodinFile> result = new HashSet<IRodinFile>();
		boolean valid = false;
		for (IIndexer indexer : indexers) {
			final boolean success = addDependencies(file, indexer, result);
			if (!success)
				break;
			valid = true;
		}
		if (valid) {
			return result.toArray(new IRodinFile[result.size()]);
		} else {
			throw new IndexingException();
		}
	}

	private boolean addDependencies(IRodinFile file, IIndexer indexer,
			final Set<IRodinFile> result) {
		try {
			final IRodinFile[] deps = getDependencies(file, indexer);

			result.addAll(Arrays.asList(deps));
			return true;
		} catch (Throwable e) {
			// cast off local result
			// interruption was processed in the called method
			return false;
		}
	}

	private IRodinFile[] getDependencies(IRodinFile file, IIndexer indexer)
			throws Throwable {
		printVerbose(makeMessage("extracting dependencies", file, indexer));

		final DependCaller callGetDeps =
				new DependCaller(indexer, file.getRoot());
		Future<IRodinFile[]> task =
				Executors.newSingleThreadExecutor().submit(callGetDeps);
		try {
			final IRodinFile[] result = task.get(TIMEOUT, UNIT);
			printDebugDeps(file, result);

			return result;
		} catch (InterruptedException e) {
			task.cancel(true);
			Thread.currentThread().interrupt();
			throw e;
		} catch (Throwable t) {
			printDebug(makeMessage("Exception while extracting dependencies",
					file, indexer));

			IRodinDBStatus status =
					new RodinDBStatus(RodinIndexer.INDEXER_ERROR, t);
			RodinCore.getRodinCore().getLog().log(status);
			throw t;
		}
	}

	private void printDebugDeps(IRodinFile file, final IRodinFile[] result) {
		if (IndexManager.DEBUG) {
			System.out.println("INDEXER: Dependencies for file "
					+ file.getPath()
					+ " are:");
			for (IRodinFile dep : result) {
				System.out.println("\t" + dep.getPath());
			}
		}
	}

	public IIndexingResult doIndexing(IndexingToolkit indexingToolkit) {
		final IRodinFile file = indexingToolkit.getRodinFile();

		if (!file.exists()) {
			return IndexingResult.failed(file);
		}

		final List<IIndexer> indexers = indexerRegistry.getIndexersFor(file);
		IIndexingResult prevResult = IndexingResult.failed(file);

		for (IIndexer indexer : indexers) {
			final IIndexingResult result = doIndexing(indexer, indexingToolkit);
			if (!result.isSuccess()) {
				return prevResult;
			}
			prevResult = result;
		}
		return prevResult;
	}

	private IIndexingResult doIndexing(IIndexer indexer,
			IndexingToolkit indexingToolkit) {
		final IRodinFile file = indexingToolkit.getRodinFile();
		printVerbose(makeMessage("indexing", file, indexer));

		final IndexCaller callIndex = new IndexCaller(indexer, indexingToolkit);
		Future<Boolean> task =
				Executors.newSingleThreadExecutor().submit(callIndex);

		try {
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
