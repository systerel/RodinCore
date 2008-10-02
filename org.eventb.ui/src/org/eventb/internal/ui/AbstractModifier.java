/*******************************************************************************
 * Copyright (c) 2006, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added history support
 *******************************************************************************/
package org.eventb.internal.ui;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.internal.ui.eventbeditor.editpage.IAttributeFactory;
import org.eventb.ui.IElementModifier;
import org.rodinp.core.IInternalElement;

public abstract class AbstractModifier implements IElementModifier {

	protected void modifyIfChanged(IAttributeFactory factory,
			IInternalElement element, String value) {
		UIUtils.setStringAttribute(element, factory, value,
				new NullProgressMonitor());
	}
}
