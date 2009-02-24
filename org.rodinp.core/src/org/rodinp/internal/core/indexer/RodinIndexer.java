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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.ISaveContext;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.indexer.IOccurrenceKind;
import org.rodinp.internal.core.RodinDBManager.SavedStateProcessor;
import org.rodinp.internal.core.indexer.persistence.PersistenceManager;
import org.rodinp.internal.core.util.Util;

/**
 * Facade to the Rodin indexer.
 * 
 * @author Nicolas Beauger
 * 
 */
public class RodinIndexer {
	
	private static final Map<String, IOccurrenceKind> kinds = new HashMap<String, IOccurrenceKind>();

	private static class IndexerJob  extends Job {

		private final SavedStateProcessor processSavedState;
		
		public IndexerJob(String name, SavedStateProcessor processSavedState) {
			super(name);
			this.processSavedState = processSavedState;
		}
		
		@Override
		protected IStatus run(IProgressMonitor monitor) {
			try {
				processSavedState.join();
				IndexManager.getDefault().start(
						processSavedState.getSavedState(), monitor);
				if (monitor.isCanceled()) {
					return Status.CANCEL_STATUS;
				}
				return Status.OK_STATUS;
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return Status.CANCEL_STATUS;
			}
		}
	
	}
	
	private static IndexerJob indexerJob;
	
	private RodinIndexer() {
		// private constructor: zeroton
	}
	
	public static IOccurrenceKind getOccurrenceKind(String id) {
		return kinds.get(id);
	}

	public static void load() {
		configurePluginDebugOptions();
		registerOccurrenceKinds();
		registerIndexers();
		IndexManager.getDefault().addListeners();
	}

	private static void registerOccurrenceKinds() {
		final String occKindExtPointId = RodinCore.PLUGIN_ID + ".occurrenceKinds";

		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = reg
				.getConfigurationElementsFor(occKindExtPointId);
		for (IConfigurationElement element : extensions) {
			try {
				final String extensionId = element.getDeclaringExtension()
						.getUniqueIdentifier();

				final String id = element.getAttribute("id");
				final String name = element.getAttribute("name");
				if (id == null || name == null) {
					Util.log(null, ("Unable to get occurrence kind from "
							+ extensionId));
					continue;
				}

				addOccurrenceKind(id, name);
			} catch (Exception e) {
				Util.log(e,
						"Exception while loading occurrence kind extension");
				// continue
			}
		}

	}

	private static void addOccurrenceKind(String id, String name) {
		final OccurrenceKind kind = new OccurrenceKind(id, name);
		kinds.put(id, kind);
	}

	private static void registerIndexers() {
		final String indexerExtPointId = RodinCore.PLUGIN_ID + ".indexers";

		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = reg
				.getConfigurationElementsFor(indexerExtPointId);
		for (IConfigurationElement element : extensions) {
			try {
				final String extensionId = element.getDeclaringExtension()
						.getUniqueIdentifier();

				final String indexerId = element.getAttribute("id");
				final IInternalElementType<?> rootType = getRootAttribute(
						element, "root-element-type");
				if (indexerId == null || rootType == null) {
					Util.log(null,
							("Unable to get root type from " + extensionId));
					continue;
				}

				IndexerRegistry.getDefault().addIndexer(element, indexerId,
						rootType);
			} catch (Exception e) {
				Util.log(e, "Exception while loading indexer extension");
				// continue
			}
		}
	}

	private static IInternalElementType<?> getRootAttribute(
			IConfigurationElement element, String attributeName)
			throws Exception {
		final String rootId = element.getAttribute(attributeName);
		if (rootId == null) {
			return null;
		}
		return RodinCore.getInternalElementType(rootId);
	}

	public static void stop() {
		if (indexerJob != null) {
			indexerJob.cancel();
		}
	}

	private static void configurePluginDebugOptions() {
		if (RodinCore.getPlugin().isDebugging()) {
			String option = Platform
					.getDebugOption(RodinCore.PLUGIN_ID + "/debug/indexer");
			if (option != null)
				IndexManager.DEBUG = option.equalsIgnoreCase("true"); //$NON-NLS-1$

			option = Platform
					.getDebugOption(RodinCore.PLUGIN_ID + "/debug/indexer/verbose");
			if (option != null)
				IndexManager.VERBOSE = option.equalsIgnoreCase("true"); //$NON-NLS-1$

			option = Platform
					.getDebugOption(RodinCore.PLUGIN_ID + "/debug/indexer/disable_persistence");
			
			option = Platform
					.getDebugOption(RodinCore.PLUGIN_ID + "/debug/indexer/delta");
			if (option != null)
				DeltaQueuer.DEBUG = option.equalsIgnoreCase("true"); //$NON-NLS-1$
		}

	}

	public static void saving(ISaveContext context) throws CoreException {
		PersistenceManager.getDefault().saving(context);		
	}

	public static void startAfter(SavedStateProcessor processSavedState) {
		indexerJob = new IndexerJob("Indexer", processSavedState);
		indexerJob.setPriority(Job.DECORATE);
		indexerJob.setSystem(true);
		indexerJob.schedule();
	}

}