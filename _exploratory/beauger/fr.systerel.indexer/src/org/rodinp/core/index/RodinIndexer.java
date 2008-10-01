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
package org.rodinp.core.index;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.osgi.framework.BundleContext;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IAttributedElement;
import org.rodinp.core.IFileElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinCore;
import org.rodinp.internal.core.index.AttributeLocation;
import org.rodinp.internal.core.index.AttributeSubstringLocation;
import org.rodinp.internal.core.index.IndexManager;
import org.rodinp.internal.core.index.OccurrenceKind;
import org.rodinp.internal.core.index.RodinLocation;

/**
 * <em>Temporary class</em>
 * 
 * Main entry point for the Rodin indexer. Static methods of this class will
 * later be moved to {@link RodinCore}.
 * 
 * @author Nicolas Beauger
 * 
 */
public class RodinIndexer extends Plugin {

	private static final Map<String, IOccurrenceKind> kinds = new HashMap<String, IOccurrenceKind>();

	/** To be moved to {@link RodinCore} */
	public static void register(IIndexer indexer, IFileElementType<?> fileType) {
		IndexManager.getDefault().addIndexer(indexer, fileType);
	}

	/** To be moved to an extension point */
	public static IOccurrenceKind addOccurrenceKind(String id, String name) {
		final OccurrenceKind kind = new OccurrenceKind(id, name);
		kinds.put(id, kind);
		return kind;
	}

	/** To be moved to {@link RodinCore} */
	public static IOccurrenceKind getOccurrenceKind(String id) {
		return kinds.get(id);
	}

	/**
	 * Returns the location pointing at the given element.
	 * <p>
	 * This is a handle-only method.
	 * </p>
	 * 
	 * @param element
	 *            a Rodin element
	 * @return the location pointing at the given element
	 */
	public static IRodinLocation getRodinLocation(IRodinElement element) {
		return new RodinLocation(element);
	}

	/**
	 * Returns the location pointing at the attribute of the given type in the
	 * given internal element.
	 * <p>
	 * This is a handle-only method.
	 * </p>
	 * 
	 * @param element
	 *            a Rodin element
	 * @param attributeType
	 *            the type of the attribute to point at
	 * @return the location pointing at the given attribute of the given element
	 */
	public static IRodinLocation getRodinLocation(IAttributedElement element,
			IAttributeType attributeType) {
		return new AttributeLocation(element, attributeType);
	}

	/**
	 * Returns the location pointing at the specified substring of the attribute
	 * of the given type in the given internal element. The substring is
	 * specified by giving its start (index of first character) and end position
	 * (index of last character plus one)
	 * <p>
	 * This is a handle-only method.
	 * </p>
	 * 
	 * @param element
	 *            a Rodin element
	 * @param attributeType
	 *            the type of the attribute to point at
	 * @param start
	 *            the start position of the substring in the attribute value,
	 *            that is the index of the first character. Must be less than
	 *            <code>end</code>
	 * @param end
	 *            the end position of the substring in the attribute value, that
	 *            is the index of the last character plus one. Must be greater
	 *            than <code>start</code>
	 * @return the location pointing at the given attribute of the given element
	 */
	public static IRodinLocation getRodinLocation(IAttributedElement element,
			IAttributeType.String attributeType, int start, int end) {
		return new AttributeSubstringLocation(element, attributeType, start, end);
	}

	// The plug-in ID
	public static final String PLUGIN_ID = "fr.systerel.indexer";

	// The shared instance
	private static RodinIndexer plugin;

	/**
	 * The constructor
	 */
	public RodinIndexer() {
		// nothing to do
	}

	private static Job indexerJob = new Job("Indexer") {

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			IndexManager.getDefault().start(monitor);
			// TODO Auto-generated method stub
			return Status.OK_STATUS;
		}

	};

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		indexerJob.setPriority(Job.DECORATE); // TODO decide more precisely
		indexerJob.setSystem(true);
		indexerJob.schedule();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		indexerJob.cancel();
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static RodinIndexer getDefault() {
		return plugin;
	}

}
