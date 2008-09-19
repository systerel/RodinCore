package org.eventb.internal.ui.propertiesView;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IContextFile;
import org.eventb.core.IExtendsContext;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.editpage.ExtendsContextAbstractContextNameAttributeFactory;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

public class ExtendsContextSection extends CComboSection {

	@Override
	String getLabel() {
		return "Abs. Ctx.";
	}

	@Override
	String getText() throws RodinDBException {
		IExtendsContext eElement = (IExtendsContext) element;
		return eElement.getAbstractContextName();
	}

	@Override
	void setData() {
		final IRodinProject project = element.getRodinProject();
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
	void setText(String text, IProgressMonitor monitor) throws RodinDBException {
		UIUtils.setStringAttribute(element,
				new ExtendsContextAbstractContextNameAttributeFactory(), text,
				monitor);
	}

}
