/**
 * 
 */
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
		label.setText("D15 (D3.4) Prototypes of basic tools and platform");
		label = new Label(container, SWT.CENTER);
		label.setFont(JFaceResources.getHeaderFont());
		label.setText("Copyright (c) 2005, 2006 ETH Zurich and others");
		label = new Label(container, SWT.CENTER);
		label.setFont(JFaceResources.getHeaderFont());
		label.setText("This research is being carried out as part of the EU funded research project: IST 511599 RODIN.");
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
