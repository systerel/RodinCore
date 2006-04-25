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

import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eventb.core.ITheorem;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;


/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class TheoremMirrorPage
	extends EventBMirrorPage
	implements	ITheoremMirrorPage
{

	public TheoremMirrorPage(EventBEditor editor) {
		super(editor);
	}   
    
	/**
	 * Return the form (XML formatted) string that represents the information 
	 * of the constants.
	 */
	protected String getFormString() {
		String formString = "<form>";
		try {
			IRodinElement [] theorems = editor.getRodinInput().getChildrenOfType(ITheorem.ELEMENT_TYPE);
			for (int i = 0; i < theorems.length; i++) {
				formString = formString + "<li style=\"bullet\">" + UIUtils.makeHyperlink(theorems[i].getElementName()) + ": ";
				formString = formString + UIUtils.XMLWrapUp(((IInternalElement) theorems[i]).getContents()); 
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
				try {
					UIUtils.debug("Event: " + e.toString());
					UIUtils.debug("Data: " + e.data);
					UIUtils.debug("Here: " + e.getSource());
					IRodinElement [] theorems = editor.getRodinInput().getChildrenOfType(ITheorem.ELEMENT_TYPE);
					for (int i = 0; i < theorems.length; i++) {
						if (e.getHref().equals(theorems[i].getElementName())) {
							editor.setSelection(theorems[i]);
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