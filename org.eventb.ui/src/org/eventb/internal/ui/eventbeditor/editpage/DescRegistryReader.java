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

import java.util.List;

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

	public static void createAttributeComposites(ScrolledForm frm,
			IInternalElement element, Composite parent, IEventBEditor<?> editor,
			FormToolkit toolkit, List<IEditComposite> createdComps) {
		final ElementDescRegistry registry = ElementDescRegistry.getInstance();
		final IElementDesc desc = registry.getElementDesc(element);
		final IAttributeDesc[] attrDescs = desc.getAttributeDescription();
	
		for (IAttributeDesc attrDesc : attrDescs) {
			final IEditComposite editComposite = attrDesc.createWidget();
			editComposite.setForm(frm);
			editComposite.setElement(element);
			editComposite.createComposite(editor, toolkit, parent);
			createdComps.add(editComposite);
		}
	}

	
}
