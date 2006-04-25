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

import java.lang.reflect.Constructor;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eventb.internal.ui.EventBUIPlugin;
import org.eventb.internal.ui.ExtensionLoader;

/**
 * @author htson
 * <p>
 * Event-B specific form editor for machines.
 */
public class EventBContextEditor
	extends EventBEditor 
{	
	
	/**
	 * The plug-in identifier of the Event-B Machine Editor (value
	 * <code>"org.eventb.internal.ui.editors.EventBMachineEditor"</code>).
	 */
	public static final String EDITOR_ID = EventBUIPlugin.PLUGIN_ID + ".editors.EventBContextEditor";
	
	private EventBMirrorPage theoremMirrorPage;
	private EventBMirrorPage axiomMirrorPage;
	private EventBMirrorPage carrierSetMirrorPage;
	private EventBMirrorPage constantMirrorPage;

	/**
	 * Default constructor.
	 */
	public EventBContextEditor() {
		super();
	}

	/**
	 * Creates the pages of the multi-page editor.
	 */
	protected void addPages() {
		Constructor [] constructors = new Constructor[0];
		
		constructors = ExtensionLoader.getContextPages();
				
		try {
			// Create the pages
			for (int i = 0; i < constructors.length; i++) {
				Object [] objects = {this};
				addPage((IFormPage) constructors[i].newInstance(objects));
			}
		}
		catch (PartInitException e) {
			// TODO Handle exception
			MessageDialog.openError(null,
					"Event-B Editor",
					"Error creating pages for Event-B Editor");
		}
		catch (Exception e) {
			// TODO Handle exception
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.ui.eventbeditor.EventBEditor#getAdapter(java.lang.Class)
	 */
	@Override
	public Object getAdapter(Class required) {
		if (ITheoremMirrorPage.class.equals(required)) {
			if (theoremMirrorPage == null) {
				theoremMirrorPage = new TheoremMirrorPage(this);
				return theoremMirrorPage;
			}
		}
		if (IAxiomMirrorPage.class.equals(required)) {
			if (axiomMirrorPage == null) {
				axiomMirrorPage = new AxiomMirrorPage(this);
				return axiomMirrorPage;
			}
		}
		if (ICarrierSetMirrorPage.class.equals(required)) {
			if (carrierSetMirrorPage == null) {
				carrierSetMirrorPage = new CarrierSetMirrorPage(this);
				return carrierSetMirrorPage;
			}
		}
		if (ICarrierSetMirrorPage.class.equals(required)) {
			if (constantMirrorPage == null) {
				constantMirrorPage = new CarrierSetMirrorPage(this);
				return constantMirrorPage;
			}
		}
		return super.getAdapter(required);
	}


	/* (non-Javadoc)
	 * @see org.eventb.internal.ui.eventbeditor.EventBEditor#dispose()
	 */
	@Override
	public void dispose() {
		if (theoremMirrorPage != null) theoremMirrorPage.dispose();
		if (axiomMirrorPage != null) axiomMirrorPage.dispose();
		if (carrierSetMirrorPage != null) carrierSetMirrorPage.dispose();
		if (constantMirrorPage != null) constantMirrorPage.dispose();
		super.dispose();
	}
}
