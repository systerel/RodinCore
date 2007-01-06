package org.eventb.internal.ui.propertiesView;

import static org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants.HSPACE;
import static org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants.VSPACE;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.eventb.core.ICommentedElement;
import org.eventb.internal.ui.EventBMath;
import org.eventb.internal.ui.IEventBInputText;
import org.eventb.internal.ui.TimerText;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public class CommentSection extends AbstractPropertySection implements
		IElementChangedListener {

	Text commentText;
	
	IEventBInputText inputText;

	ICommentedElement element;

	public CommentSection() {
		// Nothing to do
	}

	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);

		final TabbedPropertySheetWidgetFactory wf = getWidgetFactory();
		Composite composite = wf.createFlatFormComposite(parent);

		commentText = wf.createText(composite, "", SWT.MULTI);
		FormData data = new FormData();
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, VSPACE);
		data.height = commentText.getLineHeight() * 3;
		commentText.setLayoutData(data);
		
		inputText = new EventBMath(commentText);
		new TimerText(commentText, 1000) {
			@Override
			protected void response() {
				try {
					element.setComment(commentText.getText(), new NullProgressMonitor());
				} catch (RodinDBException e) {
					UIUtils.log(e, "Error modifiying element "
							+ element.getElementName());
					if (UIUtils.DEBUG)
						e.printStackTrace();
				}
			}
		};

		CLabel labelLabel = wf.createCLabel(composite, "Comment:");
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(commentText, HSPACE);
		data.top = new FormAttachment(commentText, 0, SWT.CENTER);
		labelLabel.setLayoutData(data);
	}

	@Override
	public void refresh() {
		try {
			if (element.exists() && element.hasComment()) {
				commentText.setText(element.getComment());
			} else {
				commentText.setText("");
			}
		} catch (RodinDBException e) {
			commentText.setText("");
			if (UIUtils.DEBUG) e.printStackTrace();
		}
		super.refresh();
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		if (selection instanceof IStructuredSelection) {
			Object input = ((IStructuredSelection) selection).getFirstElement();
			if (input instanceof ICommentedElement) {
				this.element = (ICommentedElement) input;
			}
		}
	}

	public void elementChanged(ElementChangedEvent event) {
		// TODO Filter out the delta first
		if (commentText.isDisposed())
			return;
		Display display = commentText.getDisplay();
		display.syncExec(new Runnable() {
			public void run() {
				refresh();
			}
		});
	}

	@Override
	public void aboutToBeHidden() {
		super.aboutToBeHidden();
		// TODO Need to save if no editor is open for this element
		RodinCore.addElementChangedListener(this);
	}

	@Override
	public void aboutToBeShown() {
		super.aboutToBeShown();
		RodinCore.removeElementChangedListener(this);
	}

	@Override
	public void dispose() {
		super.dispose();
		if (inputText != null)
			inputText.dispose();
	}

	
}
