package org.eventb.internal.ui.eventbeditor.editpage;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IContextFile;
import org.eventb.core.ISeesContext;
import org.rodinp.core.IAttributedElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

public class SeesContextEditor extends DefaultAttributeEditor implements
		IAttributeEditor {

	@Override
	public void setDefaultAttribute(IAttributedElement element,
			IProgressMonitor monitor) throws RodinDBException {
		ISeesContext seesContext = (ISeesContext) element;
		String name = "context";
		seesContext.setSeenContextName(name, monitor);
	}

	@Override
	public String getAttribute(IAttributedElement element,
			IProgressMonitor monitor) throws RodinDBException {
		ISeesContext seesContext = (ISeesContext) element;
		return seesContext.getSeenContextName();
	}

	@Override
	public void setAttribute(IAttributedElement element, String newValue,
			IProgressMonitor monitor) throws RodinDBException {
		assert element instanceof ISeesContext;

		ISeesContext seesContext = (ISeesContext) element;

		String value;
		try {
			value = getAttribute(element, monitor);
		} catch (RodinDBException e) {
			value = null;
		}
		if (value == null || !value.equals(newValue)) {
			seesContext.setSeenContextName(newValue, monitor);
		}
	}

	@Override
	public String[] getPossibleValues(IAttributedElement element,
			IProgressMonitor monitor) {
		List<String> results = new ArrayList<String>();
		ISeesContext seesContext = (ISeesContext) element;
		IRodinProject rodinProject = seesContext.getRodinProject();
		try {
			IContextFile[] contextFiles = rodinProject
					.getChildrenOfType(IContextFile.ELEMENT_TYPE);
			for (IContextFile contextFile : contextFiles) {
				String bareName = contextFile.getBareName();
				results.add(bareName);
			}
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results.toArray(new String[results.size()]);
	}

	@Override
	public void removeAttribute(IAttributedElement element,
			IProgressMonitor monitor) throws RodinDBException {
		element.removeAttribute(EventBAttributes.TARGET_ATTRIBUTE, monitor);
	}

}
