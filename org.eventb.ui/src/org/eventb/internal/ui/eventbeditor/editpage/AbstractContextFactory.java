/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - factor IAttributeFactory code
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.editpage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IContextRoot;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

public abstract class AbstractContextFactory<E extends IInternalElement>
		implements IAttributeFactory<E> {

	public String[] getPossibleValues(E element, IProgressMonitor monitor) {
		final List<String> results = new ArrayList<String>();
		final IContextRoot[] contextRoot = getContextRoots(element);
		final Set<String> usedContextNames = getUsedContextNames(element);
		final String elementValue = getElementValue(element);
		final String currentFileName = element.getRodinFile().getBareName();

		for (IContextRoot context : contextRoot) {
			final String name = context.getComponentName();
			if (!currentFileName.equals(name)
					&& (!usedContextNames.contains(name) || name
							.equals(elementValue))) {
				results.add(name);
			}
		}
		return results.toArray(new String[results.size()]);
	}

	protected Set<String> getUsedContextNames(E element) {
		Set<String> usedNames = new HashSet<String>();
		// Then, all contexts already extended
		for (E clause : getClauses(element)) {
			try {
				if (hasValue(clause, null))
					usedNames.add(getValue(clause, null));
			} catch (RodinDBException e) {
				UIUtils.log(e, "when reading the extends clause " + clause);
			}
		}
		return usedNames;
	}

	private IContextRoot[] getContextRoots(IInternalElement element) {
		final IRodinProject rodinProject = element.getRodinProject();
		try {
			return UIUtils.getContextRootChildren(rodinProject);
		} catch (RodinDBException e) {
			UIUtils.log(e, "When computing the list of contexts of project "
					+ rodinProject);
			return new IContextRoot[0];
		}
	}

	private String getElementValue(E element) {
		try {
			if (element.exists() && hasValue(element, null))
				return getValue(element, null);
			else
				return "";
		} catch (RodinDBException e) {
			UIUtils.log(e, "When getting the value of element " + element);
			return "";
		}
	}

	protected abstract E[] getClauses(E element);
}
