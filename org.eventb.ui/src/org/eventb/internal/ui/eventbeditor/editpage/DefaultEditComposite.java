package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.rodinp.core.IRodinElement;

public abstract class DefaultEditComposite implements IEditComposite {

	ScrolledForm form;

	IRodinElement element;

	Control control;
	
	String prefix;
	
	String postfix;

	Label prefixLabel;
	
	Label postfixLabel;
	
	boolean fillHorizontal = false;
	
	public void setForm(ScrolledForm form) {
		this.form = form;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.editpage.IEditComposite#setElement(org.rodinp.core.IRodinElement)
	 */
	public void setElement(IRodinElement element) {
		this.element = element;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.editpage.IEditComposite#createComposite(org.eclipse.ui.forms.widgets.FormToolkit,
	 *      org.eclipse.swt.widgets.Composite)
	 */
	public void createComposite(FormToolkit toolkit, Composite parent) {
		if (prefix == null)
			prefix = "";
		prefixLabel = toolkit.createLabel(parent, prefix);
		GridData gridData = new GridData();
		gridData.verticalAlignment = SWT.TOP;
		if (prefix == null)
			gridData.widthHint = 0;
		
		prefixLabel.setLayoutData(gridData);
		if (EventBEditorUtils.DEBUG)
			prefixLabel.setBackground(prefixLabel.getDisplay().getSystemColor(
					SWT.COLOR_CYAN));

		createMainComposite(toolkit, parent);
		gridData = new GridData(SWT.FILL, SWT.TOP, fillHorizontal, false);
		control.setLayoutData(gridData);

		postfixLabel = toolkit.createLabel(parent, " " + postfix + " ");
		gridData = new GridData();
		gridData.verticalAlignment = SWT.TOP;
		if (postfix == null)
			gridData.widthHint = 0;
		postfixLabel.setLayoutData(gridData);
		if (EventBEditorUtils.DEBUG)
			postfixLabel.setBackground(postfixLabel.getDisplay()
					.getSystemColor(SWT.COLOR_CYAN));
	}

	public abstract void createMainComposite(FormToolkit toolkit, Composite parent);
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.editpage.IEditComposite#setFillHorizontal(boolean)
	 */
	public void setFillHorizontal(boolean fillHorizontal) {
		this.fillHorizontal = fillHorizontal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.editpage.IEditComposite#setControl(org.eclipse.swt.widgets.Control)
	 */
	public void setControl(Control control) {
		this.control = control;
	}

	void internalPack() {
		internalPack(control);
	}

	public void setSelected(boolean selection) {
		if (selection) {
			postfixLabel.setBackground(postfixLabel.getDisplay()
					.getSystemColor(SWT.COLOR_GRAY));
			prefixLabel.setBackground(prefixLabel.getDisplay().getSystemColor(
					SWT.COLOR_GRAY));
		} else {
			if (EventBEditorUtils.DEBUG) {
				postfixLabel.setBackground(postfixLabel.getDisplay()
						.getSystemColor(SWT.COLOR_CYAN));
				prefixLabel.setBackground(prefixLabel.getDisplay()
						.getSystemColor(SWT.COLOR_CYAN));
			}
			else {
				postfixLabel.setBackground(postfixLabel.getDisplay()
						.getSystemColor(SWT.COLOR_WHITE));
				prefixLabel.setBackground(prefixLabel.getDisplay()
						.getSystemColor(SWT.COLOR_WHITE));				
			}
		}

	}

	private void internalPack(Control c) {
		if (c.equals(form.getBody())) {
			if (EventBEditorUtils.DEBUG)
				EventBEditorUtils.debug("Full resize");
			form.reflow(true);			
		}
		Rectangle bounds = c.getBounds();
		Point preferredSize = c.computeSize(SWT.DEFAULT, SWT.DEFAULT);

		if (preferredSize.x > bounds.width || preferredSize.y > bounds.height) {
			internalPack(c.getParent());
		} else {
			c.pack();
			c.setBounds(bounds);
		}
	}

	public void setPostfix(String postfix) {
		this.postfix = postfix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

}
