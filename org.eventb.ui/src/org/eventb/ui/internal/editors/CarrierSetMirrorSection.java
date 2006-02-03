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

package org.eventb.ui.internal.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IContext;
import org.eventb.ui.editors.EventBEditor;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 * <p>
 * An implementation of Event-B Mirror section to display the information of
 * carrier sets.
 */
public class CarrierSetMirrorSection
	extends EventBMirrorSection
{
	
	// Title and description of the section.
	private static final String title = "Carrier Sets";
    private static final String description = "List of carrier sets of the construct";
    

    /**
     * Contructor.
     * <p>
     * @param page The Form Page that this mirror section belong to
     * @param parent The Composite parent 
     * @param style The style for the section
     * @param rodinFile The Rodin File which the carrier sets belong to
     */
	public CarrierSetMirrorSection(FormPage page, Composite parent, int style, IRodinFile rodinFile) {
		super(page, parent, style, title, description, rodinFile);
	}

	
	/**
	 * Return the form (XML formatted) string that represents the information 
	 * of the carrier sets.
	 */
	protected String getFormString() {
		String formString = "<form>";
		try {
			ICarrierSet [] carrierSets = ((IContext) rodinFile).getCarrierSets();
			for (int i = 0; i < carrierSets.length; i++) {
				formString = formString + "<li style=\"bullet\">" + makeHyperlink(carrierSets[i].getElementName()) + ":</li>";
			}
		}
		catch (RodinDBException e) {
			// TODO Exception handle
			e.printStackTrace();
		}
		formString = formString + "</form>";
		return formString;
	
	}
	

	/**
	 * Return the hyperlink listener which enable the navigation on the form. 
	 */
	protected HyperlinkAdapter createHyperlinkListener() {
		return (new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
				EventBEditor editor = ((EventBEditor) getPage().getEditor());
				IRodinFile rodinFile = editor.getRodinInput();
				try {
					ICarrierSet [] carrierSets = ((IContext) rodinFile).getCarrierSets();
					for (int i = 0; i < carrierSets.length; i++) {
						if (e.getHref().equals(carrierSets[i].getElementName())) {
							editor.setSelection(carrierSets[i]);
						}
					}
				}
				catch (RodinDBException exception) {
					// TODO Exception handle
					exception.printStackTrace();
				}			
			}
		});
	}
	
	@Override
	protected void expansionStateChanging(boolean expanding) {
		if (expanding) {
			GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
			gd.heightHint = 100;
			gd.minimumHeight = 50;
			gd.widthHint = 200;
			this.getSection().setLayoutData(gd);
		}
		else {
			GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
			gd.heightHint = 0;
			gd.widthHint = 200;
			this.getSection().setLayoutData(gd);
		}
		super.expansionStateChanging(expanding);
	}

}
