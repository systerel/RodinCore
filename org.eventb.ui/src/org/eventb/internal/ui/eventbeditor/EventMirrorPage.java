/*******************************************************************************
 * Copyright (c) 2005-2006 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.eventbeditor;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eventb.core.IAction;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IMachineFile;
import org.eventb.core.IVariable;
import org.eventb.internal.ui.UIUtils;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This class is an implementation of a Event Mirror 'page'.
 */
public class EventMirrorPage extends EventBMirrorPage implements
		IEventMirrorPage {

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param editor
	 *            an Event-B Editor
	 */
	public EventMirrorPage(IEventBEditor editor) {
		super(editor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.EventBMirrorPage#getFormString()
	 */
	@Override
	protected String getFormString() {
		String formString = "<form>";

		try {
			IRodinElement[] events = editor.getRodinInput().getChildrenOfType(
					IEvent.ELEMENT_TYPE);
			for (int i = 0; i < events.length; i++) {
				formString = formString + "<li style=\"bullet\">"
						+ UIUtils.makeHyperlink(events[i].getElementName())
						+ ":</li>";
				IRodinElement[] lvars = ((IInternalElement) events[i])
						.getChildrenOfType(IVariable.ELEMENT_TYPE);
				IRodinElement[] guards = ((IInternalElement) events[i])
						.getChildrenOfType(IGuard.ELEMENT_TYPE);
				IRodinElement[] actions = ((IInternalElement) events[i])
						.getChildrenOfType(IAction.ELEMENT_TYPE);

				if (lvars.length != 0) {
					formString = formString
							+ "<li style=\"text\" value=\"\" bindent = \"20\">";
					formString = formString + "<b>ANY</b> ";
					for (int j = 0; j < lvars.length; j++) {
						if (j == 0) {
							formString = formString
									+ UIUtils.makeHyperlink(lvars[j]
											.getElementName());
						} else
							formString = formString
									+ ", "
									+ UIUtils.makeHyperlink(lvars[j]
											.getElementName());
					}
					formString = formString + " <b>WHERE</b>";
					formString = formString + "</li>";
				} else {
					if (guards.length != 0) {
						formString = formString
								+ "<li style=\"text\" value=\"\" bindent = \"20\">";
						formString = formString + "<b>WHEN</b></li>";
					} else {
						formString = formString
								+ "<li style=\"text\" value=\"\" bindent = \"20\">";
						formString = formString + "<b>BEGIN</b></li>";
					}

				}

				for (int j = 0; j < guards.length; j++) {
					formString = formString
							+ "<li style=\"text\" value=\"\" bindent=\"40\">";
					formString = formString
							+ UIUtils.makeHyperlink(guards[j].getElementName())
							+ ": "
							+ UIUtils.XMLWrapUp(((IGuard) guards[j])
									.getPredicateString(null));
					formString = formString + "</li>";
				}

				if (guards.length != 0) {
					formString = formString
							+ "<li style=\"text\" value=\"\" bindent=\"20\">";
					formString = formString + "<b>THEN</b></li>";
				}

				for (int j = 0; j < actions.length; j++) {
					formString = formString
							+ "<li style=\"text\" value=\"\" bindent=\"40\">";
					formString = formString
							+ UIUtils.makeHyperlink(((IAction) actions[j])
									.getAssignmentString(null));
					formString = formString + "</li>";
				}
				formString = formString
						+ "<li style=\"text\" value=\"\" bindent=\"20\">";
				formString = formString + "<b>END</b></li>";
			}
		} catch (RodinDBException e) {
			// TODO Exception handle
			e.printStackTrace();
		}
		formString = formString + "</form>";

		return formString;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.EventBMirrorPage#createHyperlinkListener()
	 */
	@Override
	protected HyperlinkAdapter createHyperlinkListener() {
		// TODO Using name is not enough since local guards can have same name
		// in different event.
		return (new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				IRodinFile rodinFile = editor.getRodinInput();
				try {
					IEvent[] events = ((IMachineFile) rodinFile).getEvents(null);
					for (int i = 0; i < events.length; i++) {
						if (e.getHref().equals(events[i].getLabel(null))) {
							editor.edit(events[i]);
						}
						IRodinElement[] lvars = events[i]
								.getChildrenOfType(IVariable.ELEMENT_TYPE);
						IRodinElement[] guards = events[i]
								.getChildrenOfType(IGuard.ELEMENT_TYPE);
						IRodinElement[] actions = events[i]
								.getChildrenOfType(IAction.ELEMENT_TYPE);
						for (int j = 0; j < lvars.length; j++) {
							if (e.getHref().equals(
									((IVariable) lvars[j])
											.getIdentifierString(new NullProgressMonitor()))) {
								editor.edit(lvars[j]);
							}
						}
						for (int j = 0; j < guards.length; j++) {
							if (e.getHref().equals(((IGuard) guards[j]).getLabel(null))) {
								editor.edit(guards[j]);
							}
						}
						for (int j = 0; j < actions.length; j++) {
							if (e.getHref().equals(
									((IAction) actions[j])
											.getAssignmentString(null))) {
								editor.edit(actions[j]);
							}
						}
					}
				} catch (RodinDBException exception) {
					// TODO Exception handle
					exception.printStackTrace();
				}
			}
		});
	}

}