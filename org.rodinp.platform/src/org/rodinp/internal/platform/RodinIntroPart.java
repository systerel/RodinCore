/*******************************************************************************
 * Copyright (c) 2006-2007 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.internal.platform;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.IntroPart;

/**
 * @author halstefa
 *
 */
public class RodinIntroPart extends IntroPart {

    private Composite container;

    /* (non-Javadoc)
	 * @see org.eclipse.ui.part.IntroPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
//		container = new Composite(parent, SWT.no);
//		container = new Canvas(parent, SWT.DEFAULT);
		container = parent;
		FillLayout layout = new FillLayout();
		layout.type = SWT.VERTICAL;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		container.setLayout(layout);
		Label label = new Label(container, SWT.CENTER);
		label.setSize(0, 10);
		label = new Label(container, SWT.CENTER);
		label.setFont(JFaceResources.getHeaderFont());
		label.setText("Project IST-511599");
		label = new Label(container, SWT.CENTER);
		label.setFont(JFaceResources.getHeaderFont());
		label.setText("RODIN");
		label = new Label(container, SWT.CENTER);
		label.setFont(JFaceResources.getHeaderFont());
		label.setText("Rigorous Open Development Environment for Complex Systems");
		label = new Label(container, SWT.CENTER);
		label.setFont(JFaceResources.getHeaderFont());
		label.setText("Project homepage: http://rodin.cs.ncl.ac.uk");
		label = new Label(container, SWT.CENTER);
		label.setFont(JFaceResources.getHeaderFont());
		label.setText("Sourceforge: http://sourceforge.net/projects/rodin-b-sharp");
		label = new Label(container, SWT.CENTER);
		label.setFont(JFaceResources.getHeaderFont());
		label.setText("D23 (D3.5) Internal version of basic tools and platform");
		label = new Label(container, SWT.CENTER);
		label.setFont(JFaceResources.getHeaderFont());
		label.setText("Copyright (c) 2005-2007 ETH Zurich and others");
		label = new Label(container, SWT.CENTER);
		label.setFont(JFaceResources.getHeaderFont());
		label.setText("This prototype has been developed with the support of the EU.");
		label = new Label(container, SWT.CENTER);
		label.setSize(0, 10);
		container.layout();
		container.setRedraw(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.IntroPart#setFocus()
	 */
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.intro.IIntroPart#standbyStateChanged(boolean)
	 */
	public void standbyStateChanged(boolean standby) {
		// TODO Auto-generated method stub

	}

}
