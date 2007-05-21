package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.rodinp.core.IAttributedElement;

public abstract class DefaultEditComposite implements IEditComposite {

	ScrolledForm form;

	IAttributedElement element;

	Composite composite;
	
	String prefix;
	
	String postfix;

	Label prefixLabel;
	
	Label postfixLabel;
	
	boolean fillHorizontal = false;

	private FormToolkit toolkit;
	
	public void setForm(ScrolledForm form) {
		this.form = form;
	}

	protected FormToolkit getFormToolkit() {
		return toolkit;
	}
	
	public void refresh() {
		initialise();
		internalPack();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.editpage.IEditComposite#setElement(org.rodinp.core.IRodinElement)
	 */
	public void setElement(IAttributedElement element) {
		this.element = element;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.editpage.IEditComposite#createComposite(org.eclipse.ui.forms.widgets.FormToolkit,
	 *      org.eclipse.swt.widgets.Composite)
	 */
	public void createComposite(FormToolkit tk, Composite parent) {
		this.toolkit = tk;
		
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

		composite = toolkit.createComposite(parent);
		gridData = new GridData(SWT.FILL, SWT.TOP, fillHorizontal, false);
		gridData.minimumWidth = 200;
		composite.setLayoutData(gridData);
		composite.setLayout(new FillLayout());
		
		initialise();

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

	public abstract void initialise();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.editpage.IEditComposite#setFillHorizontal(boolean)
	 */
	public void setFillHorizontal(boolean fillHorizontal) {
		this.fillHorizontal = fillHorizontal;
	}

	void internalPack() {
		internalPack(composite);
	}

	public void setSelected(boolean selection) {
		if (selection) {
			postfixLabel.setBackground(postfixLabel.getDisplay()
					.getSystemColor(SWT.COLOR_GRAY));
			composite.setBackground(composite.getDisplay().getSystemColor(SWT.COLOR_GRAY));
			prefixLabel.setBackground(prefixLabel.getDisplay().getSystemColor(
					SWT.COLOR_GRAY));
		} else {
			if (EventBEditorUtils.DEBUG) {
				postfixLabel.setBackground(postfixLabel.getDisplay()
						.getSystemColor(SWT.COLOR_CYAN));
				composite.setBackground(composite.getDisplay().getSystemColor(
						SWT.COLOR_CYAN));
				prefixLabel.setBackground(prefixLabel.getDisplay()
						.getSystemColor(SWT.COLOR_CYAN));
			}
			else {
				postfixLabel.setBackground(postfixLabel.getDisplay()
						.getSystemColor(SWT.COLOR_WHITE));
				composite.setBackground(composite.getDisplay().getSystemColor(
						SWT.COLOR_WHITE));
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
			((Composite) c).layout(true);
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
