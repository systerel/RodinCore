/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - replaced inherited by extended, event variable by parameter
 *     Systerel - separation of file and root element
 *     Systerel - added implicit children for events
 *     Systerel - added theorem attribute of IDerivedPredicateElement
 *     Systerel - fixed bug #2884774 : display guards marked as theorems
 *     Systerel - fixed bug #2936324 : Extends clauses in pretty print
 *     Systerel - Extracted and refactored from AstConverter
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.prettyprinters;

import static org.eventb.ui.prettyprint.PrettyPrintUtils.wrapString;

import org.eventb.core.IExtendsContext;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.ISeesContext;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.ui.prettyprint.IElementPrettyPrinter;
import org.eventb.ui.prettyprint.IPrettyPrintStream;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

public class DependenciesPrettyPrinter extends ComponentPrettyPrinter implements
		IElementPrettyPrinter {

	@Override
	public void prettyPrint(IInternalElement elt, IInternalElement parent,
			IPrettyPrintStream sb) {
		if (elt instanceof IRefinesMachine) {
			try {
				final String name;
				name = ((IRefinesMachine) elt).getAbstractMachineName();
				super.appendComponentName(sb, wrapString(name));
			} catch (RodinDBException e) {
				EventBEditorUtils.debugAndLogError(e,
						"Cannot get refines machine of "
								+ elt.getRodinFile().getElementName());
			}
		} else if (elt instanceof IExtendsContext) {
			final String name;
			try {
				name = ((IExtendsContext) elt).getAbstractContextName();
				super.appendComponentName(sb, wrapString(name));
			} catch (RodinDBException e) {
				EventBEditorUtils.debugAndLogError(e,
						"Cannot get extends context of "
								+ elt.getRodinFile().getElementName());
			}
		} else if (elt instanceof ISeesContext) {
			try {
				final String name = ((ISeesContext) elt).getSeenContextName();

				super.appendComponentName(sb, wrapString(name));
			} catch (RodinDBException e) {
				EventBEditorUtils.debugAndLogError(e,
						"Cannot get sees context of "
								+ elt.getRodinFile().getElementName());
			}
		}
	}

}
