/*******************************************************************************
 * Copyright (c) 2010, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.htmlpage;


import org.eventb.core.IDerivedPredicateElement;
import org.eventb.core.IEvent;
import org.eventb.internal.ui.eventbeditor.elementdesc.ElementDesc;
import org.eventb.internal.ui.eventbeditor.elementdesc.ElementDescRegistry;
import org.eventb.ui.prettyprint.IElementPrettyPrinter;
import org.eventb.ui.prettyprint.PrettyPrintUtils;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

public class CorePrettyPrintUtils {

	/**
	 * Returns the string <code>directChildId</code> if a given event child
	 * element is direct, or the string <code>implicitChildId</code> if the
	 * given event child element is implicit (i.e. is owned by a more abstract
	 * event). The string is typically HTML code with specific CSS style built
	 * before calling this method.
	 * 
	 * @param event
	 *            the event that is currently treated
	 * @param child
	 *            the element that is tested
	 * @param directChildId
	 *            the string that corresponds to the element, if the element is
	 *            direct child of the event given as parameter
	 * @param implicitChildId
	 *            the string that corresponds to the element, if the element is
	 *            implicit for the event given as parameter
	 * @return the implicit string if the element is an implicit child of the
	 *         event given as parameter, the direct string if the element is
	 *         direct child of the event given as parameter
	 */
	public static String getDirectOrImplicitChildString(IEvent event,
			IInternalElement child, String directChildId, String implicitChildId) {
		return (event.isAncestorOf(child)) ? directChildId : implicitChildId;
	}

	/**
	 * Returns the pretty printer for a given internal element or
	 * <code>null</code> if there is no pretty printer defined by the element's
	 * contribution.
	 * 
	 * @param e
	 *            the element for which we want the pretty printer
	 * @return the pretty printer or <code>null</code> if any
	 */
	public static IElementPrettyPrinter getPrettyPrinter(IInternalElement e) {
		final ElementDesc desc = ElementDescRegistry.getInstance()
				.getElementDesc(e);
		return desc.getPrettyPrinter();
	}

	public static boolean isTheorem(IDerivedPredicateElement elem) {
		try {
			return elem.isTheorem();
		} catch (RodinDBException e) {
			PrettyPrintUtils.debugAndLogError(e, "Could not verify if "
					+ elem.getElementName() + " is a theorem");
		}
		return false;
	}

}
