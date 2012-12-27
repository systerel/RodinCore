/*******************************************************************************
 * Copyright (c) 2009, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.rodinp.core.IRodinFile;

/**
 * Abstract Event-B form editor for machines, contexts, proofs.
 */
public abstract class EventBFormEditor extends FormEditor {

	/**
	 * Constructor.
	 */
	public EventBFormEditor() {
		super();
	}
	
	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
	}
	
	/**
	 * Get the Rodin file associated with this editor.
	 * <p>
	 * 
	 * @return a handle to a Rodin file
	 */
	public abstract IRodinFile getRodinInputFile();

}
