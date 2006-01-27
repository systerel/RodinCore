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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eventb.ui.editors.IEventBInputText;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 * <p>
 * An implementation of Event-B Input row for editing name of Rodin elements.
 */
public class NameInputRow 
	extends EventBInputRow 
{

	/**
	 * Contructor.
	 * <p>
	 * @param page The Detail page that holds this row
	 * @param toolkit The Form Toolkit used to create this
	 * @param parent The parent Composite
	 * @param label The label for the content (e.g. predicate, substitution)
	 * @param tip The tip for this row
	 */
	public NameInputRow(EventBDetailsSection page, FormToolkit toolkit, Composite parent, String label, String tip) {
		super(page, toolkit, parent, label, tip, SWT.SINGLE |SWT.BORDER);
	}
	

	/**
	 * Set the text for the text field part of the row.
	 */
	protected void setText() {
		textInput.setText(page.getInput().getElementName());
	}


	/**
	 * Commit the change and notify the listeners.
	 */
	public void commit() {
		if (dirty) {
			String name = textInput.getText();
			try {
				IInternalElement input = page.getInput();
				if (input.exists()) {
					System.out.println("Commit name: " + input + " to be " + name);
					if (!(input.getElementName().equals(name))) {
						SectionPart masterPart = page.getBlock().getMasterPart();
						boolean expand = false;
						if (masterPart instanceof EventBTreePartWithButtons) {
							TreeViewer viewer = ((EventBTreePartWithButtons) masterPart).getViewer();
							expand = viewer.getExpandedState(input);
						}
						
						IRodinElement parent = input.getParent();
						input.rename(name, false, null);
						
						IRodinElement [] children = ((IParent) parent).getChildrenOfType(input.getElementType());
						int i;
						for (i = 0; i < children.length; i++) {
							if (children[i].getElementName().equals(name)) {
								page.setInput((IInternalElement) children[i]);
								break;
							}
						}
						if (i == children.length) {
							System.out.println("WARNING: CANNOT FIND THE NEW ELEMENT");
						}
						//	System.out.println("After " + input.getContents());
						if (masterPart instanceof EventBTablePartWithButtons) {
							System.out.println("Master refresh");
							((EventBTablePartWithButtons) masterPart).commit();
							//((EventBTablePartWithButtons) masterPart).getViewer().setSelection(new StructuredSelection(input));
						}
						else if (masterPart instanceof EventBTreePartWithButtons) {
							System.out.println("Master refresh");
							TreeViewer viewer = ((EventBTreePartWithButtons) masterPart).getViewer();
							Control control = viewer.getControl();
							control.setRedraw(false);
							((EventBTreePartWithButtons) masterPart).commit();
							//viewer.setSelection(new StructuredSelection(input));
							viewer.setExpandedState(input, expand);
							control.setRedraw(true);
						}
					}
				}
			}
			catch (RodinDBException e) {
				MessageDialog.openError(null, "Rename Error", "Name collision for " + name);
				e.printStackTrace();
			}
			
			dirty = false;
		}
	}
	

	/**
	 * Creating the input text field part. 
	 */
	protected IEventBInputText createEventBInputText(Composite parent, FormToolkit toolkit, int style) {
		return new EventBText(parent, toolkit, style);
	}
}
