package org.eventb.internal.ui.eventbeditor;


import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eventb.internal.ui.EventBMath;

public class IntelligentNewVariableInputDialog extends Dialog {
	private String defaultName;
	private String defaultInvariantName;
	private String name;
	private String invariantName;
	private String invariantPredicate;
	private String init;
	private Text nameText;
	private Text invariantNameText;
	private EventBMath invariantPredicateText;
	private EventBMath initText;
	private ScrolledForm scrolledForm;
	private String title;
	private FormToolkit toolkit;
	
	public IntelligentNewVariableInputDialog(Shell parentShell, FormToolkit toolkit, String title, String defaultName, String defaultInvariantName) {
		super(parentShell);
		this.toolkit = toolkit;
		this.title = title;
		this.defaultName = defaultName;
		this.defaultInvariantName = defaultInvariantName;
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(title);
	}



	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID,
                IDialogConstants.OK_LABEL, true);

        createButton(parent, IDialogConstants.CANCEL_ID,
                IDialogConstants.CANCEL_LABEL, false);
	}

	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);

		
		scrolledForm = toolkit.createScrolledForm(composite);
		Composite body = scrolledForm.getBody();
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		body.setLayout(layout);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		scrolledForm.setLayoutData(gd);
		
		toolkit.createLabel(body, "Name");
		
		nameText = toolkit.createText(body, defaultName);
		gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		gd.horizontalSpan = 2;
		gd.widthHint = 200;
		nameText.setLayoutData(gd);
		
		toolkit.createLabel(body, "Invariant");
		
		invariantNameText = toolkit.createText(body, defaultInvariantName);
		gd = new GridData(SWT.FILL, SWT.NONE, false, false);
		gd.widthHint = 50;
		invariantNameText.setLayoutData(gd);
		
		invariantPredicateText = new EventBMath(toolkit.createText(body, ""));
		gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		gd.widthHint = 150;
		invariantPredicateText.getTextWidget().setLayoutData(gd);
		
		toolkit.createLabel(body, "Initialisation");
		
		initText = new EventBMath(toolkit.createText(body, ""));
		gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		gd.horizontalSpan = 2;
		gd.widthHint = 200;
		initText.getTextWidget().setLayoutData(gd);
		
		composite.pack();
		
		toolkit.paintBordersFor(body);
		applyDialogFont(body);
		return body;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
	 */
	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.CANCEL_ID) {
			name = null;
			invariantName = null;
			invariantPredicate = null;
			init = null;
        }
		else if (buttonId == IDialogConstants.OK_ID) {
			name = nameText.getText();
			invariantName = invariantNameText.getText();
			invariantPredicate = invariantPredicateText.getTextWidget().getText();
			init = initText.getTextWidget().getText();
		}
		super.buttonPressed(buttonId);
	}
	
	public String getName() {
		return name;
	}
	
	public String getInvariantName() {
		return invariantName;
	}
	
	public String getInvariantPredicate() {
		return invariantPredicate;
	}
	
	public String getInit() {
		return init;
	}
}
