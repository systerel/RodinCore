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

import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eventb.core.IInvariant;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This class is an implementation of a Invariant Mirror 'page'.
 */
public class InvariantMirrorPage extends EventBMirrorPage implements
		IInvariantMirrorPage {

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param editor
	 *            an Event-B Editor.
	 */
	public InvariantMirrorPage(EventBEditor editor) {
		super(editor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.EventBMirrorPage#getFormString()
	 */
	protected String getFormString() {
		String formString = "<form>";
		try {
			IRodinElement[] invariants = editor.getRodinInput()
					.getChildrenOfType(IInvariant.ELEMENT_TYPE);
			for (int i = 0; i < invariants.length; i++) {
				formString = formString
						+ "<li style=\"bullet\">"
						+ UIUtils.makeHyperlink(((IInvariant) invariants[i])
								.getLabel(null)) + ": ";
				formString = formString
						+ UIUtils.XMLWrapUp(((IInvariant) invariants[i])
								.getPredicateString());
				formString = formString + "</li>";
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
	protected HyperlinkAdapter createHyperlinkListener() {
		return (new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
				try {
					// UIUtils.debug("Event: " + e.toString());
					IRodinElement[] invariants = editor.getRodinInput()
							.getChildrenOfType(IInvariant.ELEMENT_TYPE);
					for (int i = 0; i < invariants.length; i++) {
						if (e.getHref().equals(
								((IInvariant) invariants[i]).getLabel(null))) {
							editor.edit(invariants[i]);
							return;
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