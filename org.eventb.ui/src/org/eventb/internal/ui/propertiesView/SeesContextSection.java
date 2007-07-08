package org.eventb.internal.ui.propertiesView;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IContextFile;
import org.eventb.core.ISeesContext;
import org.eventb.internal.ui.EventBUIExceptionHandler;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

public class SeesContextSection extends CComboSection {

	@Override
	String getLabel() {
		return "Abs. Ctx.";
	}

	@Override
	String getText() throws RodinDBException {
		ISeesContext sElement = (ISeesContext) element;
		return sElement.getSeenContextName();
	}

	@Override
	void setData() {
		final IRodinProject project = element.getRodinProject();
		final IContextFile[] contexts;
		try {
			contexts = project.getChildrenOfType(IContextFile.ELEMENT_TYPE);
		} catch (RodinDBException e) {
			EventBUIExceptionHandler.handleGetChildrenException(e);
			return;
		}
		for (IContextFile context : contexts) {
			final String bareName = context.getComponentName();
			comboWidget.add(bareName);
		}
	}

	@Override
	void setText(String text, IProgressMonitor monitor) throws RodinDBException {
		ISeesContext sElement = (ISeesContext) element;
		String seenContextName = null;
		try {
			sElement.getSeenContextName();
		}
		catch (RodinDBException e) {
			// Do nothing
		}
		if (seenContextName == null || !seenContextName.equals(text)) {
			sElement.setSeenContextName(text, monitor);
		}
	}

}
