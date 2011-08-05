/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.proofinformation;

import org.eventb.core.IEventBRoot;
import org.eventb.core.basis.EventBElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;

/**
 * Used to display a root element as a html bullet listed item (i.e. by using li
 * markup with bullet style and displaying the root name).
 * 
 * @author "Thomas Muller"
 */
public class ProofInformationRootItem {

	private static final String EVENT_B_PREFIX = "Event-B ";
	private static final String IN = " in ";
	private static final String HEADER = "<li style=\"bullet\">";
	private static final String FOOTER = "</li>";
	private static final int ROOT_LEVEL = 1;
	private final IRodinElement element;

	private ProofInformationRootItem(IRodinElement element) {
		this.element = element;
	}

	private String getItemtext() {
		if (!(element instanceof EventBElement)) {
			return "";
		}
		final EventBElement ebElement = (EventBElement) element;
		final IEventBRoot root = (IEventBRoot) ebElement.getRoot();
		final String componentName = root.getComponentName();
		return HEADER + getSimpleTypeName(ebElement.getElementType()) + IN
				+ componentName + FOOTER
				+ ProofInformationListItem.getInfo(element, ROOT_LEVEL);
	}

	private static String getSimpleTypeName(IInternalElementType<?> type) {
		final String fullName = type.getName();
		final String[] split = fullName.split(EVENT_B_PREFIX);
		if (split.length == 2) {
			return split[1];
		}
		return fullName;
	}

	public static String getInfo(IRodinElement element) {
		final ProofInformationRootItem i = new ProofInformationRootItem(element);
		return i.getItemtext();
	}

}