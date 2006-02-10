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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Section;
import org.eventb.core.IContext;
import org.eventb.core.IMachine;
import org.rodinp.core.IRodinFile;

/**
 * @author htson
 * <p>
 * An implementation of the Event-B Form Page
 * for editing Theorems (Rodin elements).
 */
public class TheoremsPage
	extends EventBFormPage 
{
	
	// Title, tab title and ID of the page.
	public static final String PAGE_ID = "Theorems"; //$NON-NLS-1$
	public static final String PAGE_TITLE = "Theorems";
	public static final String PAGE_TAB_TITLE = "Theorems";
	

	/**
	 * Contructor.
	 * <p>
	 * @param editor The form editor that holds the page 
	 */
	public TheoremsPage(FormEditor editor) {
		super(editor, PAGE_ID, PAGE_TITLE, PAGE_TAB_TITLE);  //$NON-NLS-1$
	}
	

	/**
	 * Create the Master detail block.
	 */
	protected EventBMasterDetailsBlock createMasterDetailsBlock() {
		return new TheoremMasterDetailsBlock(this);
	}
	

	/**
	 * Creating the Mirror sections within the page.
	 */
	protected void createMirrorSections(Composite body, IManagedForm managedForm) {
		IRodinFile rodinFile = ((EventBEditor) this.getEditor()).getRodinInput();
	
		if (rodinFile instanceof IContext) {
			Composite composite = managedForm.getToolkit().createComposite(body);
			GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
			composite.setLayoutData(gd);
			GridLayout layout = new GridLayout();
			layout.numColumns = 2;
			layout.makeColumnsEqualWidth = true;
			composite.setLayout(layout);

			// Axiom mirror section.
			AxiomMirrorSection axiomMirrorSection = new AxiomMirrorSection(this, composite, ExpandableComposite.TITLE_BAR | Section.EXPANDED, rodinFile);
			managedForm.addPart(axiomMirrorSection);
			gd = new GridData(SWT.FILL, SWT.FILL, true, true);
			gd.heightHint = 200;
			gd.minimumHeight = 150;
			gd.widthHint = 200;
			axiomMirrorSection.getSection().setLayoutData(gd);
			EventBFormPage page = (EventBFormPage) this.getEditor().findPage(AxiomsPage.PAGE_ID);
			page.addChangedListener(axiomMirrorSection);
			
			// Theorem mirror section.
			TheoremMirrorSection theoremMirrorSection = new TheoremMirrorSection(this, composite, ExpandableComposite.TITLE_BAR | Section.EXPANDED, rodinFile);
			managedForm.addPart(theoremMirrorSection);
			gd = new GridData(SWT.FILL, SWT.FILL, true, true);
			gd.heightHint = 200;
			gd.minimumHeight = 150;
			gd.widthHint = 200;
			theoremMirrorSection.getSection().setLayoutData(gd);
			page = (EventBFormPage) this.getEditor().findPage(TheoremsPage.PAGE_ID);
			page.addChangedListener(theoremMirrorSection);

			// Carrier set mirror section.
			CarrierSetMirrorSection carrierSetMirrorSection = new CarrierSetMirrorSection(this, composite, ExpandableComposite.TITLE_BAR | Section.EXPANDED, rodinFile);
			managedForm.addPart(carrierSetMirrorSection);
			gd = new GridData(SWT.FILL, SWT.FILL, true, true);
			gd.heightHint = 100;
			gd.minimumHeight = 50;
			gd.widthHint = 200;
			carrierSetMirrorSection.getSection().setLayoutData(gd);
			page = (EventBFormPage) this.getEditor().findPage(CarrierSetsPage.PAGE_ID);
			page.addChangedListener(carrierSetMirrorSection);
			
			// Constant set mirror section.
			ConstantMirrorSection constantMirrorSection = new ConstantMirrorSection(this, composite, ExpandableComposite.TITLE_BAR | Section.EXPANDED, rodinFile);
			managedForm.addPart(constantMirrorSection);
			gd = new GridData(SWT.FILL, SWT.FILL, true, true);
			gd.heightHint = 100;
			gd.minimumHeight = 50;
			gd.widthHint = 200;
			constantMirrorSection.getSection().setLayoutData(gd);
			page = (EventBFormPage) this.getEditor().findPage(ConstantsPage.PAGE_ID);
			page.addChangedListener(constantMirrorSection);
			
		}
		else if (rodinFile instanceof IMachine) {
			GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
			Composite composite = managedForm.getToolkit().createComposite(body);
			composite.setLayoutData(gd);
			GridLayout layout = new GridLayout();
			layout.numColumns = 2;
			layout.makeColumnsEqualWidth = true;
			composite.setLayout(layout);
			
			// Invariant mirror section.
			InvariantMirrorSection invariantMirrorSection = new InvariantMirrorSection(this, composite, ExpandableComposite.TITLE_BAR |Section.EXPANDED, rodinFile);
			managedForm.addPart(invariantMirrorSection);
			gd = new GridData(SWT.FILL, SWT.FILL, true, true);
			gd.heightHint = 200;
			gd.minimumHeight = 150;
			gd.widthHint = 200;
			invariantMirrorSection.getSection().setLayoutData(gd);
			EventBFormPage page = (EventBFormPage) this.getEditor().findPage(InvariantsPage.PAGE_ID);
			page.addChangedListener(invariantMirrorSection);

			// Theorem mirror section.
			TheoremMirrorSection theoremMirrorSection = new TheoremMirrorSection(this, composite, ExpandableComposite.TITLE_BAR | Section.EXPANDED, rodinFile);
			managedForm.addPart(theoremMirrorSection);
			gd = new GridData(SWT.FILL, SWT.FILL, true, true);
			gd.heightHint = 200;
			gd.minimumHeight = 150;
			gd.widthHint = 200;
			theoremMirrorSection.getSection().setLayoutData(gd);
			page = (EventBFormPage) this.getEditor().findPage(TheoremsPage.PAGE_ID);
			page.addChangedListener(theoremMirrorSection);
		}	
	}
	
}
