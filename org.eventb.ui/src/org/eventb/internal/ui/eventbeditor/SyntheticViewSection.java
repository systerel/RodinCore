/*******************************************************************************
 * Copyright (c) 2005 ETH-Zurich
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH RODIN Group
 *******************************************************************************/

package org.eventb.internal.ui.eventbeditor;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;

/**
 * @author htson
 * <p>
 * An implementation of Section Part for displaying and editting Sees clause.
 */
public class SyntheticViewSection
	extends SectionPart
	implements IElementChangedListener
{

	// The Form editor contains this section.
    private EventBEditor editor;
    private TreeViewer viewer;	
	
    /**
     * Constructor.
     * <p>
     * @param editor The Form editor contains this section
     * @param page The Dependencies page contains this section
     * @param parent The composite parent
     */
	public SyntheticViewSection(EventBEditor editor, FormToolkit toolkit, Composite parent) {
		super(parent, toolkit, Section.NO_TITLE);
		this.editor = editor;
		createClient(getSection(), toolkit);
		editor.addElementChangedListener(this);
	}


	/**
	 * Creat the content of the section.
	 */
	public void createClient(Section section, FormToolkit toolkit) {
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = 400;
		gd.minimumHeight = 300;
		gd.widthHint = 300;
		section.setLayoutData(gd);
		viewer = new SyntheticEditableTreeViewer(section, SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION, editor);
		viewer.setInput(((EventBEditor) editor).getRodinInput());
		section.setClient(viewer.getControl());		
	}	

	
	/* (non-Javadoc)
	 * @see org.rodinp.core.IElementChangedListener#elementChanged(org.rodinp.core.ElementChangedEvent)
	 */
	public void elementChanged(ElementChangedEvent event) {
		((EventBEditableTreeViewer) viewer).elementChanged(event);
	}
		
}