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
package org.eventb.internal.ui.eventbeditor.manipulation;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IContextRoot;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

public abstract class AbstractContextManipulation<E extends IInternalElement>
		extends AbstractAttributeManipulation {

	private String getCurrentFileName(E element) {
		return element.getRodinFile().getBareName();
	}
	
	public String[] getPossibleValues(IRodinElement element,
			IProgressMonitor monitor) {
		final E contextElement = asContextClause(element);
		final Set<String> results = new HashSet<String>();
		final Set<String> contextNames = getContextNames(contextElement);
		final Set<String> usedContextNames = getUsedContextNames(contextElement);
		final String elementValue = getElementValue(contextElement);

		//  result = contextRoot \ (usedContextNames \ { elementValue }) 
		final Set<String> valueToRemove = new HashSet<String>();
		valueToRemove.addAll(usedContextNames);
		valueToRemove.remove(elementValue);
		
		results.addAll(contextNames);
		results.removeAll(valueToRemove);
		return results.toArray(new String[results.size()]);
	}

	public Set<String> getUsedContextNames(E element) {
		Set<String> usedNames = new HashSet<String>();
		// First add myself
		usedNames.add(getCurrentFileName(element));
		// Then, all contexts already extended
		for (E clause : getClauses(element)) {
			try {
				if (hasValue(clause, null))
					usedNames.add(getValue(clause, null));
			} catch (RodinDBException e) {
				UIUtils.log(e, "when reading clause " + clause);
			}
		}
		return usedNames;
	}

	private Set<String> getContextNames(IInternalElement element) {
		final IRodinProject rodinProject = element.getRodinProject();
		IContextRoot[] contextRoots;
		final HashSet<String> result = new HashSet<String>();
		try {
			contextRoots = rodinProject
					.getRootElementsOfType(IContextRoot.ELEMENT_TYPE);
		} catch (RodinDBException e) {
			UIUtils.log(e, "When computing the list of contexts of project "
					+ rodinProject);
			contextRoots = new IContextRoot[0];
		}
		for (IContextRoot root : contextRoots) {
			result.add(root.getComponentName());
		}
		return result;
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

	public abstract E[] getClauses(E element);

	protected abstract E asContextClause(IRodinElement element);
}
