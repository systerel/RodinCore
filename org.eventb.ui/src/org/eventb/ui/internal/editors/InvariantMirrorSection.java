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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eventb.core.IMachine;
import org.eventb.ui.editors.EventBEditor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 * <p>
 * An abstract class of a section to display the information of
 * invariants.
 */
public class InvariantMirrorSection
	extends EventBMirrorSection
{

	// Title and description of the section.
	private static final String title = "Invariants";
    private static final String description = "List of invariants of the construct";
    

    /**
     * Contructor.
     * <p>
     * @param page The Form Page that this mirror section belong to
     * @param parent The Composite parent 
     * @param style The style for the section
     * @param rodinFile The Rodin File which the constants belong to
     */
	public InvariantMirrorSection(FormPage page, Composite parent, int style, IRodinFile rodinFile) {
		super(page, parent, style, title, description, rodinFile);
	}


	/**
	 * Return the form (XML formatted) string that represents the information 
	 * of the constants.
	 */
	protected String getFormString() {
		String formString = "<form>";
		try {
			IRodinElement [] invariants = ((IMachine) rodinFile).getInvariants();
			for (int i = 0; i < invariants.length; i++) {
				formString = formString + "<li style=\"bullet\">" + makeHyperlink(invariants[i].getElementName()) + ": ";
				formString = formString + "</li>";
				formString = formString + "<li style=\"text\" value=\"\">";
				formString = formString + ((IInternalElement) invariants[i]).getContents(); 
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
				try {
					IRodinElement [] invariants = ((IMachine) rodinFile).getInvariants();
					for (int i = 0; i < invariants.length; i++) {
						if (e.getHref().equals(invariants[i].getElementName())) {
							editor.setSelection(invariants[i]);
							return;
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
	
}
