package org.eventb.internal.ui.eventbeditor.editpage;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IMachineFile;
import org.eventb.core.IRefinesMachine;
import org.rodinp.core.IAttributedElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

public class RefinesMachineEditComposite extends DefaultAttributeEditor
		implements IAttributeEditor {

	@Override
	public void setDefaultAttribute(IAttributedElement element,
			IProgressMonitor monitor) throws RodinDBException {
		IRefinesMachine refinesEvent = (IRefinesMachine) element;
		String name = "abstract_machine";
		refinesEvent.setAbstractMachineName(name, new NullProgressMonitor());
	}

	@Override
	public String getAttribute(IAttributedElement element,
			IProgressMonitor monitor) throws RodinDBException {
		IRefinesMachine refinesMachine = (IRefinesMachine) element;
		return refinesMachine.getAbstractMachineName();
	}

	@Override
	public void setAttribute(IAttributedElement element, String str,
			IProgressMonitor monitor) throws RodinDBException {
		assert element instanceof IRefinesMachine;

		IRefinesMachine refinesMachine = (IRefinesMachine) element;

		String value;
		try {
			value = getAttribute(element, monitor);
		} catch (RodinDBException e) {
			value = null;
		}
		if (value == null || !value.equals(str)) {
			refinesMachine.setAbstractMachineName(str,
					new NullProgressMonitor());
		}
	}

	@Override
	public String[] getPossibleValues(IAttributedElement element,
			IProgressMonitor monitor) {
		List<String> results = new ArrayList<String>();
		IRefinesMachine refinesMachine = (IRefinesMachine) element;
		IMachineFile machine = (IMachineFile) refinesMachine.getParent();
		String machineName = machine.getBareName();
		IRodinProject rodinProject = refinesMachine.getRodinProject();
		try {
			IMachineFile[] machineFiles = rodinProject
					.getChildrenOfType(IMachineFile.ELEMENT_TYPE);
			for (IMachineFile machineFile : machineFiles) {
				String bareName = machineFile.getBareName();
				if (!machineName.equals(bareName))
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
