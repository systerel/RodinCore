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
 * @author htson
 * <p>
 * An abstract class of a section to display the information of
 * events.
 */
public class EventMirrorSection
	extends EventBMirrorSection
{

	// Title and description of the section.
	private static final String title = "Events";
    private static final String description = "List of events of the component";
    

    /**
     * Contructor.
     * <p>
     * @param page The Form Page that this mirror section belong to
     * @param parent The Composite parent 
     * @param style The style for the section
     * @param rodinFile The Rodin File which the constants belong to
     */
	public EventMirrorSection(FormPage page, Composite parent, int style, IRodinFile rodinFile) {
		super(page, parent, style, title, description, rodinFile);
	}
	

	/**
	 * Return the form (XML formatted) string that represents the information 
	 * of the constants.
	 */
	protected String getFormString() {
		String formString = "<form>";
		
		try {
			IEvent [] events = ((IMachine) rodinFile).getEvents();
			for (int i = 0; i < events.length; i++) {
				formString = formString + "<li style=\"bullet\">" + UIUtils.makeHyperlink(events[i].getElementName()) + ":</li>";
				IRodinElement [] lvars = events[i].getChildrenOfType(IVariable.ELEMENT_TYPE);
				IRodinElement [] guards = events[i].getChildrenOfType(IGuard.ELEMENT_TYPE);
				IRodinElement [] actions = events[i].getChildrenOfType(IAction.ELEMENT_TYPE);
				
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
				EventBEditor editor = ((EventBEditor) getPage().getEditor());
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
	
	
	@Override
	protected void expansionStateChanging(boolean expanding) {
		if (expanding) {
			GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
			gd.heightHint = 200;
			gd.minimumHeight = 150;
			gd.widthHint = 150;
			this.getSection().setLayoutData(gd);
		}
		else {
			GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
			gd.heightHint = 0;
			gd.widthHint = 150;
			this.getSection().setLayoutData(gd);
		}
		super.expansionStateChanging(expanding);
	}
	
}
