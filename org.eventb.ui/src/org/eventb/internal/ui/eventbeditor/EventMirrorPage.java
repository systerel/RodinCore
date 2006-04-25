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
import org.eventb.core.IAction;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IMachine;
import org.eventb.core.IVariable;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
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

public class EventMirrorPage
	extends EventBMirrorPage
	implements	IEventMirrorPage
{

	public EventMirrorPage(EventBEditor editor) {
		super(editor);
	}   
    
	/**
	 * Return the form (XML formatted) string that represents the information 
	 * of the constants.
	 */
	protected String getFormString() {
		String formString = "<form>";
		
		try {
			IRodinElement [] events = editor.getRodinInput().getChildrenOfType(IEvent.ELEMENT_TYPE);
			for (int i = 0; i < events.length; i++) {
				formString = formString + "<li style=\"bullet\">" + UIUtils.makeHyperlink(events[i].getElementName()) + ":</li>";
				IRodinElement [] lvars = ((IInternalElement) events[i]).getChildrenOfType(IVariable.ELEMENT_TYPE);
				IRodinElement [] guards = ((IInternalElement) events[i]).getChildrenOfType(IGuard.ELEMENT_TYPE);
				IRodinElement [] actions = ((IInternalElement) events[i]).getChildrenOfType(IAction.ELEMENT_TYPE);
				
				if (lvars.length != 0) {
					formString = formString + "<li style=\"text\" value=\"\" bindent = \"20\">";
					formString = formString + "<b>ANY</b> ";
					for (int j = 0; j < lvars.length; j++) {
						if (j == 0)	{
							formString = formString + UIUtils.makeHyperlink(lvars[j].getElementName());
						}
						else formString = formString + ", " + UIUtils.makeHyperlink(lvars[j].getElementName());
					}			
					formString = formString + " <b>WHERE</b>";
					formString = formString + "</li>";
				}
				else {
					if (guards.length !=0) {
						formString = formString + "<li style=\"text\" value=\"\" bindent = \"20\">";
						formString = formString + "<b>WHEN</b></li>";
					}
					else {
						formString = formString + "<li style=\"text\" value=\"\" bindent = \"20\">";
						formString = formString + "<b>BEGIN</b></li>";
					}
				
				}

				for (int j = 0; j < guards.length; j++) {
					formString = formString + "<li style=\"text\" value=\"\" bindent=\"40\">";
					formString = formString + UIUtils.makeHyperlink(guards[j].getElementName()) + ": "
						+ UIUtils.XMLWrapUp(((IInternalElement) guards[j]).getContents());
					formString = formString + "</li>";
				}
				
				if (guards.length != 0) {
					formString = formString + "<li style=\"text\" value=\"\" bindent=\"20\">";
					formString = formString + "<b>THEN</b></li>";
				}
			
				for (int j = 0; j < actions.length; j++) {
					formString = formString + "<li style=\"text\" value=\"\" bindent=\"40\">";
					formString = formString + UIUtils.makeHyperlink(((IInternalElement) actions[j]).getContents());
					formString = formString + "</li>";
				}
				formString = formString + "<li style=\"text\" value=\"\" bindent=\"20\">";
				formString = formString + "<b>END</b></li>";
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
				IRodinFile rodinFile = editor.getRodinInput();
				try {
					IEvent [] events = ((IMachine) rodinFile).getEvents();
					for (int i = 0; i < events.length; i++) {
						if (e.getHref().equals(events[i].getElementName())) {
							editor.setSelection(events[i]);
						}
						IRodinElement [] lvars = events[i].getChildrenOfType(IVariable.ELEMENT_TYPE);
						IRodinElement [] guards = events[i].getChildrenOfType(IGuard.ELEMENT_TYPE);
						IRodinElement [] actions = events[i].getChildrenOfType(IAction.ELEMENT_TYPE);
						for (int j = 0; j < lvars.length; j++) {
							if (e.getHref().equals(lvars[j].getElementName())) {
								editor.setSelection(lvars[j]);
							}
						}
						for (int j = 0; j < guards.length; j++) {
							if (e.getHref().equals(guards[j].getElementName())) {
								editor.setSelection(guards[j]);
							}
						}
						for (int j = 0; j < actions.length; j++) {
							if (e.getHref().equals(((IInternalElement) actions[j]).getContents())) {
								editor.setSelection(actions[j]);
							}
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