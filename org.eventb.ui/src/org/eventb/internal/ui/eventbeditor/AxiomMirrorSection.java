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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eventb.core.IContext;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 * <p>
 * An implementation of Event-B Mirror section to display the information of
 * axioms.
 */
public class AxiomMirrorSection
	extends EventBMirrorSection
{
    
	// Title and description of the section.
	private static final String title = "Axioms";
    private static final String description = "List of axioms of the construct";
    
    
    /**
     * Contructor.
     * <p>
     * @param page The Form Page that this mirror section belong to
     * @param parent The Composite parent 
     * @param style The style for the section
     * @param rodinFile The Rodin File which the axioms belong to
     */
	public AxiomMirrorSection(FormPage page, Composite parent, int style, IRodinFile rodinFile) {
		super(page, parent, style, title, description, rodinFile);
	}

	
	/**
	 * Return the form (XML formatted) string that represents the information 
	 * of the axioms.
	 */
	protected String getFormString() {
		IRodinElement [] axioms;
		String formString = "<form>";
		try {
			axioms = ((IContext) rodinFile).getAxioms();
			for (int i = 0; i < axioms.length; i++) {
				formString = formString + "<li style=\"bullet\">" + makeHyperlink(axioms[i].getElementName()) + ": ";
				formString = formString + "</li>";
				formString = formString + "<li style=\"text\" value=\"\">";
				formString = formString + ((IInternalElement) axioms[i]).getContents(); 
				formString = formString + "</li>";
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
					IRodinElement [] axioms = ((IContext) rodinFile).getAxioms();
					for (int i = 0; i < axioms.length; i++) {
						if (e.getHref().equals(axioms[i].getElementName())) {
							editor.setSelection(axioms[i]);
							break;
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
			gd.heightHint = 200;
			gd.minimumHeight = 150;
			gd.widthHint = 200;
			this.getSection().setLayoutData(gd);
		}
		else {
			GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
			gd.heightHint = 0;
			gd.widthHint = 200;
			this.getSection().setLayoutData(gd);
		}
		super.expansionStateChanging(expanding);
	}
	

}
