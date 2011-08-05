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
package org.eventb.internal.ui.eventbeditor.prettyprinters;

import java.util.List;

import org.eventb.core.IAction;
import org.eventb.core.IGuard;
import org.eventb.core.IParameter;
import org.eventb.internal.ui.UIUtils;
import org.eventb.ui.IImplicitChildProvider;
import org.eventb.ui.prettyprint.PrettyPrintUtils;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * Implicit child providers for the element type Event.
 */
public class EventsImplicitChildProviders {

	public static class GuardsImplicitChildProvider implements
			IImplicitChildProvider {

		@Override
		public List<? extends IInternalElement> getImplicitChildren(
				IInternalElement elt) {
			try {
				return UIUtils.getImplicitChildrenOfType(elt,
						IGuard.ELEMENT_TYPE);
			} catch (RodinDBException e) {
				PrettyPrintUtils.debugAndLogError(e,
						"Could not get implicit guards of "
								+ elt.getElementName());
			}
			return null;
		}
	}

	public static class ActionsImplicitChildProvider implements
			IImplicitChildProvider {

		@Override
		public List<? extends IInternalElement> getImplicitChildren(
				IInternalElement elt) {
			try {
				return UIUtils.getImplicitChildrenOfType(elt,
						IAction.ELEMENT_TYPE);
			} catch (RodinDBException e) {
				PrettyPrintUtils.debugAndLogError(e,
						"Could not get implicit actions of "
								+ elt.getElementName());
			}
			return null;
		}
	}

	public static class ParametersImplicitChildProvider implements
			IImplicitChildProvider {

		@Override
		public List<? extends IInternalElement> getImplicitChildren(
				IInternalElement elt) {
			try {
				return UIUtils.getImplicitChildrenOfType(elt,
						IParameter.ELEMENT_TYPE);
			} catch (RodinDBException e) {
				PrettyPrintUtils.debugAndLogError(e,
						"Could not get implicit parameters of "
								+ elt.getElementName());
			}
			return null;
		}
	}

}
