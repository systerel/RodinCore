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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.rodinp.core.IFileElementType;
import org.rodinp.core.IRodinDBStatus;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.index.IIndexer;
import org.rodinp.core.index.IIndexingToolkit;
import org.rodinp.core.index.RodinIndexer;
import org.rodinp.internal.core.RodinDBStatus;

public class FileIndexingManager {

	private final IndexersRegistry indexersRegistry;

	public FileIndexingManager(IndexersRegistry indManager) {
		this.indexersRegistry = indManager;
	}

	public IRodinFile[] getDependencies(IRodinFile file) {
		final IFileElementType<?> fileType = file.getElementType();
		final IIndexer indexer = indexersRegistry.getIndexerFor(fileType);
		printVerbose(makeMessage("extracting dependencies", file, indexer));
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
			printDebug(makeMessage("Exception while extracting dependencies",
					file, indexer));

			IRodinDBStatus status = new RodinDBStatus(
					RodinIndexer.INDEXER_ERROR, t);
			RodinCore.getRodinCore().getLog().log(status);
			return null;
		}
	}

	public IIndexingResult doIndexing(IRodinFile file,
			IndexingToolkit indexingToolkit, int timeout, TimeUnit timeUnit) {

		if (!file.exists()) {
			return IndexingResult.failed(file);
		}

		final IFileElementType<?> fileType = file.getElementType();
		final IIndexer indexer = indexersRegistry.getIndexerFor(fileType);

		printVerbose(makeMessage("indexing", file, indexer));

		IIndexingResult result = IndexingResult.failed(file);

		final RunIndexing runIndexing = new RunIndexing(indexer, file,
				indexingToolkit);

		Future<?> task = taskExec.submit(runIndexing);
		try {
			try {
				task.get(timeout, timeUnit); // FIXME define better
				result = indexingToolkit.getResult();
			} catch (InterruptedException e) {
				printDebug(makeMessage("InterruptedException while indexing",
						file, indexer));
				// task will be canceled below
			} catch (TimeoutException e) {
				printDebug(makeMessage("Timeout while indexing", file, indexer));
				// task will be canceled below
			} catch (ExecutionException e) {
				// propagate
				throw e.getCause();
			} finally {
				final boolean cancel = task.cancel(true);
				if (cancel) {
					printVerbose(makeMessage("Canceling indexing ", file, indexer));
				}
			}
		} catch (Throwable t) {
			printDebug(makeMessage("Exception while indexing: "
					+ t.getMessage(), file, indexer));

			IRodinDBStatus status = new RodinDBStatus(
					RodinIndexer.INDEXER_ERROR, t);
			RodinCore.getRodinCore().getLog().log(status);
		}
		return result;
	}

	private String makeMessage(String context, IRodinFile file,
			final IIndexer indexer) {
		return "INDEXER: " + context + " : file=" + file.getPath() + " : indexer="
				+ indexer.getId();
	}

	private final ExecutorService taskExec = Executors
			.newSingleThreadExecutor();

	private static final class RunIndexing implements Runnable {
		private final IIndexer indexer;
		private final IRodinFile file;
		private final IIndexingToolkit indexingToolkit;

		public RunIndexing(IIndexer indexer, IRodinFile file,
				IIndexingToolkit indexingToolkit) {
			this.indexer = indexer;
			this.file = file;
			this.indexingToolkit = indexingToolkit;
		}

		public void run() {
			indexer.index(file, indexingToolkit);
		}

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
