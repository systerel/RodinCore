/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Systerel - Initial API and implementation
 *******************************************************************************/
package org.eventb.ui;

import org.eventb.core.IEventBRoot;
import org.eventb.internal.ui.eventbeditor.elementdesc.ElementDescRegistry;
import org.eventb.internal.ui.eventbeditor.elementdesc.IAttributeDesc;
import org.eventb.internal.ui.eventbeditor.operations.History;
import org.eventb.internal.ui.eventbeditor.operations.OperationFactory;
import org.eventb.internal.ui.preferences.PreferenceUtils;
import org.rodinp.core.IInternalElementType;

/**
 * Static high-level methods to headlessly manipulate the rodin model from
 * outside the Event-B editor.
 * 
 * @author Thomas Muller
 * @since 2.4
 */
public class ElementOperationDirector {

	/**
	 * Method to rename automatically all the elements of the given root of a
	 * given type with the prefix set for this element type through the
	 * preferences.
	 */
	public static void autoRenameElements(IEventBRoot root,
			IInternalElementType<?> type) {
		final String prefix = PreferenceUtils.getAutoNamePrefix(root, type);
		final IAttributeDesc desc = ElementDescRegistry.getInstance()
				.getElementDesc(type).getAutoNameAttribute();
		History.getInstance().addOperation(
				OperationFactory.renameElements(root, type,
						desc.getManipulation(), prefix));
	}
	
	
	
}
