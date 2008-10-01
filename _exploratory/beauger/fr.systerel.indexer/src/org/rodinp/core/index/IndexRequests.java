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

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.internal.core.index.Descriptor;
import org.rodinp.internal.core.index.IndexManager;
import org.rodinp.internal.core.index.RodinIndex;
import org.rodinp.internal.core.index.tables.NameTable;

/**
 * @author Nicolas Beauger
 * 
 */
public class IndexRequests {

	private static final String EMPTY_STRING = "";

	private static final IOccurrence[] EMPTY_OCCURRENCES = new IOccurrence[] {};

	/**
	 * Returns the currently indexed user-defined name of the given element.
	 * 
	 * @param element
	 *            the element for which to get the name.
	 * @return the name of the element if it was found in the index, else an
	 *         empty String.
	 * @see #isBusy()
	 */
	public static String getIndexName(IInternalElement element) {
		final IRodinProject project = element.getRodinProject();
		final RodinIndex index = IndexManager.getDefault().getIndex(project);

		final Descriptor descriptor = index.getDescriptor(element);

		if (descriptor == null) {
			return EMPTY_STRING;
		}
		return descriptor.getName();
	}

	/**
	 * Returns the currently indexed occurrences at which the given element was
	 * found.
	 * 
	 * @param element
	 *            the element for which to get the occurrences.
	 * @return the indexed occurrences of the element.
	 * @see #isBusy()
	 */
	public static IOccurrence[] getOccurrences(IInternalElement element) {
		final IRodinProject project = element.getRodinProject();
		final RodinIndex index = IndexManager.getDefault().getIndex(project);

		final Descriptor descriptor = index.getDescriptor(element);

		if (descriptor == null) {
			return EMPTY_OCCURRENCES;
		}
		return descriptor.getOccurrences();
	}

	/**
	 * Returns the currently indexed elements having the given user-defined
	 * name.
	 * 
	 * @param project
	 *            the project in which to search.
	 * @param name
	 *            the researched name.
	 * @return the found elements with the given name.
	 * @see #isBusy()
	 */
	public static IInternalElement[] getElements(IRodinProject project,
			String name) {
		final NameTable nameTable = IndexManager.getDefault().getNameTable(
				project);

		return nameTable.getElements(name);
	}

	/**
	 * Returns whether the indexing system is currently busy. This method should
	 * be called before any other request, as a <code>true</code> result
	 * indicates that the index database is being modified and that the current
	 * result of the requests may soon become obsolete.
	 * <p>
	 * Note that the busy state is inherently volatile, and in most cases
	 * clients cannot rely on the result of this method being valid by the time
	 * the result is obtained. For example, if isBusy returns <code>true</code>,
	 * the indexing may have actually completed by the time the method returns.
	 * All clients can infer from invoking this method is that the indexing
	 * system was recently in the returned state.
	 * 
	 * @return whether the indexing system is currently busy.
	 */
	public static boolean isBusy() {
		return IndexManager.getDefault().isBusy();
	}

}
