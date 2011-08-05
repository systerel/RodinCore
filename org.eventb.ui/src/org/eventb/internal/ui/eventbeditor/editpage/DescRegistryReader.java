/*******************************************************************************
 * Copyright (c) 2008, 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eventb.internal.ui.eventbeditor.elementdesc.ElementDescRegistry;
import org.eventb.internal.ui.eventbeditor.elementdesc.IAttributeDesc;
import org.eventb.internal.ui.eventbeditor.elementdesc.IElementDesc;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IInternalElement;

/**
 * @author Nicolas Beauger
 *
 */
public class DescRegistryReader {

	public static IEditComposite[] createAttributeComposites(ScrolledForm form,
			IInternalElement element, Composite parent,
			IEventBEditor<?> editor, FormToolkit toolkit) {
		final ElementDescRegistry registry = ElementDescRegistry.getInstance();
		final IElementDesc desc = registry.getElementDesc(element);
		final IAttributeDesc[] attrDescs = desc.getAttributeDescription();
	
		final int length = attrDescs.length;
		final IEditComposite[] result = new IEditComposite[length];
		for (int i = 0; i < length; ++i) {
			final IAttributeDesc attrDesc = attrDescs[i];
			final IEditComposite editComposite = attrDesc.createWidget();
			editComposite.setForm(form);
			editComposite.setElement(element);
			editComposite.createComposite(editor, toolkit, parent);
			result[i] = editComposite;
		}
		return result;
	}

	
}
