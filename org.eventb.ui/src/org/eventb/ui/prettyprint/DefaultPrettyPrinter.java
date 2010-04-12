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
package org.eventb.ui.prettyprint;

import org.rodinp.core.IInternalElement;

/**
 * Default implementation of a pretty printer defined for an element contributed
 * as an editorItem and intended to participate to the creation of the HTML
 * pretty print page
 * 
 * Intended to be sub-classed by clients, in order to redefine only a subset of
 * the {@link IElementPrettyPrinter} methods.
 * 
 * @author Thomas Muller
 * @since 1.3
 */
public class DefaultPrettyPrinter implements IElementPrettyPrinter {

	/**
	 * This method appends nothing to the pretty print stream.
	 */
	public void prettyPrint(IInternalElement elt, IInternalElement parent,
			IPrettyPrintStream ps) {
		// The default pretty printer does nothing
	}

	/**
	 * This method does not append a special prefix to the string builder and
	 * returns false to let the default prefix be used.
	 */
	public boolean appendSpecialPrefix(IInternalElement parent,
			String defaultKeyword, IPrettyPrintStream ps, boolean empty) {
		// return false to let the default prefix be used
		return false;
	}

	/**
	 * This method does not append beginning details for the given element.
	 */
	public void appendBeginningDetails(IInternalElement elt,
			IPrettyPrintStream ps) {
		// nothing to do here
	}

	/**
	 * This method does not append ending details for the given element.
	 */
	public void appendEndingDetails(IInternalElement elt, IPrettyPrintStream ps) {
		// nothing to do here
	}

}
