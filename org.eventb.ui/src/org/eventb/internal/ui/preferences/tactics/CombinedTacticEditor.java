/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.preferences.tactics;

import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.internal.ui.preferences.AbstractEventBPreferencePage;

/**
 * @author Nicolas Beauger
 *
 */
public class CombinedTacticEditor extends AbstractTacticViewer<ITacticDescriptor> {

	private final CombinedTacticViewer combViewer = new CombinedTacticViewer();
	private Composite composite;
	private ListViewer simpleList;
	private ListViewer combList;
	private ListViewer refList;

	@Override
	protected Control getControl() {
		return composite;
	}

	@Override
	public void createContents(Composite parent) {
		composite = createGrid(parent, 3);
		final Group simpleGroup = new Group(composite, SWT.NONE);
		simpleGroup.setText("Tactics");
		simpleGroup.setLayout(new GridLayout());
		simpleList = new ListViewer(simpleGroup);
		simpleList.add("simple");
		combViewer.createContents(composite);
		final Composite combAndRef = createGrid(composite, 1);
		final Group combGroup = new Group(combAndRef, SWT.NONE);
		combGroup.setText("Combinators");
		combGroup.setLayout(new GridLayout());
		combList = new ListViewer(combGroup);
		combList.add("combined");
		final Group refGroup = new Group(combAndRef, SWT.NONE);
		refGroup.setText("Profiles");
		refGroup.setLayout(new GridLayout());
		refList = new ListViewer(refGroup);
		refList.add("profiles");
	}
	
	private static Composite createGrid(Composite parent, int numColumns) {
		final Composite composite = new Composite(parent, SWT.NONE);
		final GridLayout compLayout = new GridLayout();
		compLayout.numColumns = numColumns;
		composite.setLayout(compLayout);
		AbstractEventBPreferencePage.setFillParent(composite);
		return composite;
	}

	@Override
	public void setInput(ITacticDescriptor desc) {
		// TODO Auto-generated method stub
//		simpleList.getControl().pack();
//		combList.getControl().pack();
//		refList.getControl().pack();
//		combAndRef.pack();
//		composite.pack();
//		parent.pack();
		
	}

	@Override
	public ITacticDescriptor getInput() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITacticDescriptor getEditResult() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
