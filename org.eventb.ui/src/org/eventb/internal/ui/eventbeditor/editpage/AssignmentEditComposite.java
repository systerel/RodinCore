package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.IAssignmentElement;
import org.eventb.internal.ui.EventBUIExceptionHandler;
import org.rodinp.core.RodinDBException;

public class AssignmentEditComposite extends TextEditComposite {

	public void setValue() {
		assert element instanceof IAssignmentElement;
		final IAssignmentElement aElement = (IAssignmentElement) element;
		if (text == null)
			return;

		String str = text.getText();

		String value;
		try {
			value = getValue();
		} catch (RodinDBException e) {
			value = null;
		}

		if (value == null || !value.equals(str)) {
			try {
				aElement.setAssignmentString(str, null);
			} catch (RodinDBException e) {
				EventBUIExceptionHandler.handleSetAttributeException(e);
			}
		}
	}

	public String getValue() throws RodinDBException {
		assert element instanceof IAssignmentElement;
		final IAssignmentElement pElement = (IAssignmentElement) element;
		return pElement.getAssignmentString();
	}

	@Override
	public void setDefaultValue() {
		final IAssignmentElement aElement = (IAssignmentElement) element;
		try {
			aElement.setAssignmentString("", new NullProgressMonitor());
		} catch (RodinDBException e) {
			EventBUIExceptionHandler.handleSetAttributeException(e);
		}
		super.setDefaultValue();
	}

}
