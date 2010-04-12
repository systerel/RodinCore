/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 ******************************************************************************/
package org.eventb.ui.prettyprint;

import org.eventb.internal.ui.eventbeditor.prettyprinters.ActionsPrettyPrinter;
import org.eventb.internal.ui.eventbeditor.prettyprinters.EventsPrettyPrinter;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.HorizontalAlignment;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.VerticalAlignement;
import org.rodinp.core.IInternalElement;

/**
 * Interface for the extension of the pretty printer, that defines a pretty
 * printer for a given element type, allowing the user to pretty print all
 * elements of that type and append special keywords or details which are not
 * part of the contribution items (e.g. properties calculated on elements). For
 * a simple behavior it is sufficient to implement the
 * <code>prettyPrint()</code> method only.
 * 
 * <pre>
 * For all editor contributions below the root:
 * The prefix (or special prefix) will be appended first for a given element type,
 * For each element of this type
 *   Then beginning details for the element will be appended
 *   The pretty print of the element will be appended
 *     ---> the recursive traverse to continue pretty printing child elements
 * EndFor
 * The ending details for each element
 * </pre>
 * <p>
 * This interface is intended to be implemented by clients.
 * </p>
 * 
 * @see DefaultPrettyPrinter
 * @author Thomas Muller
 * @since 1.3
 * 
 */
public interface IElementPrettyPrinter {

	/**
	 * Appends element pretty print to the given stream.
	 * <p>
	 * Example : This example shows the body of the prettyPrint method for the
	 * carrier set element pretty printer.
	 * </p>
	 * 
	 * <pre>
	 * if (elt instanceof ICarrierSet) {
	 * 	final ICarrierSet set = (ICarrierSet) elt;
	 * 	try {
	 * 		ps.appendSetIdentifier(wrapString(set.getIdentifierString()));
	 * 	} catch (RodinDBException e) {
	 * 		EventBEditorUtils.debugAndLogError(e,
	 * 				&quot;Cannot get the identifier string for carrier set &quot;
	 * 						+ set.getElementName());
	 * 	}
	 * }
	 * </pre>
	 * 
	 * where appendSetIdentifier is defined as follows :
	 * 
	 * <pre>
	 * private static void appendSetIdentifier(IPrettyPrintStream ps, String identifier) {
	 * 	ps.appendString(identifier, //
	 * 			getHTMLBeginForCSSClass(SET_IDENTIFIER, //
	 * 					HorizontalAlignment.LEFT, //
	 * 					VerticalAlignement.MIDDLE), //
	 * 			getHTMLEndForCSSClass(SET_IDENTIFIER, //
	 * 					HorizontalAlignment.LEFT, //
	 * 					VerticalAlignement.MIDDLE), //
	 * 			SET_IDENTIFIER_SEPARATOR_BEGIN, //
	 * 			SET_IDENTIFIER_SEPARATOR_END);
	 * }
	 * </pre>
	 * <p>
	 * The following methods are intended to be used :
	 * {@link PrettyPrintUtils#getHTMLBeginForCSSClass(String, HorizontalAlignment, VerticalAlignement)}
	 * and
	 * {@link PrettyPrintUtils#getHTMLEndForCSSClass(String, HorizontalAlignment, VerticalAlignement)}
	 * </p>
	 * 
	 * @param elt
	 *            the element that the pretty printer handles
	 * @param parent
	 *            the current parent element owning the child element with which
	 *            this pretty printer is associated
	 * @param ps
	 *            the current pretty print stream
	 */
	void prettyPrint(IInternalElement elt, IInternalElement parent,
			IPrettyPrintStream ps);

	/**
	 * Appends a custom prefix, and should be used to replace the default prefix
	 * which is contributed to be appended.
	 * <p>
	 * Implementation example:
	 * {@linkplain ActionsPrettyPrinter#appendSpecialPrefix(IInternalElement, String, IPrettyPrintStream, boolean)}
	 * </p>
	 * 
	 * @param parent
	 *            the current parent that owns the child element linked to this
	 *            pretty printer
	 * @param defaultPrefix
	 *            the default prefix that should by default be appended
	 * @param ps
	 *            the current pretty printing stream
	 * @param empty
	 *            a boolean indicating whether there is any child that this
	 *            pretty printer handles
	 * @return <code>true</code> if a special prefix has been appended,
	 *         <code>false</code> otherwise
	 */
	boolean appendSpecialPrefix(IInternalElement parent, String defaultPrefix,
			IPrettyPrintStream ps, boolean empty);

	/**
	 * Appends some details that contributors can calculate on a given element,
	 * and for which there is not representation in the data model. This method
	 * appends details before traversing the children of the given element
	 * <code>elt</code>.
	 * 
	 * @see EventsPrettyPrinter#appendEndingDetails(IInternalElement,
	 *      IPrettyPrintStream) StringBuilder)
	 * @param elt
	 *            the element for which details are displayed
	 * @param ps
	 *            the current pretty print stream
	 */
	void appendBeginningDetails(IInternalElement elt, IPrettyPrintStream ps);

	/**
	 * Appends some details that contributors can calculate on a given element,
	 * and for which there is no representation in the data model. This method
	 * appends details after having traversed the children of the given element
	 * <code>elt</code>.
	 * 
	 * @see EventsPrettyPrinter#appendBeginningDetails(IInternalElement,
	 *      IPrettyPrintStream)
	 * @param elt
	 *            the element for which details are displayed
	 * @param ps
	 *            the current pretty print stream
	 */
	void appendEndingDetails(IInternalElement elt, IPrettyPrintStream ps);

}
