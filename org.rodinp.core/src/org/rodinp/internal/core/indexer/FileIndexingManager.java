/*******************************************************************************
 * Copyright (c) 2008, 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.indexer;

import static org.rodinp.internal.core.indexer.IndexManager.printVerbose;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinDBStatus;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.indexer.IDeclaration;
import org.rodinp.core.indexer.IIndexer;
import org.rodinp.internal.core.RodinDBStatus;

public class FileIndexingManager {

	private static FileIndexingManager instance;

	private FileIndexingManager() {
		// singleton: private constructor
	}

	public static final FileIndexingManager getDefault() {
		if (instance == null) {
			instance = new FileIndexingManager();
		}
		return instance;
	}

	public Set<IRodinFile> getDependencies(IRodinFile file,
			IProgressMonitor monitor) throws IndexingException {
		final IndexerRegistry indexerRegistry = IndexerRegistry.getDefault();
		final List<IIndexer> indexers = indexerRegistry.getIndexersFor(file);
		final Set<IRodinFile> result = new HashSet<IRodinFile>();
		
		final ISchedulingRule fileRule = file.getSchedulingRule();
		try {
			Job.getJobManager().beginRule(fileRule, monitor);
			boolean valid = false;
			for (IIndexer indexer : indexers) {
				final boolean success = addDependencies(file, indexer, result);
				if (!success)
					break;
				valid = true;
			}
			if (valid) {
				return result;
			} else {
				throw new IndexingException();
			}
		} finally {
			Job.getJobManager().endRule(fileRule);
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
		if (IndexManager.VERBOSE)
			printVerbose(makeMessage("extracting dependencies", file, indexer));

		try {
			final IRodinFile[] result = indexer.getDependencies(file.getRoot());
			printDebugDeps(file, result);

			return result;
		} catch (Throwable t) {
			printDebug(makeMessage("Exception while extracting dependencies",
					file, indexer));

			IRodinDBStatus status =
				new RodinDBStatus(IRodinDBStatusConstants.INDEXER_ERROR, t);
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

	public IIndexingResult doIndexing(IRodinFile file,
			Map<IInternalElement, IDeclaration> fileImports,
			IProgressMonitor monitor) {
		if (!file.exists()) {
			return IndexingResult.failed(file);
		}

		final IndexingBridge bridge =
			new IndexingBridge(file, fileImports, monitor);
		final IndexerRegistry indexerRegistry = IndexerRegistry.getDefault();
		final List<IIndexer> indexers = indexerRegistry.getIndexersFor(file);
		IIndexingResult prevResult = IndexingResult.failed(file);

		final ISchedulingRule fileRule = file.getSchedulingRule();
		try {
			Job.getJobManager().beginRule(fileRule, monitor);
			for (IIndexer indexer : indexers) {
				final IIndexingResult result = doIndexing(indexer, bridge);
				if (!result.isSuccess()) {
					return prevResult;
				}
				prevResult = result;
			}
			return prevResult;
		} finally {
			Job.getJobManager().endRule(fileRule);
		}
	}

	private IIndexingResult doIndexing(IIndexer indexer,
			IndexingBridge bridge) {
		final IRodinFile file = bridge.getRodinFile();
		if (IndexManager.VERBOSE)
			printVerbose(makeMessage("indexing", file, indexer));

		try {
			final boolean success = indexer.index(bridge);
			if (!success) {
				return IndexingResult.failed(file);
			}
			bridge.complete();
			final IIndexingResult result = bridge.getResult();
			if (IndexManager.VERBOSE) {
				printVerbose(makeMessage("indexing complete", file, indexer));
				printVerbose("result:\n" + result);
			}
			return result;

		} catch (Throwable t) {
			printDebug(makeMessage("Exception while indexing: "
					+ t.getMessage(), file, indexer));

			IRodinDBStatus status =
				new RodinDBStatus(IRodinDBStatusConstants.INDEXER_ERROR, t);
			RodinCore.getRodinCore().getLog().log(status);
			if (IndexManager.VERBOSE)
				printVerbose(makeMessage("indexing failed", file, indexer));

			return IndexingResult.failed(file);
		}
	}

	private static String makeMessage(String context, IRodinFile file,
			final IIndexer indexer) {
		return "INDEXER: "
				+ context
				+ " : file="
				+ file.getPath()
				+ " : indexer="
				+ indexer.getId();
	}

	private static void printDebug(String message) {
		if (IndexManager.DEBUG) {
			System.out.println(message);
		}
	}

}
