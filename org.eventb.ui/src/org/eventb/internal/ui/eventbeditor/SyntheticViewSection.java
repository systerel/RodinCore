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

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eventb.core.IAction;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachine;
import org.eventb.core.ISees;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 * <p>
 * An implementation of Section Part for displaying and editting Sees clause.
 */
public class SyntheticViewSection
	extends SectionPart
	implements IElementChangedListener
{

	// Title and description of the section.
	private static final String SECTION_TITLE = "Synthetic View";
	private static final String SECTION_DESCRIPTION = "The synthetic view of the component";	
	
	// The Form editor contains this section.
    private FormEditor editor;
    private FormToolkit toolkit;
    private ScrolledForm scrolledForm;
    private Collection<Control> controls;
    private boolean upToDate = false;
    
    /**
     * Constructor.
     * <p>
     * @param editor The Form editor contains this section
     * @param page The Dependencies page contains this section
     * @param parent The composite parent
     */
	public SyntheticViewSection(FormEditor editor, FormToolkit toolkit, Composite parent) {
		super(parent, toolkit, ExpandableComposite.TITLE_BAR);
		this.editor = editor;
		controls = new HashSet<Control>();
		createClient(getSection(), toolkit);
		((EventBEditor) editor).addElementChangedListener(this);
	}

	
	private void createEmptyComposite(FormToolkit toolkit, Composite parent, int span) {
		Composite separator = toolkit.createComposite(parent);
		GridData gd = new GridData(SWT.NONE, SWT.NONE, false, false);
		gd.horizontalSpan = span;
		gd.heightHint = 20;
		separator.setLayoutData(gd);
		controls.add(separator);
	}
	/**
	 * Creat the content of the section.
	 */
	public void createClient(Section section, FormToolkit toolkit) {
        section.setText(SECTION_TITLE);
        section.setDescription(SECTION_DESCRIPTION);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = 400;
		gd.minimumHeight = 300;
		gd.widthHint = 300;
		section.setLayoutData(gd);
		this.toolkit = toolkit;
		scrolledForm = toolkit.createScrolledForm(section);
		scrolledForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        GridLayout layout = new GridLayout();
        layout.numColumns = 6;
        layout.marginHeight = 2;
        layout.makeColumnsEqualWidth = true;
        Composite body = scrolledForm.getBody();
		body.setLayout(layout);

		refreshForm();
		section.setClient(scrolledForm);
	}	

	private void refreshForm() {
		for (Control control : controls) {
			control.dispose();
		}
		controls = new HashSet<Control>();
		
	 	Composite body = scrolledForm.getBody();
	 	
	 	GridData textGD;
		GridData labelGD;
		
		IRodinFile rodinFile = ((EventBEditor) editor).getRodinInput();
		if (rodinFile instanceof IMachine) {
			Label label = toolkit.createLabel(body, "MACHINE");
			labelGD = new GridData(SWT.FILL, SWT.NONE, true, false);
			label.setLayoutData(labelGD);
			controls.add(label);
			label = toolkit.createLabel(body, rodinFile.getElementName());
			labelGD = new GridData(SWT.FILL, SWT.NONE, true, false);
			label.setLayoutData(labelGD);
			controls.add(label);
			createEmptyComposite(toolkit, body, 4);
		}
		
		try {
			IRodinElement [] contexts = rodinFile.getChildrenOfType(ISees.ELEMENT_TYPE);
			if (contexts.length != 0) {
				Label label = toolkit.createLabel(body, "SEES");
				labelGD = new GridData(SWT.FILL, SWT.NONE, true, false);
				label.setLayoutData(labelGD);
				controls.add(label);
				Text text = toolkit.createText(body, contexts[0].getElementName());
				textGD = new GridData(SWT.FILL, SWT.NONE, true, false);
				text.setLayoutData(textGD);
				new ElementContentText(text, contexts[0], this);
				controls.add(text);
				createEmptyComposite(toolkit, body, 4);
			}

			IRodinElement [] variables = rodinFile.getChildrenOfType(IVariable.ELEMENT_TYPE);
			if (variables.length != 0) {
				Label label = toolkit.createLabel(body, "VARIABLES");
				labelGD = new GridData(SWT.FILL, SWT.NONE, true, false);
				label.setLayoutData(labelGD);
				controls.add(label);
				createEmptyComposite(toolkit, body, 5);
				
				for (IRodinElement var : variables) {
					createEmptyComposite(toolkit, body, 1);
					final Text text = toolkit.createText(body, var.getElementName());
					textGD = new GridData(SWT.FILL, SWT.NONE, true, false);
					text.setLayoutData(textGD);
					controls.add(text);
					new ElementNameText(text, var, this);					
					createEmptyComposite(toolkit, body, 4);
				}
			}
			
			IRodinElement [] invariants = rodinFile.getChildrenOfType(IInvariant.ELEMENT_TYPE);
			if (invariants.length != 0) {
				Label label = toolkit.createLabel(body, "INVARIANTS");
				labelGD = new GridData(SWT.FILL, SWT.NONE, true, false);
				label.setLayoutData(labelGD);
				controls.add(label);
				createEmptyComposite(toolkit, body, 5);
				
				
				for (IRodinElement inv : invariants) {
					createEmptyComposite(toolkit, body, 1);
					
					Text textName = toolkit.createText(body, inv.getElementName());
					textGD = new GridData(SWT.FILL, SWT.NONE, true, false);
					textName.setLayoutData(textGD);
					controls.add(textName);
					Text textContent = toolkit.createText(body, ((IInternalElement) inv).getContents());
					textGD = new GridData(SWT.FILL, SWT.NONE, true, false);
					textGD.horizontalSpan = 4;
					controls.add(textContent);
					textContent.setLayoutData(textGD);
					new ElementNameContentTexts(textName, textContent, inv, this);
				}
			}		
			
			IRodinElement [] theorems = rodinFile.getChildrenOfType(ITheorem.ELEMENT_TYPE);
			if (theorems.length != 0) {
				Label label = toolkit.createLabel(body, "THEOREMS");
				labelGD = new GridData(SWT.FILL, SWT.NONE, true, false);
				label.setLayoutData(labelGD);
				controls.add(label);
				createEmptyComposite(toolkit, body, 5);
				
					
				for (IRodinElement thm : theorems) {
					createEmptyComposite(toolkit, body, 1);
					
					Text textName = toolkit.createText(body, thm.getElementName());
					textGD = new GridData(SWT.FILL, SWT.NONE, true, false);
					textName.setLayoutData(textGD);
					controls.add(textName);
					Text textContent = toolkit.createText(body, ((IInternalElement) thm).getContents());
					textGD = new GridData(SWT.FILL, SWT.NONE, true, false);
					textGD.horizontalSpan = 4;
					textContent.setLayoutData(textGD);
					controls.add(textContent);
					new ElementNameContentTexts(textName, textContent, thm, this);
				}
			}
			
			IRodinElement [] events = rodinFile.getChildrenOfType(IEvent.ELEMENT_TYPE);
			if (events.length != 0) {
				Label label = toolkit.createLabel(body, "EVENTS");
				labelGD = new GridData(SWT.FILL, SWT.NONE, true, false);
				label.setLayoutData(labelGD);
				controls.add(label);
				createEmptyComposite(toolkit, body, 5);
				
				for (IRodinElement event : events) {
					createEmptyComposite(toolkit, body, 1);
					Text text = toolkit.createText(body, event.getElementName());
					textGD = new GridData(SWT.FILL, SWT.NONE, true, false);
					text.setLayoutData(textGD);
					controls.add(text);
					new ElementNameText(text, event, this);
					createEmptyComposite(toolkit, body, 4);
					
					IRodinElement [] lvariables = ((IParent) event).getChildrenOfType(IVariable.ELEMENT_TYPE);
					IRodinElement [] guards = ((IParent) event).getChildrenOfType(IGuard.ELEMENT_TYPE);
					IRodinElement [] actions = ((IParent) event).getChildrenOfType(IAction.ELEMENT_TYPE);
					if (lvariables.length != 0) {
						createEmptyComposite(toolkit, body, 2);
						label = toolkit.createLabel(body, "ANY");
						labelGD = new GridData(SWT.FILL, SWT.NONE, true, false);
						label.setLayoutData(labelGD);
						controls.add(label);
						createEmptyComposite(toolkit, body, 3);

						for (IRodinElement var : variables) {
							createEmptyComposite(toolkit, body, 3);
							text = toolkit.createText(body, var.getElementName());
							textGD = new GridData(SWT.FILL, SWT.NONE, true, false);
							text.setLayoutData(textGD);
							controls.add(text);
							new ElementNameText(text, var, this);
							createEmptyComposite(toolkit, body, 2);
						}
						createEmptyComposite(toolkit, body, 2);
						label = toolkit.createLabel(body, "WHERE");
						labelGD = new GridData(SWT.FILL, SWT.NONE, true, false);
						label.setLayoutData(labelGD);
						controls.add(label);
						createEmptyComposite(toolkit, body, 3);
						for (IRodinElement grd : guards) {
							createEmptyComposite(toolkit, body, 2);
							Text textName = toolkit.createText(body, grd.getElementName());
							textGD = new GridData(SWT.FILL, SWT.NONE, true, false);
							textName.setLayoutData(textGD);
							controls.add(textName);
							Text textContent = toolkit.createText(body, ((IInternalElement) grd).getContents());
							textGD = new GridData(SWT.FILL, SWT.NONE, true, false);
							textGD.horizontalSpan = 3;
							textContent.setLayoutData(textGD);
							controls.add(textContent);
							new ElementNameContentTexts(textName, textContent, grd, this);
						}
						createEmptyComposite(toolkit, body, 2);
						label = toolkit.createLabel(body, "THEN");
						labelGD = new GridData(SWT.FILL, SWT.NONE, true, false);
						label.setLayoutData(labelGD);
						controls.add(label);
						createEmptyComposite(toolkit, body, 3);
					}
					else {
						if (guards.length != 0) {
							createEmptyComposite(toolkit, body, 2);
							label = toolkit.createLabel(body, "SELECT");
							labelGD = new GridData(SWT.FILL, SWT.NONE, true, false);
							label.setLayoutData(labelGD);
							controls.add(label);
							createEmptyComposite(toolkit, body, 3);
							for (IRodinElement grd : guards) {
								createEmptyComposite(toolkit, body, 2);
								Text textName = toolkit.createText(body, grd.getElementName());
								textGD = new GridData(SWT.FILL, SWT.NONE, true, false);
								textName.setLayoutData(textGD);
								controls.add(textName);
								Text textContent = toolkit.createText(body, ((IInternalElement) grd).getContents());
								textGD = new GridData(SWT.FILL, SWT.NONE, true, false);
								textGD.horizontalSpan = 3;
								textContent.setLayoutData(textGD);
								controls.add(textContent);
								new ElementNameContentTexts(textName, textContent, grd, this);
							}
							createEmptyComposite(toolkit, body, 2);
							label = toolkit.createLabel(body, "THEN");
							labelGD = new GridData(SWT.FILL, SWT.NONE, true, false);
							label.setLayoutData(labelGD);
							controls.add(label);
							createEmptyComposite(toolkit, body, 3);
						}
						else {
							createEmptyComposite(toolkit, body, 2);
							label = toolkit.createLabel(body, "BEGIN");
							labelGD = new GridData(SWT.FILL, SWT.NONE, true, false);
							label.setLayoutData(labelGD);
							controls.add(label);
							createEmptyComposite(toolkit, body, 3);
						}
					}
					
					for (IRodinElement action : actions) {
						createEmptyComposite(toolkit, body, 3);
						text = toolkit.createText(body, ((IInternalElement) action).getContents());
						textGD = new GridData(SWT.FILL, SWT.NONE, true, false);
						textGD.horizontalSpan = 3;
						text.setLayoutData(textGD);
						controls.add(text);
						new ElementContentText(text, action, this);
					}
					createEmptyComposite(toolkit, body, 2);
					label = toolkit.createLabel(body, "END");
					labelGD = new GridData(SWT.FILL, SWT.NONE, true, false);
					label.setLayoutData(labelGD);
					controls.add(label);
					createEmptyComposite(toolkit, body, 3);
				}
				label = toolkit.createLabel(body, "END");
				labelGD = new GridData(SWT.FILL, SWT.NONE, true, false);
				label.setLayoutData(labelGD);
				controls.add(label);
				createEmptyComposite(toolkit, body, 5);
			}
		}
		catch (RodinDBException e) {
			e.printStackTrace();
		}
		
		toolkit.paintBordersFor(body);
		scrolledForm.reflow(true); // Important for refresh
	}
	

	/* (non-Javadoc)
	 * @see org.rodinp.core.IElementChangedListener#elementChanged(org.rodinp.core.ElementChangedEvent)
	 */
	public void elementChanged(ElementChangedEvent event) {
		if (upToDate) upToDate = false;
		else refreshForm();
	}
	
	@Override
	protected void expansionStateChanging(boolean expanding) {
		if (expanding) {
			GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
			gd.heightHint = 400;
			gd.minimumHeight = 300;
			gd.widthHint = 300;
			this.getSection().setLayoutData(gd);
		}
		else {
			GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
			gd.heightHint = 0;
			gd.widthHint = 300;
			this.getSection().setLayoutData(gd);
		}
		super.expansionStateChanging(expanding);
	}
		
	public void setUpToDate() {upToDate = true;}
	
}