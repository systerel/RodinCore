package org.eventb.internal.ui.propertiesView;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.IMachineFile;
import org.eventb.core.ISeesContext;
import org.rodinp.core.RodinDBException;

public class SeesContextSection extends AbstractContextSection<IMachineFile> {

	public SeesContextSection() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void refresh() {
		initContextCombo();
		super.refresh();
	}

	@Override
	public void setContext(String text) {
		assert element instanceof ISeesContext;
		ISeesContext sElement = (ISeesContext) element;
		try {
			if (!sElement.getSeenContextName().equals(text)) {
				sElement.setSeenContextName(text,
						new NullProgressMonitor());
			}
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void setInitialInput() {
		assert element instanceof ISeesContext;
		ISeesContext sElement = (ISeesContext) element;
		try {
			contextCombo.setText(sElement.getSeenContextName());
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
