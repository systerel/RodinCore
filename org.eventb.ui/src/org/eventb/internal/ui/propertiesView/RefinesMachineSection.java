package org.eventb.internal.ui.propertiesView;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IMachineFile;
import org.eventb.core.IRefinesMachine;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.editpage.RefinesMachineAbstractMachineNameAttributeFactory;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

public class RefinesMachineSection extends CComboSection {

	@Override
	String getLabel() {
		return "Ref. Mch.";
	}

	@Override
	String getText() throws RodinDBException {
		IRefinesMachine rElement = (IRefinesMachine) element;
		return rElement.getAbstractMachineName();
	}

	@Override
	void setData() {
		final IRodinProject project = element.getRodinProject();
		final IMachineFile[] machines;
		try {
			machines = project.getChildrenOfType(IMachineFile.ELEMENT_TYPE);
		} catch (RodinDBException e) {
			UIUtils.log(e, "when listing the machines of " + project);
			return;
		}
		for (IMachineFile machine : machines) {
			final String bareName = machine.getComponentName();
			comboWidget.add(bareName);
		}
	}

	@Override
	void setText(String text, IProgressMonitor monitor) throws RodinDBException {
		UIUtils.setStringAttribute(element,
				new RefinesMachineAbstractMachineNameAttributeFactory(), text,
				monitor);
	}

}
