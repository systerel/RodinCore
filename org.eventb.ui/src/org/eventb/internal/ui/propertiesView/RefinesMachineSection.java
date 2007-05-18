package org.eventb.internal.ui.propertiesView;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.IMachineFile;
import org.eventb.core.IRefinesMachine;
import org.eventb.internal.ui.UIUtils;
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
		final IRodinProject project = editor.getRodinInput().getRodinProject();
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
	void setText(String text) throws RodinDBException {
		IRefinesMachine rElement = (IRefinesMachine) element;
		String abstractMachineName = null;
		try {
			rElement.getAbstractMachineName();
		}
		catch (RodinDBException e) {
			// Do nothing
		}
		if (abstractMachineName == null || !abstractMachineName.equals(text)) {
			rElement.setAbstractMachineName(text, new NullProgressMonitor());
		}
	}

}
