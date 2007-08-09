package org.eventb.internal.ui.eventbeditor.editpage;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IContextFile;
import org.eventb.core.IExtendsContext;
import org.eventb.core.IRefinesMachine;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IAttributedElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

public class ExtendsContextAbstractContextNameAttributeFactory implements
		IAttributeFactory {

	public void setDefaultValue(IEventBEditor<?> editor,
			IAttributedElement element, IProgressMonitor monitor)
			throws RodinDBException {
		IRefinesMachine refinesEvent = (IRefinesMachine) element;
		String name = "abstract_context";
		refinesEvent.setAbstractMachineName(name, new NullProgressMonitor());
	}

	public String getValue(IAttributedElement element, IProgressMonitor monitor)
			throws RodinDBException {
		IExtendsContext extendsContext = (IExtendsContext) element;
		return extendsContext.getAbstractContextName();
	}

	public void setValue(IAttributedElement element, String str,
			IProgressMonitor monitor) throws RodinDBException {
		assert element instanceof IExtendsContext;

		IExtendsContext extendsContext = (IExtendsContext) element;

		String value;
		try {
			value = getValue(element, monitor);
		} catch (RodinDBException e) {
			value = null;
		}
		if (value == null || !value.equals(str)) {
			extendsContext.setAbstractContextName(str,
					new NullProgressMonitor());
		}
	}

	public String[] getPossibleValues(IAttributedElement element,
			IProgressMonitor monitor) throws RodinDBException {
		List<String> results = new ArrayList<String>();
		IExtendsContext extendsContext = (IExtendsContext) element;
		IRodinProject rodinProject = extendsContext.getRodinProject();
		IContextFile[] contextFiles = rodinProject
				.getChildrenOfType(IContextFile.ELEMENT_TYPE);
		IContextFile context = (IContextFile) extendsContext.getParent();
		String contextName = context.getBareName();

		for (IContextFile contextFile : contextFiles) {
			String bareName = contextFile.getBareName();
			if (!contextName.equals(bareName))
				results.add(bareName);
		}
		return results.toArray(new String[results.size()]);
	}

	public void removeAttribute(IAttributedElement element,
			IProgressMonitor monitor) throws RodinDBException {
		element.removeAttribute(EventBAttributes.TARGET_ATTRIBUTE, monitor);
	}

}
