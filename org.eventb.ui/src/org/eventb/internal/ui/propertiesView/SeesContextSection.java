package org.eventb.internal.ui.propertiesView;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.IContextFile;
import org.eventb.core.ISeesContext;
import org.eventb.internal.ui.UIUtils;
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
		final IRodinProject project = editor.getRodinInput().getRodinProject();
		final IContextFile[] contexts;
		try {
			contexts = project.getChildrenOfType(IContextFile.ELEMENT_TYPE);
		} catch (RodinDBException e) {
			UIUtils.log(e, "when listing the contexts of " + project);
			return;
		}
		for (IContextFile context : contexts) {
			final String bareName = context.getComponentName();
			comboWidget.add(bareName);
		}
	}

	@Override
	void setText(String text) throws RodinDBException {
		ISeesContext sElement = (ISeesContext) element;
		if (!sElement.getSeenContextName().equals(text)) {
			sElement.setSeenContextName(text, new NullProgressMonitor());
		}
	}

}
