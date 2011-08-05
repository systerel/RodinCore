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

import static org.eventb.internal.ui.proofinformation.ProofInformationItem.getItem;

import java.util.List;

import org.rodinp.core.IRodinElement;

/**
 * Used to display an element as a html listed item (i.e. using li markup)
 * @author "Thomas Muller"
 */
public class ProofInformationListItem {

	private static final String HEADER = "<li style=\"text\" value=\"\" bindent=\"";
	private static final String HEADER_END = "\">";
	private static final String FOOTER = "</li>";
	private static final int DEFAULT_INDENT = 10;
	public ProofInformationItem item;

	private ProofInformationListItem(IRodinElement element) {
		this.item = getItem(element);
	}

	private String getItemText(IRodinElement element, int level) {
		final ProofInformationListItem lItem = new ProofInformationListItem(
				element);
		final StringBuilder strb = new StringBuilder();
		strb.append(HEADER);
		strb.append(level * DEFAULT_INDENT);
		strb.append(HEADER_END);
		strb.append(lItem.item.getInfoText());
		strb.append(FOOTER);
		return strb.toString();
	}

	public static String getInfo(IRodinElement element, int level) {
		final ProofInformationListItem i = new ProofInformationListItem(element);
		return i.getItemText(element, level);
	}

	public static String getInfo(List<? extends IRodinElement> elements,
			int level) {

		final StringBuilder strb = new StringBuilder();
		strb.append(HEADER);
		strb.append(level * DEFAULT_INDENT);
		strb.append(HEADER_END);
		String sep = "";
		for (IRodinElement elem : elements) {
			strb.append(sep);
			sep = ", ";
			final ProofInformationItem litem = getItem(elem);
			if (litem != null)
				strb.append(litem.getInfoText());
		}
		strb.append(FOOTER);
		return strb.toString();
	}

}